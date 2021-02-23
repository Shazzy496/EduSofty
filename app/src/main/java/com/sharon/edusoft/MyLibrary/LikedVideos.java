package com.sharon.edusoft.MyLibrary;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.sharon.edusoft.MyVideos.MyVideosAdapter;
import com.sharon.edusoft.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LikedVideos extends Fragment {
    private Context mContext;
    private RecyclerView likedVideos_frag;
    private List<UserLikedVideos> userLikedVideosList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private LikedVideosAdapter likedVideosAdapter;
    private TextView userLikedVideos;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private String user_id, videoId;
    public LikedVideos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_liked_videos, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        likedVideos_frag=view.findViewById(R.id.likedVideo_frag);
        likedVideosAdapter=new LikedVideosAdapter(mContext,userLikedVideosList);
        linearLayoutManager=new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,true);
        likedVideos_frag.setAdapter(likedVideosAdapter);
        likedVideos_frag.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);
        user_id = currentUser.getUid();
        userLikedVideos=view.findViewById(R.id.userLikedVideos);

        getUserVideos();
        getUserLikedVideos();

    }

    private void getUserLikedVideos() {
        mDatabase.child("usersLikedVideos").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){
                   userLikedVideos.setVisibility(View.GONE);
               }else{
                   userLikedVideos.setVisibility(View.VISIBLE);
               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserVideos() {
        mDatabase.child("usersLikedVideos").child(user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                    UserLikedVideos userLikedVideos=snapshot.getValue(UserLikedVideos.class);
                    if (userLikedVideos.getLiked_user_id().equals(user_id)){
                        userLikedVideosList.add(userLikedVideos);
                    }
                    likedVideosAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}