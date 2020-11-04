package com.sharon.edusoft.Video.VideoComments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoCommentsAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<VideoComments> videoCommentsList;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String User_id;
    private String profile_pic, person_name;

    public VideoCommentsAdapter(Context mContext, List<VideoComments> videoCommentsList) {
        this.mContext = mContext;
        this.videoCommentsList = videoCommentsList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_comment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final VideoComments videoComments = videoCommentsList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        setCommentUserDetails((ViewHolder) holder, videoComments);
        setComments((ViewHolder) holder, videoComments);

        ((ViewHolder) holder).cvVideoComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRepliesIntent = new Intent("openRepliesFragment");
                openRepliesIntent.putExtra("openReplies", true);
                openRepliesIntent.putExtra("comment_id", videoComments.getComment_id());
                openRepliesIntent.putExtra("video_id", videoComments.getVideo_id());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(openRepliesIntent);
            }
        });

    }

    private void setCommentUserDetails(final ViewHolder holder, VideoComments videoComments) {
        mDatabase.child("users").child(String.valueOf(videoComments.getUser_id())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                person_name = dataSnapshot.child("name").getValue(String.class);
                profile_pic = dataSnapshot.child("profile_image").getValue(String.class);

                holder.tvCommentPersonName.setText(person_name);
                Glide.with(mContext).load(profile_pic).into(holder.civCommentProfilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setComments(ViewHolder holder, VideoComments videoComments) {
        holder.tvComment.setText(videoComments.getComment());
    }

    @Override
    public int getItemCount() {
        return videoCommentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView civCommentProfilePic;
        private TextView tvCommentPersonName, tvComment, tvCommentReply;
        private CardView cvVideoComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            civCommentProfilePic = itemView.findViewById(R.id.civCommentProfilePic);
            tvCommentPersonName = itemView.findViewById(R.id.tvCommentPersonName);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvCommentReply = itemView.findViewById(R.id.tvCommentReply);
            cvVideoComment = itemView.findViewById(R.id.cvVideoComment);

        }
    }
}
