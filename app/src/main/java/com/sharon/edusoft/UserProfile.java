package com.sharon.edusoft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharon.edusoft.SetupAccount.RegisteredUsers;
import com.sharon.edusoft.Video.registration;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private CircleImageView setupImageCIV;
    private TextView bio, name, email;
    private ImageView imageButton2;
    private String user_id;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mContext=UserProfile.this;
        setupImageCIV = findViewById(R.id.setupImageCIV);
        imageButton2=findViewById(R.id.imageButton2);
        bio = findViewById(R.id.bio);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("registration").child(currentUser.getUid());
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        user_id = currentUser.getUid();
        getUserInfo();
    }


    private void getUserInfo() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("RegisteredUsers").child(user_id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RegisteredUsers users = snapshot.getValue(RegisteredUsers.class);
                    if (users!=null){
                        name.setText(users.getName());
                        bio.setText(users.getBio());
                        email.setText(users.getEmail());
                        String profpic=users.getProfile_image();
                        Glide.with(mContext).load(profpic).into(setupImageCIV);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
