package com.sharon.edusoft;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharon.edusoft.Profile.ProfileFragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText etLoginEmail, etLoginPassword;
    private TextView reg,fpass,usertype;
    private Spinner spinner2;
    private Button loginbutton;
    private ProgressBar loginPB;


    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String email, password,userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        loginbutton = findViewById(R.id.loginbutton);
        reg=findViewById(R.id.reg);
        fpass=findViewById(R.id.fpass);
        loginPB = findViewById(R.id.loginPB);
        spinner2=findViewById(R.id.spinner2);
        usertype=findViewById(R.id.usertype);



        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        loginPB.setVisibility(View.GONE);

        Spinner spinner2=findViewById(R.id.spinner2);

        spinner2.setOnItemSelectedListener(this);
        List<String> categoriesList=new ArrayList<>();
        categoriesList.add("Producer");
        categoriesList.add("Student");

        ArrayAdapter<String> categoriesDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categoriesList);
        spinner2.setAdapter(categoriesDataAdapter);




        if (currentUser != null) {
            sendToMain();
        }

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
       fpass.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
               startActivity(i);
           }
       });
       reg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
              startActivity(i);
           }
       });

    }

    private void loginUser() {
        loginPB.setVisibility(View.VISIBLE);
        loginbutton.setVisibility(View.GONE);
        email = etLoginEmail.getText().toString();
        password = etLoginPassword.getText().toString();

        if (email.isEmpty()) {
            loginPB.setVisibility(View.GONE);
            loginbutton.setVisibility(View.VISIBLE);
            etLoginEmail.setError("Please enter your email");
        } else if (password.isEmpty()) {
            loginPB.setVisibility(View.GONE);
            loginbutton.setVisibility(View.VISIBLE);
            etLoginPassword.setError("Please enter your password");
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String login_id = mDatabase.child("users").child(task.getResult().getUser().getUid()).push().getKey();
                        String user_id = task.getResult().getUser().getUid();
                        try {
                            PackageInfo pInfo = LoginActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);

                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }


                        HashMap<String, Object> mLoginInfoDataMap = new HashMap<>();
                        mLoginInfoDataMap.put("login_id", login_id);
                        mLoginInfoDataMap.put("user_id", user_id);
                        mLoginInfoDataMap.put("Role",userType);
                        mLoginInfoDataMap.put("timestamp", System.currentTimeMillis());


                        mDatabase.child("users").child(user_id).child("login").child(login_id).updateChildren(mLoginInfoDataMap);
                        sendToMain();
                        loginPB.setVisibility(View.GONE);


                    } else {
                        loginPB.setVisibility(View.GONE);
                        loginbutton.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loginPB.setVisibility(View.GONE);
                    loginbutton.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private void sendToMain() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        userType=spinner2.getSelectedItem().toString();
        usertype.setText(userType);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
