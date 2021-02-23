package com.sharon.edusoft.AdminPanel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sharon.edusoft.AddVideo.ChooseVideoActivity;
import com.sharon.edusoft.LoginActivity;
import com.sharon.edusoft.MyVideos.MyVideosActivity;
import com.sharon.edusoft.OTP_Receiver.OtpVerification;
import com.sharon.edusoft.R;

public class AdminHome extends AppCompatActivity {

    private CardView video, setbook,myVideos,myPdf;
    private TextView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        video=findViewById(R.id.uploadVideo);
        setbook=findViewById(R.id.uploadPdf);
        logout=findViewById(R.id.logout);
        myVideos=findViewById(R.id.myVideos);
        myPdf=findViewById(R.id.myPdf);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadVideo = new Intent(AdminHome.this, ChooseVideoActivity.class);
                startActivity(uploadVideo);
            }
        });

        setbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent newintent=new Intent(AdminHome.this, UploadPdf.class);
             startActivity(newintent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adminlogout=new Intent(AdminHome.this, OtpVerification.class);
                startActivity(adminlogout);
                finish();
            }
        });
        myVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myVideos=new Intent(AdminHome.this, MyVideosActivity.class);
                startActivity(myVideos);
            }
        });
        myPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mypdf=new Intent(AdminHome.this,AdminViewPdf.class);
                startActivity(mypdf);
            }
        });
    }
}
