package com.sharon.edusoft.Video.VideoComments;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.sharon.edusoft.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharon.edusoft.SetupAccount.RegisteredUsers;
import com.sharon.edusoft.Video.VideoActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoCommentsFragment extends Fragment {



    private ImageButton ibVideoCommentsClose;
    private TextView VideoComment, VideoCommentPersonName;
    private CircleImageView civVideoCommentProfilePic, civCommentingProfilePic;
    private FrameLayout flVideoComment1;
    private List<VideoReplies> videoRepliesList= new ArrayList<>();
    private VideoReplyAdapter videoReplyAdapter;
    private RecyclerView rvVideoCommentReplies;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String comment_id, video_id,   video_user_id,comment_user_id;
    private String comment;
    private String user_id;
    private Context mContext;


    public VideoCommentsFragment() {
        // Required empty public constructor
        if (videoRepliesList!=null) videoRepliesList.clear();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext =getActivity();
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_video_comments, container, false);

        video_user_id= requireActivity().getIntent().getStringExtra("video_user_id");
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
//        comment_user_id = preferences.getString("user_id","");
//        comment_id = preferences.getString("comment_id","");
//        video_id = preferences.getString("video_id","");

        Bundle bundle = getArguments();
        comment_user_id = Objects.requireNonNull(bundle).getString("user_id");
        comment_id = bundle.getString("comment_id");
        video_id = bundle.getString("video_id");


        flVideoComment1=view.findViewById(R.id.flVideoComment1);
        ibVideoCommentsClose = view.findViewById(R.id.ibVideoCommentsClose);
        civVideoCommentProfilePic = view.findViewById(R.id.civVideoCommentProfilePic);
        VideoComment = view.findViewById(R.id.VideoComment);
        VideoCommentPersonName = view.findViewById(R.id.VideoCommentPersonName); //
        civCommentingProfilePic = view.findViewById(R.id.civCommentingProfilePic);

        rvVideoCommentReplies = view.findViewById(R.id.rvVideoCommentReplies);
        linearLayoutManager = new LinearLayoutManager(mContext);
        rvVideoCommentReplies.setLayoutManager(linearLayoutManager);
        videoReplyAdapter = new VideoReplyAdapter(mContext, videoRepliesList);
        rvVideoCommentReplies.setAdapter(videoReplyAdapter);
        linearLayoutManager.setReverseLayout(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        if (getArguments()!=null){
            comment_id = getArguments().getString("comment_id");
            video_id = getArguments().getString("video_id");
        }

        if (currentUser != null) {
            user_id = currentUser.getUid();

        }
        setComment();
        getReplies();
        ibVideoCommentsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getActivity().onBackPressed();
            }
        });
        flVideoComment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReplyDialog();
            }
        });
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void openReplyDialog() {
        final Dialog commentDialog = new Dialog(mContext);
        commentDialog.setContentView(R.layout.dialog_comment_layout);
        commentDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        commentDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        commentDialog.show();

        CardView cvVideoCommentCancel = commentDialog.findViewById(R.id.cvVideoCommentCancel);
        CardView cvVideoComment = commentDialog.findViewById(R.id.cvVideoComment);
        final EditText etVideoComment = commentDialog.findViewById(R.id.etVideoComment);
        final TextView personName=commentDialog.findViewById(R.id.personName);
        final CircleImageView commentpic=commentDialog.findViewById(R.id.commentpic);

        cvVideoCommentCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentDialog.dismiss();
            }
        });

        cvVideoComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("RegisteredUsers").child(user_id);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        RegisteredUsers users=snapshot.getValue(RegisteredUsers.class);
                        if (users!=null){
                        String name=users.getName();
                        String photo=users.getProfile_image();
                        personName.setText(name);
                        if (photo.equals(""))
                            Glide.with(mContext).load(R.drawable.default_profile_pic).into(civCommentingProfilePic);
                        else {
                            Glide.with(mContext).load(photo).into(civCommentingProfilePic);

                        }
                        String reply = etVideoComment.getText().toString();

                        if (comment.isEmpty()) {
                            etVideoComment.setError("Cannot post empty reply");
                        } else {
                            String reply_id = mDatabase.child("videos").child(video_id).child("reply").push().getKey();
                            HashMap<String, Object> mCommentDataMap = new HashMap<>();
                            mCommentDataMap.put("reply", reply);
                            mCommentDataMap.put("reply_id", reply_id);
                            mCommentDataMap.put("timestamp", System.currentTimeMillis());
                            mCommentDataMap.put("user_id", user_id);
                            mCommentDataMap.put("video_id", video_id);
                            mCommentDataMap.put("commentId",comment_id);
                            mCommentDataMap.put("name", name);
                            mCommentDataMap.put("photo", photo);
                            mDatabase.child("videos").child(video_id).child("comments").child(comment_id).child("reply").child(reply_id).setValue(mCommentDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        commentDialog.dismiss();
                                    }
                                }
                            });
                        }}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void setComment() {
        mDatabase.child("videos").child(video_id).child("comments").child(comment_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                VideoComments videoComments=dataSnapshot.getValue(VideoComments.class);
                if (videoComments!=null) {
                        comment = videoComments.getComment();
                        comment_user_id = videoComments.getUser_id();
                        VideoComment.setText(comment);
                        VideoCommentPersonName.setText(videoComments.getName());
                        String profilePic=videoComments.getPhoto();
                        if (profilePic.equals(""))
                            Glide.with(mContext.getApplicationContext()).load(R.drawable.default_profile_pic).into(civVideoCommentProfilePic);
                        else {
                            Glide.with(mContext.getApplicationContext()).load(profilePic).into(civVideoCommentProfilePic);

                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getReplies() {
        mDatabase.child("videos").child(video_id).child("comments").child(comment_id).child("reply").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    VideoReplies videoReplies = snapshot.getValue(VideoReplies.class);
                    if (videoReplies!=null) {
                        if (videoReplies.getCommentId().equals(comment_id)) {
                            videoRepliesList.add(videoReplies);
                        }
                        videoReplyAdapter.notifyDataSetChanged();
                    }
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
