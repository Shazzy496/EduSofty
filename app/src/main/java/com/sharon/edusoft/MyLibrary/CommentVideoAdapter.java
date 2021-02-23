package com.sharon.edusoft.MyLibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharon.edusoft.R;
import com.sharon.edusoft.Video.VideoComments.VideoComments;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentVideoAdapter extends RecyclerView.Adapter<CommentVideoAdapter.ViewHolder> {
    private Context mContext;
    private List<VideoComments> videoCommentsList;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;


    public CommentVideoAdapter(Context mContext, List<VideoComments> videoCommentsList) {
        this.mContext=mContext;
        this.videoCommentsList=videoCommentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_comment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentVideoAdapter.ViewHolder holder, int position) {
        final VideoComments videoComments = videoCommentsList.get(position);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        setComments( holder, videoComments);
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
            CommentReply.setVisibility(View.GONE);
            cvVideoComment = itemView.findViewById(R.id.cvVideoComment);
            time=itemView.findViewById(R.id.time);
        }
    }
}
