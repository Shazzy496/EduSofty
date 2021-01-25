package com.sharon.edusoft.Home.SubscriptionFeeds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.sharon.edusoft.Video.VideoActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class SubscriptionFeedsAdapter extends RecyclerView.Adapter<SubscriptionFeedsAdapter.ViewHolder> {

    private Context mContext;
    private List<SubscriptionFeeds> subscriptionFeedsList;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private String user_id;


    public SubscriptionFeedsAdapter(Context mContext, List<SubscriptionFeeds> subscriptionFeedsList) {
        this.mContext = mContext;
        this.subscriptionFeedsList = subscriptionFeedsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videos_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final SubscriptionFeeds subscriptionFeeds = subscriptionFeedsList.get(position);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("RegisteredUsers");
        mDatabase1 = FirebaseDatabase.getInstance().getReference("videos");
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");


        if (currentUser != null) {
            user_id = currentUser.getUid();
        }


        setVideoDetails(holder, subscriptionFeeds);

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
                                videoIntent.putExtra("video_id", subscriptionFeeds.getVideo_id());
                                videoIntent.putExtra("video", subscriptionFeeds.getVideo());
                                videoIntent.putExtra("video_duration", subscriptionFeeds.getVideoDuration());
                                videoIntent.putExtra("video_user_id", subscriptionFeeds.getUser_id());
                                mContext.startActivity(videoIntent);

                            } else {

                                Intent mpesaIntent = new Intent(mContext, MpesaActivity.class);
                                mContext.startActivity(mpesaIntent);


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

    private void setVideoDetails(ViewHolder holder, SubscriptionFeeds subscriptionFeeds) {
        Glide.with(mContext).load(subscriptionFeeds.getVideoThumbnail()).into(holder.ivVideoThumbnail);
        holder.videoTitle.setText(subscriptionFeeds.getVideoTitle());
        Spanned sp = Html.fromHtml(subscriptionFeeds.getVideoDescription());
        if (subscriptionFeeds.getVideoDescription().equals("")) {
            holder.videoDesc.setVisibility(View.GONE);
        } else {
            holder.videoDesc.setText(sp);
        }
        long millis = subscriptionFeeds.getVideoDuration();
        String videoDurationFormat = String.format("%02d:%02d:%02d",TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        holder.videosVideoDuration.setText(videoDurationFormat);
        holder.video_id= subscriptionFeeds.getVideo_id();

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        //java.util.Date currenTimeZone=new java.util.Date((long)1379487711*1000);
        holder.videoUploadDate.setText(sdf.format(subscriptionFeeds.getTimestamp()));

        Glide.with(mContext).load(subscriptionFeeds.getVideoThumbnail()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).into(holder.ivVideoBg);
    }

    @Override
    public int getItemCount() {
        return subscriptionFeedsList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView ivVideoThumbnail, ivVideoBg;
        private TextView videoTitle, videoDesc, videoUploadDate, videosVideoDuration;
        private CardView cvVideosBottom, cvVideos;
        private String video_id;
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
            video_id = null;

        }
    }
}
