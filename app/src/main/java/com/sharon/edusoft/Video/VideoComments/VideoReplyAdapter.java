package com.sharon.edusoft.Video.VideoComments;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharon.edusoft.R;
import com.sharon.edusoft.SetupAccount.RegisteredUsers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoReplyAdapter extends RecyclerView.Adapter<VideoReplyAdapter.ViewHolder> {

    private Context mContext;
    private List<VideoReplies> videoRepliesList;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    public VideoReplyAdapter(Context mContext, List<VideoReplies> videoRepliesList) {

        this.mContext = mContext;
        this.videoRepliesList = videoRepliesList;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_reply_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final VideoReplies videoReplies = videoRepliesList.get(position);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase1=FirebaseDatabase.getInstance().getReference("RegisteredUsers");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");
        holder.setIsRecyclable(false);
//        setReplyUserDetails(holder, videoReplies);
        setReplies(holder, videoReplies);


    }

    private void setReplies(ViewHolder holder, VideoReplies videoReplies) {
        holder.reply.setText(videoReplies.getReply());
        Glide.with(mContext).load(videoReplies.getPhoto()).into(holder.civReplyProfilePic);
        holder.personName.setText(videoReplies.getName());
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy h:mm a", Locale.getDefault());
        holder.time.setText(sdf.format(videoReplies.getTimestamp()));
    }
    @Override
    public long getItemId(int position){return position;}
    @Override
    public int getItemViewType(int position){return position;}
    @Override
    public int getItemCount() {
        return videoRepliesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView civReplyProfilePic;
        private TextView personName, reply,time;
        private CardView cvVideoComment;

        public ViewHolder(View view) {
            super(view);
            civReplyProfilePic = itemView.findViewById(R.id.civReplyProfilePic);
            personName = itemView.findViewById(R.id.personName);
            reply = itemView.findViewById(R.id.reply);
            cvVideoComment = itemView.findViewById(R.id.cvVideoComment);
            time=itemView.findViewById(R.id.time);
        }
    }
}
