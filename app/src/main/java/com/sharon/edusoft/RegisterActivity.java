package com.sharon.edusoft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharon.edusoft.SetupAccount.SetupAccountImageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity{

    private EditText etRegisterEmail, etRegisterPassword, name;
    private Button registerbutton;
    private ProgressBar loginPB;

    private String Email, password, Name,user_id;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        name = findViewById(R.id.name);
        registerbutton = findViewById(R.id.registerbutton);
        loginPB = findViewById(R.id.loginPB);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        loginPB.setVisibility(View.GONE);

        if (currentUser != null) {
            sendToMain();
        }

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


    }

    private void registerUser() {
        Email = etRegisterEmail.getText().toString();
        password = etRegisterPassword.getText().toString();
        Name=name.getText().toString();
        loginPB.setVisibility(View.VISIBLE);
        registerbutton.setVisibility(View.GONE);

        if (Email.isEmpty()) {
            loginPB.setVisibility(View.GONE);
            registerbutton.setVisibility(View.VISIBLE);
            etRegisterEmail.setError("Please enter your email");
        } else if (password.isEmpty()) {
            loginPB.setVisibility(View.GONE);
            registerbutton.setVisibility(View.VISIBLE);
            etRegisterPassword.setError("Please enter your password");
        } else if (Name.isEmpty()) {
            loginPB.setVisibility(View.GONE);
            registerbutton.setVisibility(View.VISIBLE);
            name.setError("Please enter your name");
        } else if(password.length()<8){
            etRegisterPassword.setError("Password should be more than 8 characters");
        }
        else {
            mAuth.createUserWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String Name=name.getText().toString();
                        String Email=etRegisterEmail.getText().toString();

                        HashMap<String, Object> mDataMap = new HashMap<>();
                        mDataMap.put("Email", Email);
                        mDataMap.put("Name", Name);
                        mDataMap.put("user_id", task.getResult().getUser().getUid());

                        mDatabase.child("registration").child(task.getResult().getUser().getUid()).setValue(mDataMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        loginPB.setVisibility(View.GONE);
                                        registerbutton.setVisibility(View.VISIBLE);
                                        Intent intent=new Intent(RegisterActivity.this, SetupAccountImageActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                    }
                                }

                        );
                    }
                    else {
                        loginPB.setVisibility(View.GONE);
                        registerbutton.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loginPB.setVisibility(View.GONE);
                    registerbutton.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

}
 