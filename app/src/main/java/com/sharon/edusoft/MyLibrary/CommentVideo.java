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
import com.sharon.edusoft.R;
import com.sharon.edusoft.Video.VideoComments.VideoComments;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentVideo extends Fragment {
    private Context mContext;
    private RecyclerView commentVideo_frag;
    private CommentVideoAdapter commentVideoAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<VideoComments> videoCommentsList=new ArrayList<>();

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    String user_id;
    private TextView userComments;

    public CommentVideo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment_video, container, false);
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

        commentVideo_frag=view.findViewById(R.id.commentVideo_frag);
        commentVideoAdapter=new CommentVideoAdapter(mContext,videoCommentsList);
        linearLayoutManager=new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,true);
        commentVideo_frag.setAdapter(commentVideoAdapter);
        commentVideo_frag.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);
        user_id=currentUser.getUid();
        userComments=view.findViewById(R.id.userComments);

        getUserComments();
        getuserVideoComments();

    }

    private void getuserVideoComments() {
        mDatabase.child("userComments").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if (snapshot.exists()){
                  userComments.setVisibility(View.GONE);
              } else{
                  userComments.setVisibility(View.VISIBLE);
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserComments() {
        mDatabase.child("userComments").child(user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
               if (snapshot.exists()){
                    VideoComments videoComments=snapshot.getValue(VideoComments.class);
                    if (videoComments.getComment_userId().equals(user_id)){
                        videoCommentsList.add(videoComments);
                    }
                    commentVideoAdapter.notifyDataSetChanged();
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
