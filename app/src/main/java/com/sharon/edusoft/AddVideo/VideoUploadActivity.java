package com.sharon.edusoft.AddVideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.sharon.edusoft.LoginActivity;
import com.sharon.edusoft.MyVideos.MyVideosActivity;
import com.sharon.edusoft.R;
import com.github.tcking.giraffecompressor.GiraffeCompressor;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;


public class VideoUploadActivity extends AppCompatActivity {

    private TextView UploadVideoMessage,pause,cancel;
    private ProgressBar pbVideoUpload;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    private Uri videoUri, videoThumbnailUri;
    private String videoTitle, videoDesc, videoCategory;
    private Bitmap mCompressedVideoThumbnail;
    private int videoHeight, videoWidth;
    private long videoDuration;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        mContext = VideoUploadActivity.this;

        GiraffeCompressor.init(mContext);

        Bundle bundle = getIntent().getExtras();
        videoUri = Uri.parse(bundle.getString("videoUri"));
        videoThumbnailUri = Uri.parse(bundle.getString("videoThumbnailUri"));
        videoTitle = bundle.get("videoTitle").toString();
        videoDesc = bundle.get("videoDesc").toString();
        videoCategory = bundle.get("videoCategory").toString();
        videoHeight = bundle.getInt("videoHeight");
        videoWidth = bundle.getInt("videoWidth");
        videoDuration = bundle.getLong("videoDuration");

        UploadVideoMessage = findViewById(R.id.uploadVideoMessage);
        pause=findViewById(R.id.pause);
        cancel=findViewById(R.id.cancel);
        pbVideoUpload = findViewById(R.id.pbVideoUpload);

        pbVideoUpload.setIndeterminate(false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        if (currentUser == null) {
            sendToLogin();
        } else {
            user_id = currentUser.getUid();

            uploadVideo();
        }
    }

    private void uploadVideo() {
        if (videoUri == null) {
            Toast.makeText(getApplicationContext(), "Video missing", Toast.LENGTH_SHORT).show();
        } else if (videoThumbnailUri == null) {
            Toast.makeText(getApplicationContext(), "Video thumbnail is missing", Toast.LENGTH_SHORT).show();
        } else {
            String videoFileName = String.valueOf(System.currentTimeMillis()/1000);
            final String videoId = mDatabase.child("videos").push().getKey();
            final String[] videoThumbnailDownloadUrl = {null};

            if (!videoThumbnailUri.toString().equals("")) {

                final StorageReference mChildRefVideoThumbnail = storageReference.child("videos/" + videoId + "/videoThumbnail/" + videoId + ".jpg");

                final UploadTask videoThumbnail_uploadTask = mChildRefVideoThumbnail.putFile(videoThumbnailUri);

                videoThumbnail_uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Task<Uri> uriTask = videoThumbnail_uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                return mChildRefVideoThumbnail.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    videoThumbnailDownloadUrl[0] = downloadUri.toString();

                                    mDatabase.child("videos").child(videoId).child("videoThumbnail").setValue(downloadUri.toString());
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "UriTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "UploadTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }

            File mFileVideo = new File(videoUri.getPath());
            final StorageReference mChildRefVideoThumbnail = storageReference.child("videos/" + videoId + "/" + videoId + ".mp4");

            final UploadTask videoThumbnail_uploadTask = mChildRefVideoThumbnail.putFile(videoUri);

            videoThumbnail_uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Task<Uri> uriTask = videoThumbnail_uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return mChildRefVideoThumbnail.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                videoThumbnailDownloadUrl[0] = downloadUri.toString();

                                HashMap<String, Object> mVideoThumbnailDataMap = new HashMap<>();
                                mVideoThumbnailDataMap.put("video_id", videoId);
                                mVideoThumbnailDataMap.put("user_id", user_id);
                                mVideoThumbnailDataMap.put("video", downloadUri.toString());
                                mVideoThumbnailDataMap.put("timestamp", System.currentTimeMillis());
                                mVideoThumbnailDataMap.put("videoTitle", videoTitle);
                                mVideoThumbnailDataMap.put("videoHeight", videoHeight);
                                mVideoThumbnailDataMap.put("videoWidth", videoWidth);
                                mVideoThumbnailDataMap.put("videoDuration", videoDuration);
                                mVideoThumbnailDataMap.put("videoCategory", videoCategory);
                                mVideoThumbnailDataMap.put("videoDescription", videoDesc);


                                mDatabase.child("videos").child(videoId).updateChildren(mVideoThumbnailDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent myUploadsIntent = new Intent(VideoUploadActivity.this, MyVideosActivity.class);;
                                            startActivity(myUploadsIntent);
                                            finish();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "UriTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    pbVideoUpload.setIndeterminate(false);
                    int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    pbVideoUpload.setProgress(progress);
                    UploadVideoMessage.setText("Uploading Video... " + String.valueOf(progress) + "%");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "UploadTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });


        }


    }


    private void sendToLogin() {
        Intent loginIntent = new Intent(VideoUploadActivity.this, LoginActivity.class);;
        startActivity(loginIntent);
        finish();
    }
}
