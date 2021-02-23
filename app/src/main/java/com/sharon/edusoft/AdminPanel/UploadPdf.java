package com.sharon.edusoft.AdminPanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sharon.edusoft.R;

import java.util.HashMap;

public class UploadPdf extends AppCompatActivity{
    private Toolbar uploadtoolbar;
    TextView textViewStatus;
    EditText editTextFilename;
    ProgressBar progressBar;
    ImageView cancelfile,fileLogo,imageBrowse;
    private final int PICK_PDF_CODE=2342;
    private Button buttonUploadFile;
    Uri filepath;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpdf);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        uploadtoolbar = findViewById(R.id.uploadtoolbar);
        uploadtoolbar.setTitle("Choose Pdf");
        setSupportActionBar(uploadtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        uploadtoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        uploadtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");
        mDatabase=FirebaseDatabase.getInstance().getReference();
        //getting the view
        buttonUploadFile=findViewById(R.id.buttonUploadFile);
        editTextFilename = findViewById(R.id.editTextFileName);
        cancelfile=findViewById(R.id.cancelfile);
        fileLogo=findViewById(R.id.fileLogo);
        imageBrowse=findViewById(R.id.imagebrowse);
        progressBar = findViewById(R.id.progressbar);
        fileLogo.setVisibility(View.INVISIBLE);
        cancelfile.setVisibility(View.INVISIBLE);
        cancelfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileLogo.setVisibility(View.INVISIBLE);
                cancelfile.setVisibility(View.INVISIBLE);
                imageBrowse.setVisibility(View.VISIBLE);
            }
        });
 imageBrowse.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         Intent intent = new Intent();
         intent.setType("application/pdf");
         intent.setAction(Intent.ACTION_GET_CONTENT);//specify what kind of action you want intent-receiving app to do
         startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_CODE);
     }
 });
        buttonUploadFile.setOnClickListener(new View.OnClickListener() {@Override
        public void onClick(View v) {
               processupload(filepath);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK &&data!=null&& data.getData()!=null) {
            filepath=data.getData();
            fileLogo.setVisibility(View.VISIBLE);
            cancelfile.setVisibility(View.VISIBLE);
            imageBrowse.setVisibility(View.INVISIBLE);
        }
    }

    private void processupload(Uri filepath) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File loading.....");
        progressDialog.show();
        final StorageReference sRef = storageReference.child("upload" + System.currentTimeMillis() + ".Pdf");
        final UploadTask selectPdf = sRef.putFile(filepath);
        selectPdf.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Task<Uri> uriTask=selectPdf.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return sRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()){
                            Uri downloadUri = task.getResult();
                            String filename=editTextFilename.getText().toString();
                            String pdfId= mDatabase.child("UploadedPdf").push().getKey();
                            HashMap<String, Object> mVideoThumbnailDataMap = new HashMap<>();
                            mVideoThumbnailDataMap.put("id",pdfId);
                            mVideoThumbnailDataMap.put("name",filename);
                            mVideoThumbnailDataMap.put("url",downloadUri.toString());
                            mDatabase.child("UploadedPdf").child(pdfId).updateChildren(mVideoThumbnailDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent myUploadsIntent = new Intent(UploadPdf.this, AdminHome.class);
                                        startActivity(myUploadsIntent);
                                        finish();
                                    } }});
                        }else {
                            Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                        } }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "UriTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setMessage("Uploading..."+(int)progress+"%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "UploadTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }}