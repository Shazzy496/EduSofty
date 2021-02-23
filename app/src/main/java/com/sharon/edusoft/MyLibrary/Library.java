package com.sharon.edusoft.MyLibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharon.edusoft.R;

import java.util.ArrayList;

public class Library extends AppCompatActivity {
    ImageButton imageButton;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        imageButton=findViewById(R.id.imageButton2);
        TabLayout tablayout=findViewById(R.id.tablayout);
        ViewPager viewPager=findViewById(R.id.mylibraryViewPager);
        ViewpagerAdapter viewpagerAdapter=new ViewpagerAdapter(getSupportFragmentManager());
        viewpagerAdapter.addFragment(new LikedVideos(),"Liked Videos");
        viewpagerAdapter.addFragment(new CommentVideo(),"Video Comments");
        viewPager.setAdapter(viewpagerAdapter);
        tablayout.setupWithViewPager(viewPager);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    class ViewpagerAdapter extends FragmentPagerAdapter{
       private ArrayList<Fragment> fragments;
       private ArrayList<String> titles;
       ViewpagerAdapter(FragmentManager fragmentManager){
           super(fragmentManager);
           this.fragments=new ArrayList<>();
           this.titles=new ArrayList<>();

       }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFragment(Fragment fragment, String title){
           fragments.add(fragment);
           titles.add(title);
        }
        public CharSequence getPageTitle(int position){
           return titles.get(position);
        }
    }
}
