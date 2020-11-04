package com.sharon.edusoft.Channel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sharon.edusoft.R;
import com.sharon.edusoft.Utils.Variables;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class CreateChannelActivity extends AppCompatActivity {

    private TextView CreateChannel;
    private ImageView ivCreateChannelProfilePic;
    private CircleImageView civCreateChannelProfilePic;
    private EditText etCreateChannelDialogChannelName, etCreateChannelDialogAbout;
    private Button bCreateChannel, bCreateChannelDialog;
    private ProgressBar pbCreateChannelDialogProfileIndicator;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id, channel_profile_image, channel_profile_pic, channel_id, channel_name, channel_description;
    private Uri channelProfilePicuri;
    private Bitmap mCompressedProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        CreateChannel = findViewById(R.id.CreateChannel);
        bCreateChannel = findViewById(R.id.bCreateChannel);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        if (currentUser != null) {
            user_id = currentUser.getUid();

            mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChild("users")) {
                            String createChannel = dataSnapshot.child("users").getValue().toString();
                            Picasso.get().load(createChannel).placeholder(R.drawable.default_profile_pic).into(civCreateChannelProfilePic);

                        }
                        String email = dataSnapshot.child("users").child("email").getValue(String.class);
                        CreateChannel.setText("Your channel will create under this account " + email);
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            bCreateChannel.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    channel_id = mDatabase.child("users").child(user_id).child("channels").push().getKey();

                    Dialog dialog = new Dialog(CreateChannelActivity.this);
                    dialog.setContentView(R.layout.dialog_create_channel);
                    dialog.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    dialog.setCanceledOnTouchOutside(false);

                    ivCreateChannelProfilePic = dialog.findViewById(R.id.ivCreateChannelProfilePic);
                    civCreateChannelProfilePic = dialog.findViewById(R.id.civCreateChannelProfilePic);
                    bCreateChannelDialog = dialog.findViewById(R.id.bCreateChannelDialog);
                    etCreateChannelDialogChannelName = dialog.findViewById(R.id.etCreateChannelDialogChannelName);
                    pbCreateChannelDialogProfileIndicator = dialog.findViewById(R.id.pbCreateChannelDialogProfileIndicator);
                    etCreateChannelDialogAbout = dialog.findViewById(R.id.etCreateChannelDialogAbout);

                    pbCreateChannelDialogProfileIndicator.setVisibility(View.GONE);

                    civCreateChannelProfilePic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CropImage.activity()
                                    .setAspectRatio(1, 1)
                                    .setMinCropWindowSize(512, 512)
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setOutputUri(channelProfilePicuri)
                                    .start(CreateChannelActivity.this);
                        }
                    });

                    bCreateChannelDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            channel_name = etCreateChannelDialogChannelName.getText().toString();
                            channel_description = etCreateChannelDialogAbout.getText().toString();

                            if (channel_name.isEmpty()) {
                                etCreateChannelDialogChannelName.setError("Enter your Channel Name");
                            } else {
                                if (channelProfilePicuri == null) {
                                    createChannelWithoutProfilePic();
                                } else {
                                    createChannelWithProfilePic(channelProfilePicuri);
                                }
                            }
                        }
                    });


                    dialog.show();

                }
            });
        }

    }

    private void sendToChannel(String channel_id) {
        Intent channelIntent = new Intent(getApplicationContext(), ChannelActivity.class);
        channelIntent.putExtra("channel_id", channel_id);
        channelIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(channelIntent);
        finish();
        Toast.makeText(getApplicationContext(), "Channel successfully created", Toast.LENGTH_LONG).show();
    }

    private void createChannelWithProfilePic(Uri channelProfilePicuri) {
        if (channelProfilePicuri != null) {
            pbCreateChannelDialogProfileIndicator.setVisibility(View.VISIBLE);
            File mFileProfileImage = new File(channelProfilePicuri.getPath());

            try {
                mCompressedProfileImage = new Compressor(CreateChannelActivity.this).setQuality(15).compressToBitmap(mFileProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream mProfileBAOS = new ByteArrayOutputStream();
            mCompressedProfileImage.compress(Bitmap.CompressFormat.JPEG, 15, mProfileBAOS);
            byte[] mProfileData = mProfileBAOS.toByteArray();

            final StorageReference mChildRefProfile = storageReference.child("channels/profile_images/" + channel_id + ".jpg");


            final UploadTask profile_uploadTask = mChildRefProfile.putBytes(mProfileData);

            profile_uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Task<Uri> uriTask = profile_uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return mChildRefProfile.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                pbCreateChannelDialogProfileIndicator.setVisibility(View.GONE);
                                Uri downloadUri = task.getResult();
                                channel_profile_image = downloadUri.toString();

                                final HashMap<String, Object> mChannelDataMap = new HashMap<>();
                                mChannelDataMap.put("channel_id", channel_id);
                                mChannelDataMap.put("timestamp", System.currentTimeMillis());
                                mChannelDataMap.put("user_id", user_id);
                                mChannelDataMap.put("channel_name", channel_name);
                                mChannelDataMap.put("channel_profile_pic", channel_profile_image);
                                mChannelDataMap.put("channel_description", channel_description);
                                mChannelDataMap.put("channel_banner", "");
                                mChannelDataMap.put("channel_email", currentUser.getEmail());
                                mDatabase.child("users").child(user_id).child("channels").child(channel_id).setValue(mChannelDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mDatabase.child("users").child(user_id).child("hasChannel").setValue(true);
                                            mDatabase.child("channels").child(channel_id).setValue(mChannelDataMap);
                                            sendToChannel(channel_id);
                                        }
                                    }
                                });
                            } else {
                                pbCreateChannelDialogProfileIndicator.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pbCreateChannelDialogProfileIndicator.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "UriTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pbCreateChannelDialogProfileIndicator.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "UploadTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    pbCreateChannelDialogProfileIndicator.setIndeterminate(false);
                    int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    pbCreateChannelDialogProfileIndicator.setProgress(progress);
                }
            });


        }
    }

    private void createChannelWithoutProfilePic() {
        final HashMap<String, Object> mChannelDataMap = new HashMap<>();
        mChannelDataMap.put("channel_id", channel_id);
        mChannelDataMap.put("timestamp", System.currentTimeMillis());
        mChannelDataMap.put("user_id", user_id);
        mChannelDataMap.put("channel_name", channel_name);
        mChannelDataMap.put("channel_profile_pic", "");
        mChannelDataMap.put("channel_description", channel_description);
        mChannelDataMap.put("channel_banner", "");
        mChannelDataMap.put("channel_email", currentUser.getEmail());
        mDatabase.child("users").child(user_id).child("channels").child(channel_id).setValue(mChannelDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDatabase.child("users").child(user_id).child("hasChannel").setValue(true);
                    mDatabase.child("channels").child(channel_id).setValue(mChannelDataMap);
                    sendToChannel(channel_id);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                channelProfilePicuri = result.getUri();

                Picasso.get().load(channelProfilePicuri).into(civCreateChannelProfilePic);
                ivCreateChannelProfilePic.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
