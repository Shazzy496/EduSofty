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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharon.edusoft.Library;
import com.sharon.edusoft.LoginActivity;;
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
import com.sharon.edusoft.SetupAccount.RegisteredUsers;
import com.sharon.edusoft.SetupAccount.SetupAccountImageActivity;
import com.sharon.edusoft.Video.registration;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Toolbar profiletoolbar;

    private CircleImageView civChannelProfilePic;
    private TextView personName, bio;

    private NestedScrollView nswProfile;
    private FrameLayout flProfileContentHeader;
    private CardView cvProfileSettings,cvMyChannel,likedvideos,changeProfile;

    private DatabaseReference mDatabase;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private FirebaseUser mFireBaseUser;

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
        personName = view.findViewById(R.id.profileName);
        bio = view.findViewById(R.id.etSetupNameBio);
        cvProfileSettings = view.findViewById(R.id.cvProfileSettings);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("registration").child(currentUser.getUid());
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

        }
        else {
            user_id = currentUser.getUid();
            getUserDetails();
            setProfilePic();
        }



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
        likedvideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), Library.class);
                startActivity(intent);
            }
        });



    }


    private void setProfilePic() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("RegisteredUsers").child(currentUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RegisteredUsers users=snapshot.getValue(RegisteredUsers.class);
                assert users!=null;
                if (users.getProfile_image().equals(""))
                    Glide.with(getActivity()).load(R.drawable.default_profile_pic).into(civChannelProfilePic);
                else {
                    Glide.with(getActivity()).load(users.getProfile_image()).into(civChannelProfilePic);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserDetails() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                registration reg =dataSnapshot.getValue(registration.class);
                personName.setText(reg.getName());
                bio.setText(reg.getEmail());
                assert reg!=null;


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.profile_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {


        }
        return super.onOptionsItemSelected(item);
    }
}
