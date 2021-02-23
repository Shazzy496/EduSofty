package com.sharon.edusoft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.sharon.edusoft.SetbookPdf.SetbookPdf;

public class StudDashboard extends AppCompatActivity {
    private Button watch,read;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stud_dashboard);

        watch=findViewById(R.id.watchVideo);
        read=findViewById(R.id.readBook);

        watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent watchVid=new Intent(StudDashboard.this,MainActivity.class);
                startActivity(watchVid);
            }
        });

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent readbook=new Intent(StudDashboard.this, SetbookPdf.class);
                startActivity(readbook);
            }
        });
    }
}
