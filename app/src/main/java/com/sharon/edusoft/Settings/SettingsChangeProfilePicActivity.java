package com.sharon.edusoft.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sharon.edusoft.R;
import com.sharon.edusoft.SetupAccount.SetupAccountImageActivity;
import com.sharon.edusoft.StudDashboard;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class SettingsChangeProfilePicActivity extends AppCompatActivity {
    private Button saveProfileInfor;
    private EditText editName,editUsername,editBio,editEmail;
    private CircleImageView settingschangeprofilepicCIV;
    private ImageView imageButton2;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private String user_id,name,bio,username,email,profileImage;
    private Uri profileImageUri;
    private Bitmap mCompressedProfileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_change_profile_pic);

        settingschangeprofilepicCIV=findViewById(R.id.settingschangeprofilepicCIV);
        saveProfileInfor=findViewById(R.id.saveProfileInfor);
        editName=findViewById(R.id.editName);
        editUsername=findViewById(R.id.editUsername);
        editBio=findViewById(R.id.editBio);
        editEmail=findViewById(R.id.editEmail);
        imageButton2=findViewById(R.id.imageButton2);




        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

           saveProfileInfor.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   saveProfileInformation();
               }
           });
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        settingschangeprofilepicCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(SettingsChangeProfilePicActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .setAspectRatio(1, 1)
                                        .start(SettingsChangeProfilePicActivity.this);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                                PermissionListener dialogPermissionListener =
                                        DialogOnDeniedPermissionListener.Builder
                                                .withContext(SettingsChangeProfilePicActivity.this)
                                                .withTitle("Storage permission")
                                                .withMessage("Storage permission is needed to choose a picture")
                                                .withButtonText(android.R.string.ok)
                                                .withIcon(R.mipmap.ic_launcher)
                                                .build();
                                dialogPermissionListener.onPermissionDenied(response);

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .check();
            }
        });

    getUserProfile();
    }

    private void saveProfileInformation() {
        name=editName.getText().toString();
        bio=editBio.getText().toString();
        username=editUsername.getText().toString();
        email=editEmail.getText().toString();

            final Dialog dialog = new Dialog(SettingsChangeProfilePicActivity.this);
            dialog.setContentView(R.layout.loading_dialog_layout);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            File mFileProfileImage = new File(profileImageUri.getPath());

            try {
                mCompressedProfileImage = new Compressor(SettingsChangeProfilePicActivity.this).setQuality(15).compressToBitmap(mFileProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream mProfileBAOS = new ByteArrayOutputStream();
            mCompressedProfileImage.compress(Bitmap.CompressFormat.JPEG, 15, mProfileBAOS);
            byte[] mProfileData = mProfileBAOS.toByteArray();

            final StorageReference mChildRefProfile = storageReference.child("users/profiles/profile_images/" + currentUser.getUid() + ".jpg");

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
                                Uri downloadUri = task.getResult();

                                HashMap<String, Object> mDataMap = new HashMap<>();
                                mDataMap.put("user_id", user_id);
                                mDataMap.put("profile_image", downloadUri.toString());
                                mDataMap.put("name", name);
                                mDataMap.put("bio", bio);
                                mDataMap.put("username", username);
                                mDataMap.put("email", email);


                                mDatabase.child("RegisteredUsers").child(user_id).updateChildren(mDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            onBackPressed();
                                            dialog.cancel();
                                        } else {
                                            dialog.cancel();
                                            Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), "UriTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "UploadTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

    }

    private void getUserProfile() {
        mDatabase.child("RegisteredUsers").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    name = snapshot.child("name").getValue().toString();
                    bio=snapshot.child("bio").getValue().toString();
                    username=snapshot.child("username").getValue().toString();
                    email=snapshot.child("email").getValue().toString();
                    profileImage=snapshot.child("profile_image").getValue().toString();

                    editName.setText(name);
                    editBio.setText(bio);
                    editEmail.setText(email);
                    editUsername.setText(username);
                    Picasso.get().load(profileImage).into(settingschangeprofilepicCIV);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImageUri = result.getUri();
                Picasso.get().load(profileImageUri).into(settingschangeprofilepicCIV);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_LONG).show();
            }
        }
    }

}
