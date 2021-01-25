package com.sharon.edusoft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import com.sharon.edusoft.Discover.DiscoverFragment;
import com.sharon.edusoft.Home.HomeFragment;
import com.sharon.edusoft.Profile.ProfileFragment;
import com.sharon.edusoft.SetupAccount.SetupAccountImageActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mainFrameLayout;
    private BottomNavigationView mainBottomNavigationView;

    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private DiscoverFragment discoverFragment;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;


    private String user_id;
    private Dialog dialog;

    private Context mContext;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        mainFrameLayout = findViewById(R.id.mainFrameLayout);
        mainBottomNavigationView = findViewById(R.id.mainBottomNavigationView);

        mainBottomNavigationView.setBackgroundColor(Color.WHITE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");


        if (currentUser != null) {
            user_id = currentUser.getUid();

        }

        homeFragment = new HomeFragment();
        discoverFragment = new DiscoverFragment();
        profileFragment = new ProfileFragment();

        loadFragment(homeFragment);


        mainBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.home_main_bottom_menu_item:
                        loadFragment(homeFragment);
                        return true;

                    case R.id.discover_main_bottom_menu_item:
                        loadFragment(discoverFragment);
                        return true;

                    case R.id.profile_main_bottom_menu_item:
                        loadFragment(profileFragment);
                        return true;
                }
                return false;
            }
        });


    }

    private void checkUserHasProfilePicOrNot() {
        mDatabase.child("users").child(user_id).child("profile_image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Intent userProfilePicIntent = new Intent(MainActivity.this, SetupAccountImageActivity.class);
                    startActivity(userProfilePicIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

   /* @Override
    public void onBackPressed() {
        if (hasChannel && Variables.selected_channel_id.equals("")) {
            checkUserHasChannelOrNot();
        } else {
            super.onBackPressed();
        }

    }*/
}
