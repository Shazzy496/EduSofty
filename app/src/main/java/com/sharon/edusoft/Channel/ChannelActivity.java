package com.sharon.edusoft.Channel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharon.edusoft.R;
import com.sharon.edusoft.Video.Videos;
import com.sharon.edusoft.Video.VideosAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChannelActivity extends AppCompatActivity {

    private Toolbar channeltoolbar;

    private ImageView ivChannelBanner;
    private CircleImageView civChannelProfilePic;
    private TextView ChannelName, ChannelVideosSeeAll;
    private CardView cvChannelPopularVideos;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private RecyclerView rvChannelVideos;
    private VideosAdapter videosAdapter;
    private LinearLayoutManager linearLayoutManagerVideos;
    private List<Videos> videosList = new ArrayList<>();

    private String user_id, Channel_id;
    private Channel channel;

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        mContext = ChannelActivity.this;


        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            String Channel_id = extras.getString("Channel_id");
        }


        channeltoolbar = findViewById(R.id.channeltoolbar);
        setSupportActionBar(channeltoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        channeltoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        channeltoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivChannelBanner = findViewById(R.id.ivChannelBanner);
        civChannelProfilePic = findViewById(R.id.civChannelProfilePic);
        ChannelName = findViewById(R.id.ChannelName);
        cvChannelPopularVideos = findViewById(R.id.cvChannelPopularVideos);
        ChannelVideosSeeAll = findViewById(R.id.ChannelVideosSeeAll);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        cvChannelPopularVideos.setVisibility(View.GONE);

        rvChannelVideos = findViewById(R.id.rvChannelVideos);
        videosAdapter = new VideosAdapter(mContext, videosList);
        linearLayoutManagerVideos = new LinearLayoutManager(mContext);
        rvChannelVideos.setAdapter(videosAdapter);
        rvChannelVideos.setLayoutManager(linearLayoutManagerVideos);

        if (Channel_id!= null) {
            setChannelDetails();
//            setChannelVideos();
        }


    }

    private void setChannelVideos() {
        mDatabase.child("videos").limitToLast(5).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Videos videos = dataSnapshot.getValue(Videos.class);
                if (videos.getChannelId().equals(Channel_id)) {
                    videosList.add(videos);
                    videosAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setChannelDetails() {
        mDatabase.child("channels").child(Channel_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    channel = dataSnapshot.getValue(Channel.class);

                    channeltoolbar.setTitle(channel.getChannel_name());

                    if (channel.getChannel_banner().equals("")) {
                        Picasso.get().load(R.drawable.default_banner_bg_image).into(ivChannelBanner);
                    } else {
                        Picasso.get().load(channel.getChannel_banner()).into(ivChannelBanner);
                    }

                    if (channel.getChannel_profile_pic().equals("")) {
                        Picasso.get().load(R.drawable.default_profile_pic).into(civChannelProfilePic);
                    } else {
                        Picasso.get().load(channel.getChannel_profile_pic()).into(civChannelProfilePic);
                    }

                    ChannelName.setText(channel.getChannel_name());


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.channel_menu, menu);
        if (currentUser == null) {
            menu.findItem(R.menu.channel_edit_menu).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.channel_edit_menu_item:
                Intent channelEditIntent = new Intent(ChannelActivity.this, EditChannelActivity.class);
                channelEditIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(channelEditIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
