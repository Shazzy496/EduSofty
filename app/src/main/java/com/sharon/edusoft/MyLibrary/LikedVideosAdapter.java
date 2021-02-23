package com.sharon.edusoft.MyLibrary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.sharon.edusoft.MyVideos.MyVideos;
import com.sharon.edusoft.R;
import com.sharon.edusoft.Video.VideoActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class LikedVideosAdapter extends RecyclerView.Adapter<LikedVideosAdapter.ViewHolder> {
    private Context mContext;
    private List<UserLikedVideos> userLikedVideosList;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private String user_id;

    public LikedVideosAdapter(Context mContext,List<UserLikedVideos> userLikedVideosList){
        this.mContext=mContext;
        this.userLikedVideosList=userLikedVideosList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_video_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
      final UserLikedVideos userLikedVideos=userLikedVideosList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        user_id = currentUser.getUid();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");
        setVideoDetails(holder, userLikedVideos);

        holder.cvMyVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("usersLikedVideos").child(user_id);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String videoId=userLikedVideos.getVideo_id();
                        mDatabase.child("videos").child(videoId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    MyVideos mahvid=snapshot.getValue(MyVideos.class);
                                    Intent myVideos=new Intent(mContext, VideoActivity.class);
                                    myVideos.putExtra("video_id", userLikedVideos.getVideo_id());
                                    myVideos.putExtra("video",mahvid.getVideo());
                                    mContext.startActivity(myVideos);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void setVideoDetails(final ViewHolder holder, final UserLikedVideos userLikedVideos) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("usersLikedVideos").child(user_id);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String videoId=userLikedVideos.getVideo_id();
                    mDatabase.child("videos").child(videoId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            MyVideos myVideos=snapshot.getValue(MyVideos.class);
                            long millis = myVideos.getVideoDuration();
                            String title=myVideos.getVideoTitle();
                            String videoDurationFormat = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                            Calendar calendar = Calendar.getInstance();
                            TimeZone tz = TimeZone.getDefault();
                            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy h:mm a", Locale.getDefault());
                            Glide.with(mContext).load(myVideos.getVideoThumbnail()).into(holder.ivMyVideosVideoThumbnail);
                            holder.MyVideosVideoTItle.setText(title);
                            holder.MyVideosVideoDesc.setText(myVideos.getVideoDesc());
                            holder.MyVideosVideoDuration.setText(videoDurationFormat);
                            holder.MyVideosVideoUploadedDate.setText("Uploaded on: " + sdf.format(myVideos.getTimestamp()));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }

    @Override
    public int getItemCount() {
        return userLikedVideosList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivMyVideosVideoThumbnail;
        private TextView MyVideosVideoTItle, MyVideosVideoDesc, MyVideosVideoUploadedDate, MyVideosVideoDuration;
        private ImageButton ibMyVideosMoreOptions,delete;
        private CardView cvMyVideos;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMyVideosVideoThumbnail = itemView.findViewById(R.id.ivMyVideosVideoThumbnail);
            MyVideosVideoTItle = itemView.findViewById(R.id.MyVideosVideoTItle);
            MyVideosVideoDesc = itemView.findViewById(R.id.MyVideosVideoDesc);
            MyVideosVideoUploadedDate = itemView.findViewById(R.id.MyVideosVideoUploadedDate);
            cvMyVideos = itemView.findViewById(R.id.cvMyVideos);
            ibMyVideosMoreOptions = itemView.findViewById(R.id.ibMyVideosMoreOptions);
            ibMyVideosMoreOptions.setVisibility(View.GONE);
            MyVideosVideoDuration = itemView.findViewById(R.id.MyVideosVideoDuration);
            delete=itemView.findViewById(R.id.delete);
            delete.setVisibility(View.GONE);
        }
    }
}
