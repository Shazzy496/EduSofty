package com.sharon.edusoft.Home;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sharon.edusoft.AddVideo.ChooseVideoActivity;
import com.sharon.edusoft.R;
import com.sharon.edusoft.Search.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharon.edusoft.Utils.Variables;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private Toolbar hometoolbar;

    private ViewPager homeVP;
    private HomeViewPagerAdapter homeViewPagerAdapter;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id, channel_id;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            channel_id = getArguments().getString("channel_id");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (view != null) {
            hometoolbar = view.findViewById(R.id.hometoolbar);
            hometoolbar.setTitle("Home");
            ((AppCompatActivity)getActivity()).setSupportActionBar(hometoolbar);


            homeVP = view.findViewById(R.id.homeVP);
            homeViewPagerAdapter = new HomeViewPagerAdapter(getChildFragmentManager());
            homeVP.setAdapter(homeViewPagerAdapter);
            homeVP.setCurrentItem(1);

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            mStorage = FirebaseStorage.getInstance();
            storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        }


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        if (currentUser == null) {
            if (Variables.selected_channel_id.equals("")) {
                menu.findItem(R.id.upload_home_menu_item).setVisible(false);
            } else {
                menu.findItem(R.id.upload_home_menu_item).setVisible(true);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload_home_menu_item:
                if (currentUser == null) {
                    Toast.makeText(getActivity(), "Please login to upload", Toast.LENGTH_LONG).show();
                } else {
                        Intent uploadIntent = new Intent(getActivity(), ChooseVideoActivity.class);
                        startActivity(uploadIntent);
                }
                break;

            case R.id.search_home_menu_item:
                Intent uploadIntent = new Intent(getActivity(), SearchActivity.class);
                uploadIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(uploadIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
