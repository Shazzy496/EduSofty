package com.sharon.edusoft.Video;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sharon.edusoft.DarajaMpesa.MpesaActivity;
import com.sharon.edusoft.LoginActivity;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    private Context mContext;
    private List<Videos> videosList;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    public String ActivityIdentity="VideoFeeds";

    private String name, profile_pic;
    private String user_id;

    public VideosAdapter(Context mContext, List<Videos> videosList) {
        this.mContext = mContext;
        this.videosList = videosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videos_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Videos videos = videosList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference("RegisteredUsers");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        setVideoDetails(holder, videos);

        if (currentUser != null) {
            user_id = currentUser.getUid();
            checkUsersVideoStopPosition(holder, videos);
        }

        holder.cvVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                boolean Islogin = prefs.getBoolean("Islogin", false);//get value of last login status.

                if (!Islogin) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(intent);
                } else if (Islogin) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SuccessFul Users");
                    reference.orderByChild("id").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Intent videoIntent = new Intent(mContext, VideoActivity.class);
                                videoIntent.putExtra("video_id", videos.getVideo_id());
                                videoIntent.putExtra("video", videos.getVideo());
                                videoIntent.putExtra("video_duration", videos.getVideoDuration());
                                videoIntent.putExtra("video_user_id", videos.getUser_id());
                                mContext.startActivity(videoIntent);

                            } else {
                                if (ActivityIdentity!=null) {
                                    Intent mpesaIntent = new Intent(mContext.getApplicationContext(), MpesaActivity.class);
                                    mpesaIntent.putExtra("identity",ActivityIdentity);
                                    mContext.startActivity(mpesaIntent);
                                } else{
                                    Toast.makeText(mContext.getApplicationContext(),"NULL IDENTITY",Toast.LENGTH_LONG).show();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }

    private void checkUsersVideoStopPosition(final ViewHolder holder, Videos videos) {
        mDatabase.child("usersVideosStopPosition").child(user_id).child(videos.getVideo_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.pbVideoStopPosition.setVisibility(View.VISIBLE);
                } else {
                    holder.pbVideoStopPosition.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//
//    private void setVideoUserDetails(final ViewHolder holder, Videos videos) {
//        mDatabase.child("RegisteredUsers").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot data:dataSnapshot.getChildren()) {
//                    RegisteredUsers user = data.getValue(RegisteredUsers.class);
//                    name = dataSnapshot.child("name").getValue().toString();
//                    holder.videoPersonName.setText(user.getName());
//                    if (user.getProfile_image().equals("")) {
//                        Glide.with(mContext).load(R.drawable.default_profile_pic).into(holder.civVideoProfilePic);
//                    } else {
//                        Glide.with(mContext).load(user.getProfile_image()).into(holder.civVideoProfilePic);
//                    }
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void setVideoDetails(ViewHolder holder, Videos videos) {
        Glide.with(mContext).load(videos.getVideoThumbnail()).into(holder.ivVideoThumbnail);
        holder.videoTitle.setText(videos.getVideoTitle());
        Spanned sp = Html.fromHtml(videos.getVideoDescription());
        holder.videoDesc.setText(sp);

        long millis = videos.getVideoDuration();
        String videoDurationFormat = String.format("%02d:%02d:%02d",TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        holder.videosVideoDuration.setText(videoDurationFormat);
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        java.util.Date currenTimeZone=new java.util.Date((long)1379487711*1000);
        holder.videoUploadDate.setText(sdf.format(videos.getTimestamp()));

        Glide.with(mContext).load(videos.getVideoThumbnail()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).into(holder.ivVideoBg);
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivVideoThumbnail, ivVideoBg;
        private TextView videoTitle, videoDesc, videoUploadDate,videosVideoDuration;
        private CardView cvVideosBottom, cvVideos;
        private ProgressBar pbVideoStopPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivVideoThumbnail = itemView.findViewById(R.id.ivVideoThumbnail);
            ivVideoBg = itemView.findViewById(R.id.ivVideoBg);
            videoTitle = itemView.findViewById(R.id.VideoTitle);
            videoDesc = itemView.findViewById(R.id.VideoDesc);
            videoUploadDate = itemView.findViewById(R.id.VideoUploadDate);
            videosVideoDuration = itemView.findViewById(R.id.VideosVideoDuration);
            cvVideosBottom = itemView.findViewById(R.id.cvVideosBottom);

            cvVideos = itemView.findViewById(R.id.cvVideos);
            pbVideoStopPosition = itemView.findViewById(R.id.VideoStopPosition);

        }
    }


}
