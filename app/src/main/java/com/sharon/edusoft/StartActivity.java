package com.sharon.edusoft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.sharon.edusoft.OTP_Receiver.OtpVerification;

public class StartActivity extends AppCompatActivity {

private Button getStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getStarted=findViewById(R.id.getStarted);
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent=new Intent(StartActivity.this, OtpVerification.class);
                startActivity(startIntent);
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),StudDashboard.class));
            finish();
        }
    }
}
