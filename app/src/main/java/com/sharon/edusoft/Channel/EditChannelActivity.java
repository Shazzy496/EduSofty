package com.sharon.edusoft.Channel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sharon.edusoft.R;
import com.sharon.edusoft.SetupAccount.SetupAccountImageActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static com.sharon.edusoft.R.anim.play_panel_close_background;

public class EditChannelActivity extends AppCompatActivity {

    private Toolbar channeledittoolbar;

    private EditText etEditChannelPersonName;
    private TextView changeBanner;


    private String profile_image, banner_image, person_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_channel);

        channeledittoolbar = findViewById(R.id.channeledittoolbar);
        channeledittoolbar.setTitle("Edit Channel");
        setSupportActionBar(channeledittoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        channeledittoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_close_black_24dp));
        channeledittoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(0, 0);
            }
        });

        etEditChannelPersonName = findViewById(R.id.etEditChannelPersonName);
        changeBanner=findViewById(R.id.changeBanner);

        changeBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(EditChannelActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .setAspectRatio(1, 1)
                                        .start(EditChannelActivity.this);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                                PermissionListener dialogPermissionListener =
                                        DialogOnDeniedPermissionListener.Builder
                                                .withContext(EditChannelActivity.this)
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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.channel_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.channel_edit_save_menu_item:
                person_name = etEditChannelPersonName.getText().toString();

                if (person_name.isEmpty()) {
                    etEditChannelPersonName.setError("Name cannot be empty");
                } else {
                    Intent channelSaveIntent = new Intent(EditChannelActivity.this, ChannelActivity.class);
                    channelSaveIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(channelSaveIntent);
                }


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(
                0,
                play_panel_close_background
        );
    }
}
