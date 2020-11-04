package com.sharon.edusoft.Channel;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharon.edusoft.LoginActivity;
import com.sharon.edusoft.MyVideos.MyVideosActivity;
import com.sharon.edusoft.R;
import com.sharon.edusoft.RegisterActivity;
import com.sharon.edusoft.Settings.SettingsActivity;
import com.sharon.edusoft.Utils.Variables;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ChannelFragment extends Fragment {

    private Toolbar profiletoolbar;

    private CircleImageView civChannelProfilePic;
    private TextView ChannelPersonName, ChannelEmail;
    private CardView cvChannelSettings, cvMyChannel,cvHistory;

    private NestedScrollView nswProfile;
    private FrameLayout flProfileContentHeader;
    private CardView cvProfileMyVideos;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;


    private Dialog dialog;
    private Context mContext;
    private List<Channel> channelList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;



    public ChannelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_channel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        profiletoolbar = view.findViewById(R.id.profiletoolbar);
        profiletoolbar.setTitle("Profile");
        ((AppCompatActivity)getActivity()).setSupportActionBar(profiletoolbar);

        nswProfile = view.findViewById(R.id.nswProfile);
        flProfileContentHeader = view.findViewById(R.id.flProfileContentHeader);

        civChannelProfilePic = view.findViewById(R.id.civChannelProfilePic);
        ChannelPersonName = view.findViewById(R.id.ChannelPersonName);
        ChannelEmail = view.findViewById(R.id.ChannelEmail);

        cvProfileMyVideos = view.findViewById(R.id.cvProfileMyVideos);
        cvChannelSettings = view.findViewById(R.id.cvChannelSettings);
        cvMyChannel = view.findViewById(R.id.cvMyChannel);
        cvHistory=view.findViewById(R.id.cvHistory);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        if (currentUser == null) {
            nswProfile.setVisibility(View.INVISIBLE);

            LayoutInflater factory = LayoutInflater.from(getActivity());
            View myView = factory.inflate(R.layout.dialog_account_login_register, null);
            flProfileContentHeader.addView(myView);

            Button register = myView.findViewById(R.id.register);
            Button login = myView.findViewById(R.id.login);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent);
                }
            });

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
                    startActivity(registerIntent);
                }
            });

        } else {
            user_id = currentUser.getUid();
            getChannelDetails();


            cvMyChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent channelIntent = new Intent(getActivity(), ChannelActivity.class);
                    channelIntent.putExtra("channel_id", Variables.selected_channel_id);
                    channelIntent.putExtra("user_id", user_id);
                    startActivity(channelIntent);
                }
            });


        }

        cvProfileMyVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getActivity(), MyVideosActivity.class);
                startActivity(profileIntent);
            }
        });

        cvChannelSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
    }
    private void getChannelDetails() {
        mDatabase.child("channels").child(String.valueOf(Variables.selected_channel_id)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("channels")) {
                      String myChannel=dataSnapshot.child("channels").getValue().toString();
                        Picasso.get().load(myChannel).placeholder(R.drawable.default_profile_pic).into(civChannelProfilePic);

                    }
                    String name = dataSnapshot.child("channel_name").getValue().toString();
                    String email = dataSnapshot.child("channel_email").getValue().toString();
//                String profile_image = dataSnapshot.child("profile_image").getValue().toString();

                    if (dataSnapshot.child("channel_profile_pic").getValue().toString().equals("")) {
                        Glide.with(getActivity()).load(R.drawable.default_profile_pic).into(civChannelProfilePic);
                    } else {
                        String profile_image = dataSnapshot.child("channel_profile_pic").getValue().toString();
                        Glide.with(getActivity()).load(profile_image).into(civChannelProfilePic);
                    }

//                Glide.with(getActivity()).load(profile_image).into(civChannelProfilePic);
                    ChannelPersonName.setText(name);
                    ChannelEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {


        }
        return super.onOptionsItemSelected(item);
    }




}
