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

        setCommentUserDetails( holder, videoComments);
        setComments( holder, videoComments);




        (holder).cvVideoComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRepliesIntent = new Intent("openRepliesFragment");
                openRepliesIntent.putExtra("openReplies", true);
                openRepliesIntent.putExtra("user_id",videoComments.getUser_id());
                openRepliesIntent.putExtra("comment_id", videoComments.getComment_id());
                openRepliesIntent.putExtra("video_id", videoComments.getVideo_id());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(openRepliesIntent);
            }
        });

    }

    private void setCommentUserDetails(final ViewHolder holder, VideoComments videoComments) {
        mDatabase.child("videos").child(videoComments.getVideo_id()).child("comments").child("comment_id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    final  String commenting_person=snapshot.child("user_id").getKey();

                    mDatabase1.orderByChild("id").equalTo(commenting_person).addListenerForSingleValueEvent (new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                RegisteredUsers users = dataSnapshot.getValue(RegisteredUsers.class);
                                String name = dataSnapshot.child("name").getValue().toString();
                                holder.CommentPersonName.setText(name);
                                assert users != null;
                                if (users.getProfile_image().equals("")) {
                                    Glide.with(mContext).load(R.drawable.default_profile_pic).into(holder.civCommentProfilePic);
                                } else {
                                    Glide.with(mContext).load(users.getProfile_image()).into(holder.civCommentProfilePic);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        java.util.Date currenTimeZone=new java.util.Date((long)1379487711*1000);
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
        private  String vdeoId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            civCommentProfilePic = itemView.findViewById(R.id.civCommentProfilePic);
            CommentPersonName = itemView.findViewById(R.id.commentPersonName);
            Comment = itemView.findViewById(R.id.comment);
            CommentReply = itemView.findViewById(R.id.commentReply);
            cvVideoComment = itemView.findViewById(R.id.cvVideoComment);
            time=itemView.findViewById(R.id.time);
            vdeoId=null;

        }
    }

}
