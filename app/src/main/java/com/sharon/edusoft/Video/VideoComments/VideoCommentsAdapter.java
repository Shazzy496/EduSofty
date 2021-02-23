package com.sharon.edusoft.Video.VideoComments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.sharon.edusoft.SetupAccount.RegisteredUsers;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


import de.hdodenhof.circleimageview.CircleImageView;

public class VideoCommentsAdapter extends RecyclerView.Adapter<VideoCommentsAdapter.ViewHolder> {

    private Context mContext;
    private List<VideoComments> videoCommentsList;
    public boolean openRepliesFragment = false;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;



    public VideoCommentsAdapter(Context mContext, List<VideoComments> videoCommentsList) {
        this.mContext = mContext;
        this.videoCommentsList = videoCommentsList;
    }


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_comment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final VideoComments videoComments = videoCommentsList.get(position);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase1=FirebaseDatabase.getInstance().getReference("RegisteredUsers");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        setComments( holder, videoComments);

        (holder).cvVideoComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                  SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(mContext);
//                  SharedPreferences.Editor editor=preferences.edit();
//                  editor.putString("user_id", videoComments.getUser_id());
//                    editor.putString("comment_id", videoComments.getComment_id());
//                   editor.putString("video_id", videoComments.getVideo_id());
//                    editor.apply();
                    AppCompatActivity appCompatActivity = (AppCompatActivity) v.getContext();
                    VideoCommentsFragment fragment = new VideoCommentsFragment();
                    Bundle openRepliesIntent = new Bundle();
                    openRepliesIntent.putString("user_id", videoComments.getUser_id());
                    openRepliesIntent.putString("comment_id", videoComments.getComment_id());
                    openRepliesIntent.putString("video_id", videoComments.getVideo_id());
                    fragment.setArguments(openRepliesIntent);
                    appCompatActivity.getSupportFragmentManager()
                            .beginTransaction().add(R.id.flVideoComments, fragment)
                            .addToBackStack(null).commit();


            }
        });


    }

    private void setComments(ViewHolder holder, VideoComments videoComments) {
        holder.Comment.setText(videoComments.getComment());
        Glide.with(mContext).load(videoComments.getPhoto()).into(holder.civCommentProfilePic);
        holder.CommentPersonName.setText(videoComments.getName());
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy h:mm a", Locale.getDefault());
        holder.time.setText(sdf.format(videoComments.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return videoCommentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView civCommentProfilePic;
        private TextView CommentPersonName, Comment, CommentReply,time;
        private CardView cvVideoComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            civCommentProfilePic = itemView.findViewById(R.id.civCommentProfilePic);
            CommentPersonName = itemView.findViewById(R.id.commentPersonName);
            Comment = itemView.findViewById(R.id.comment);
            CommentReply = itemView.findViewById(R.id.commentReply);
            cvVideoComment = itemView.findViewById(R.id.cvVideoComment);
            time=itemView.findViewById(R.id.time);


        }
    }

}
