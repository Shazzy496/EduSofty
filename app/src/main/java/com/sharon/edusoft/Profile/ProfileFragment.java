package com.sharon.edusoft.Profile;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharon.edusoft.Channel.ChannelActivity;
import com.sharon.edusoft.Channel.CreateChannelActivity;
import com.sharon.edusoft.LoginActivity;
import com.sharon.edusoft.R;
import com.sharon.edusoft.RegisterActivity;
import com.sharon.edusoft.Settings.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharon.edusoft.Settings.SettingsChangeProfilePicActivity;
import com.sharon.edusoft.SetupAccount.SetupAccountImageActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Toolbar profiletoolbar;

    private CircleImageView civChannelProfilePic;
    private TextView PersonName, Email;

    private NestedScrollView nswProfile;
    private FrameLayout flProfileContentHeader;
    private CardView cvProfileSettings, cvCreateNewChannel,cvMyChannel,history,likedvideos,changeProfile;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id,userType;



    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profiletoolbar = view.findViewById(R.id.profiletoolbar);
        profiletoolbar.setTitle("Profile");
        ((AppCompatActivity)getActivity()).setSupportActionBar(profiletoolbar);

        nswProfile = view.findViewById(R.id.nswProfile);
        flProfileContentHeader = view.findViewById(R.id.flProfileContentHeader);
        changeProfile = view.findViewById(R.id.changeProfile);
        cvMyChannel = view.findViewById(R.id.cvMyChannel);
        likedvideos = view.findViewById(R.id.likedvideos);

        civChannelProfilePic = view.findViewById(R.id.civChannelProfilePic);
        PersonName = view.findViewById(R.id.PersonName);
        Email = view.findViewById(R.id.Email);
        cvCreateNewChannel = view.findViewById(R.id.cvCreateNewChannel);
        cvProfileSettings = view.findViewById(R.id.cvProfileSettings);


        mDatabase = FirebaseDatabase.getInstance().getReference("users");
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
            getUserDetails();
        }

        cvCreateNewChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createChannelIntent = new Intent(getActivity(), CreateChannelActivity.class);
                startActivity(createChannelIntent);
            }
        });

        cvProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), SettingsChangeProfilePicActivity.class);
                startActivity(i);
            }
        });

      cvMyChannel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent=new Intent(getActivity(), SetupAccountImageActivity.class);
              startActivity(intent);
          }
      });

    }
    private void getUserDetails() {
        mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("users")) {
                        String myChannel = dataSnapshot.child("users").getValue().toString();
                        Picasso.get().load(myChannel).placeholder(R.drawable.default_profile_pic).into(civChannelProfilePic);

                    }
                    String name = dataSnapshot.child("users").child("name").getValue(String.class);
                    String email = dataSnapshot.child("users").child("email").getValue(String.class);
                    String userType=dataSnapshot.child("users").child("login").getValue(String.class);

                    if (dataSnapshot.child("profile_image").getValue().toString().equals("")) {
                        Glide.with(getActivity()).load(R.drawable.default_profile_pic).into(civChannelProfilePic);
                    } else {
                        String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                        Glide.with(getActivity()).load(profile_image).into(civChannelProfilePic);
                    }if (userType.equals("Producer")){
                        nswProfile.setVisibility(View.VISIBLE);

                    }else{
                        nswProfile.setVisibility(View.INVISIBLE);
                    }


                    PersonName.setText(name);
                    Email.setText(email);
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
