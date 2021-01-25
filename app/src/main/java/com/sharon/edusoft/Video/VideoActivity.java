package com.sharon.edusoft.Video;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sharon.edusoft.Home.SubscriptionFeeds.SubscriptionFeeds;
import com.sharon.edusoft.Home.SubscriptionFeeds.SubscriptionFeedsAdapter;
import com.sharon.edusoft.R;
import com.sharon.edusoft.SetupAccount.RegisteredUsers;
import com.sharon.edusoft.Video.VideoComments.VideoComments;
import com.sharon.edusoft.Video.VideoComments.VideoCommentsAdapter;
import com.sharon.edusoft.Video.VideoComments.VideoCommentsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

@RequiresApi(api = Build.VERSION_CODES.O)
public class VideoActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "1";
    private static Bitmap videoThumbnailBitmap;
    private Context mContext;

    private FrameLayout flVideoComment1, flVideoComments, videoFL;

    private ImageView ivVideoBG;
    private TextView VideoTitle, VideoDateUploaded, VideoChannelName, VideoNoComments, VideoDesc, VideoCategoryName,VideoCommentPersonName;
    private CircleImageView civVideoChannelProfilePic, civVideoCommentProfilePic, civVideoCommentProfilePic2;
    private CardView cvVideoChannel;
    private NestedScrollView nswVideoDetails;

    private RatingBar ratingBar;
    private TextView ratings;
    private VideoView vvVideo;
    private ProgressBar pbVideo;
    private SeekBar sbVideo;
    private TextView VideoDuration, VideoCurrentVIdeoTIme;
    private ImageButton ibVideoPlayPause, ibVideoFullScreen,settings;
    private FrameLayout flVideoBottomPanel;
    private NestedScrollView nestedScrollView;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private RecyclerView rvVideoComments;
    private List<VideoComments> videoCommentsList = new ArrayList<>();
    private VideoCommentsAdapter videoCommentsAdapter;
    private LinearLayoutManager linearLayoutManager;

    private String profile_pic,channel_name, user_id,channel_id;
    String videoUri;
    private Uri video;
    private String video_thumbnail,video_user_id,video_category_name,comment_id,videoId,video_desc,video_title,producer,rate_id;
    private long video_uploaded_date,video_duration;
    private int stopPosition;
    public boolean openRepliesFragment = false, isVideoPlaying = true;
    public int videoProgess,video_width,video_height,mCurrentPosition=0;
    public  static final String PLAYBACK_TIME="play-time";
    private SubscriptionFeedsAdapter subscriptionFeedsAdapter;
    MediaPlayer mediaPlayer;
    Handler mHandler;
    private Runnable mRunnable;
    float myRatings;
    int count=1;
    private TextView rate;

    MediaSessionCompat mediaSessionCompat;
    private static final int CONTENT_VIEW_ID_FOR_VIDEO = 1;
    private static final int CONTENT_VIEW_ID_FOR_REACTION = 2;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    private FragmentTransaction fragmentTransactionVideoComments = fragmentManager.beginTransaction();
    private VideoPlayerFragment videoPlayerFragment;
    private VideoReactionFragment videoReactionFragment;
    private VideoCommentsFragment videoCommentsFragment;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mContext = VideoActivity.this;


        Bundle bundle = getIntent().getExtras();
        videoId =bundle.get("video_id").toString();
        video_user_id=bundle.get("video_user_id").toString();
        video_duration = bundle.getLong("video_duration");
        video = Uri.parse(bundle.get("video").toString());


        if (savedInstanceState!=null){
            mCurrentPosition=savedInstanceState.getInt(PLAYBACK_TIME);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(openRepliesFragmentBroadcastReceiver,
                new IntentFilter("openRepliesFragment"));
        Bundle setVideoPLayerBundle = new Bundle();
        setVideoPLayerBundle.putString("video_id", String.valueOf(videoId));
        setVideoPLayerBundle.putString("video_duration", String.valueOf(video_duration));
        setVideoPLayerBundle.putString("video", String.valueOf(video));
        setVideoPLayerBundle.putString("stopPosition", String.valueOf(stopPosition));
        setVideoPLayerBundle.putString("video_user_id", String.valueOf(video_user_id));

        videoPlayerFragment = new VideoPlayerFragment();
        videoReactionFragment = new VideoReactionFragment();
        videoCommentsFragment = new VideoCommentsFragment();

        videoReactionFragment.setArguments(setVideoPLayerBundle);
        fragmentTransaction.add(R.id.flVideoReaction, videoReactionFragment, "HELLO-REACTION");

        videoCommentsFragment.setArguments(setVideoPLayerBundle);
        fragmentTransaction.add(R.id.flVideoComments, videoCommentsFragment, "HELLO-COMMENTS");

        fragmentTransaction.commit();
        VideoCommentPersonName=findViewById(R.id.VideoCommentPersonName);
        rate=findViewById(R.id.feedback);
        ratingBar=findViewById(R.id.ratingBar);
        ratings=findViewById(R.id.ratings);
        ivVideoBG = findViewById(R.id.ivVideoBG);
        nswVideoDetails = findViewById(R.id.nswVideoDetails);
        VideoTitle = findViewById(R.id.VideoTitle);
        VideoDateUploaded = findViewById(R.id.dateUploaded);
        VideoNoComments = findViewById(R.id.VideoNoComments);
        civVideoCommentProfilePic = findViewById(R.id.civVideoCommentProfilePic);
        VideoDesc = findViewById(R.id.VideoDesc);
        flVideoComment1 = findViewById(R.id.flVideoComment1);
        flVideoComments = findViewById(R.id.flVideoComments);
        VideoCategoryName = findViewById(R.id.VideoCategoryName);


        vvVideo = findViewById(R.id.vvVideo);
        pbVideo = findViewById(R.id.pbVideo);
        sbVideo = findViewById(R.id.sbVideo);
        VideoDuration = findViewById(R.id.VideoDuration);
        VideoCurrentVIdeoTIme = findViewById(R.id.videoCurrentTime);
        ibVideoPlayPause = findViewById(R.id.ibVideoPlayPause);
        flVideoBottomPanel = findViewById(R.id.flVideoBottomPanel);
        ibVideoFullScreen = findViewById(R.id.ibVideoFullScreen);
        videoFL = findViewById(R.id.videoFL);

        mDatabase = FirebaseDatabase.getInstance().getReference("");
        mDatabase1=FirebaseDatabase.getInstance().getReference("RegisteredUsers");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        Collections.sort(videoCommentsList, new Comparator<VideoComments>() {
            @Override
            public int compare(VideoComments o1, VideoComments o2) {
                long time=o1.getTimestamp();
                long time2=o2.getTimestamp();
            if (time2>time){
                return 1;
            }else if (time>time2){return -1;}
            else return  0;
            }
        });
        rvVideoComments = findViewById(R.id.rvVideoComments);
        videoCommentsAdapter = new VideoCommentsAdapter(mContext, videoCommentsList);
        linearLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        rvVideoComments.setAdapter(videoCommentsAdapter);
        rvVideoComments.setLayoutManager(linearLayoutManager);


        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, vvVideo.getDuration());
        mediaSessionCompat = new MediaSessionCompat(mContext, "tag");
        mediaSessionCompat.setMetadata(builder.build());

        flVideoComments.setVisibility(View.GONE);


        if (currentUser!=null) {
            user_id = currentUser.getUid();
            setVideo();
            setVideoBufferUpdate();
            setVideOtherDetails();
            getVideoDetails();
            checkVideoHasCommentOrNot();
            getComments();
            getUserImage();
        }

        ibVideoPlayPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (vvVideo.isPlaying()) {
                    ibVideoPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    vvVideo.pause();
                    stopPosition = vvVideo.getCurrentPosition();
                    isVideoPlaying = false;
                } else {
                    ibVideoPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
                    vvVideo.start();
                    vvVideo.seekTo(stopPosition);
                    isVideoPlaying = true;
                }
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                final DatabaseReference ref=FirebaseDatabase.getInstance().getReference("videos").child(videoId).child("Ratings").child("MyRatings").child(user_id).child("ratings");
                double intRating=rating;
                ref.setValue(intRating);
                String ratingbar=String.valueOf(ratingBar.getRating());
                Toast.makeText(getApplicationContext(), "You rated this video as:"+ratingbar, Toast.LENGTH_LONG).show();
            }
        });
        vvVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(mContext, "Playback completed", Toast.LENGTH_SHORT).show();
                vvVideo.seekTo(1);
            }
        });


        ibVideoFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoId != null) {
                    Intent videoFullScreenIntent = new Intent(getApplicationContext(), VideoFullscreenActivity.class);
                    videoFullScreenIntent.putExtra("video_id", videoId);
                    videoFullScreenIntent.putExtra("video", video);
                    videoFullScreenIntent.putExtra("stopPosition", vvVideo.getCurrentPosition());
                    videoFullScreenIntent.putExtra("isVideoPlaying", isVideoPlaying);
                    videoFullScreenIntent.putExtra("video_duration", video_duration);
                    startActivityForResult(videoFullScreenIntent, 1);
                }
            }
        });

        flVideoComment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCommentDialog();
            }
        });
      submitRatings();
    }

    private void getUserImage() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("RegisteredUsers").child(currentUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RegisteredUsers users=snapshot.getValue(RegisteredUsers.class);
                assert users!=null;
                if (users.getProfile_image().equals(""))
                    Glide.with(mContext).load(R.drawable.default_profile_pic).into(civVideoCommentProfilePic);
                else {
                    Glide.with(mContext).load(users.getProfile_image()).into(civVideoCommentProfilePic);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void submitRatings(){
        try {
            final DatabaseReference db=FirebaseDatabase.getInstance().getReference();
            final DatabaseReference dbRef=db.child("videos").child(videoId).child("Ratings").child("MyRatings");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    double total=0.0;
                    double count=0.0;
                    double average=0.0;
                    for (DataSnapshot ds:snapshot.getChildren()){
                        String userrate=ds.child("ratings").getValue().toString();
                        double rating= Double.parseDouble(userrate);
                        total=total+rating;
                        count=count+1;
                        average=total/count;

                    }
                    final DatabaseReference newRef=db.child("videos").child(videoId).child("Ratings").child("AverageRatings");
                    newRef.child("current").setValue(average);
                    mDatabase.child("videos").child(videoId).child("Ratings").child("AverageRatings").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String currentRate=snapshot.child("current").getValue().toString();
                            ratings.setText("Average Video Rating is:"+currentRate);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    throw error.toException();

                }
            });
        }catch(Exception e){
            Toast.makeText(VideoActivity.this,""+e,Toast.LENGTH_SHORT).show();
        }
    }

    private void setMediaPlayer(){
        mediaPlayer.stop();
    }

    private void openCommentDialog() {
        final Dialog commentDialog = new Dialog(VideoActivity.this);
        commentDialog.setContentView(R.layout.dialog_comment_layout);
        commentDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        commentDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        commentDialog.show();

        CardView cvVideoCommentCancel = commentDialog.findViewById(R.id.cvVideoCommentCancel);
        CardView cvVideoComment = commentDialog.findViewById(R.id.cvVideoComment);
        final TextView personName=commentDialog.findViewById(R.id.personName);
        final CircleImageView commentpic=commentDialog.findViewById(R.id.commentpic);

        final EditText etVideoComment = commentDialog.findViewById(R.id.etVideoComment);

        cvVideoCommentCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentDialog.dismiss();
            }
        });

        cvVideoComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("RegisteredUsers").child(currentUser.getUid());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        RegisteredUsers users=snapshot.getValue(RegisteredUsers.class);
                        assert users!=null;
                        String name=snapshot.child("name").getValue().toString();
                        String photo=snapshot.child("profile_image").getValue().toString();
                        personName.setText(users.getName());
                        if (users.getProfile_image().equals(""))
                            Glide.with(mContext).load(R.drawable.default_profile_pic).into(commentpic);
                        else {
                            Glide.with(mContext).load(users.getProfile_image()).into(commentpic);

                        }
                        String comment = etVideoComment.getText().toString();

                        if (comment.isEmpty()) {
                            etVideoComment.setError("Cannot post empty comment");
                        } else {
                            String comment_id = mDatabase.child("videos").child(videoId).child("comment").push().getKey();
                            HashMap<String, Object> mCommentDataMap = new HashMap<>();
                            mCommentDataMap.put("comment", comment);
                            mCommentDataMap.put("comment_id", comment_id);
                            mCommentDataMap.put("timestamp", System.currentTimeMillis());
                            mCommentDataMap.put("user_id", video_user_id);
                            mCommentDataMap.put("video_id", videoId);
                            mCommentDataMap.put("name", name);
                            mCommentDataMap.put("photo", photo);
                            mDatabase.child("videos").child(videoId).child("comments").child(comment_id).setValue(mCommentDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        commentDialog.dismiss();
                                    }
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void setVideOtherDetails() {
        long millis = video_duration;
        String videoDurationFormat = String.format("%02d:%02d:%02d",TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        VideoDuration.setText(videoDurationFormat);

        MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                        pbVideo.setVisibility(View.GONE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                        pbVideo.setVisibility(View.VISIBLE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                        pbVideo.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;
            }
        };

        vvVideo.setOnInfoListener(onInfoListener);

        sbVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                VideoActivity.this.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                VideoActivity.this.onStopTrackingTouch(seekBar);
            }
        });
    }



    private void setVideoBufferUpdate() {
        if (video == null) {
            mediaPlayer = MediaPlayer.create(VideoActivity.this, Uri.parse(String.valueOf(video)));
            mediaPlayer.stop();
            sbVideo.setSecondaryProgressTintList(ColorStateList.valueOf(Color.WHITE));
            if (mediaPlayer != null) {
                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
                        double per = percent * 1.00;
                        per = (per / 100);
                        double f = (sbVideo.getMax() * 1.00);
                        f = f * per;
                        if (percent < sbVideo.getMax()) {
                            sbVideo.setSecondaryProgress((int) f);
                        }
                        mediaPlayer.start();
                    }
                });
            }

        }

    }

    private void setVideo() {
        if (isVideoPlaying) {
            vvVideo.setVideoPath(String.valueOf(video));
            vvVideo.seekTo(stopPosition);
            vvVideo.start();
            mHandler = new Handler();
            updateProgressBar();
        } else {
            vvVideo.setVideoPath(String.valueOf(video));
            vvVideo.seekTo(stopPosition);
            vvVideo.pause();
            ibVideoPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            mHandler = new Handler();
            updateProgressBar();
        }
    }

    private void checkVideoHasCommentOrNot() {
        mDatabase.child("videos").child(videoId).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    VideoNoComments.setVisibility(View.GONE);
                } else {
                    VideoNoComments.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getComments() {
        mDatabase.child("videos").child(videoId).child("comments").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    VideoComments videoComments = dataSnapshot.getValue(VideoComments.class);
                    videoCommentsList.add(videoComments);
                    Collections.reverse(videoCommentsList);
                    videoCommentsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    private void getVideoDetails() {
        mDatabase.child("videos").child(videoId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SubscriptionFeeds subscriptionFeeds=dataSnapshot.getValue(SubscriptionFeeds.class);

                VideoTitle.setText(subscriptionFeeds.getVideoTitle());
                Spanned sp = Html.fromHtml(subscriptionFeeds.getVideoDescription());
                if (subscriptionFeeds.getVideoDescription().equals("")) {
                    VideoDesc.setVisibility(View.GONE);
                } else {
                    VideoDesc.setText(sp);
                }

                long millis = subscriptionFeeds.getVideoDuration();
                String videoDurationFormat = String.format("%02d:%02d:%02d",TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                VideoDuration.setText(videoDurationFormat);

                Calendar calendar = Calendar.getInstance();
                TimeZone tz = TimeZone.getDefault();
                calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                VideoDateUploaded.setText(sdf.format(subscriptionFeeds.getTimestamp()));
                video_title = dataSnapshot.child("videoTitle").getValue(String.class);
                video_desc = dataSnapshot.child("videoDescription").getValue(String.class);
                video_thumbnail = dataSnapshot.child("videoThumbnail").getValue(String.class);
                video_uploaded_date = (long) Objects.requireNonNull(dataSnapshot).child("timestamp").getValue();
                video_user_id = dataSnapshot.child("user_id").getValue(String.class);
                video_category_name = dataSnapshot.child("videoCategory").getValue(String.class);
                Glide.with(getApplicationContext()).load(video_thumbnail).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).into(ivVideoBG);
                VideoTitle.setText(video_title);
                VideoCategoryName.setText(video_category_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            stopPosition = data.getIntExtra("stopPosition", 0);
            isVideoPlaying = data.getBooleanExtra("isVideoPlaying", true);


        }
    }

    public BroadcastReceiver openRepliesFragmentBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            openRepliesFragment = intent.getBooleanExtra("openReplies", false);
            comment_id = intent.getStringExtra("comment_id");
            videoId = intent.getStringExtra("video_id");
//            Toast.makeText(mContext, String.valueOf(openRepliesFragment), Toast.LENGTH_SHORT).show();

            if (openRepliesFragment == true) {
                Intent openRepliesIntent = new Intent("showReplies");
                openRepliesIntent.putExtra("comment_id", comment_id);
                openRepliesIntent.putExtra("video_id", videoId);
                openRepliesIntent.putExtra("openReplies", true);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(openRepliesIntent);
                flVideoComments.setVisibility(View.VISIBLE);
            } else {
                flVideoComments.setVisibility(View.GONE);
            }
        }
    };

    private void updateProgressBar() {
        mHandler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            videoProgess = vvVideo.getCurrentPosition() * 100 / vvVideo.getDuration();
            String tm = getVideoTime(vvVideo.getCurrentPosition());
            VideoCurrentVIdeoTIme.setText(tm);
            sbVideo.setProgress(vvVideo.getCurrentPosition());
            sbVideo.setMax(vvVideo.getDuration());
            mHandler.postDelayed(this, 100);

        }
    };

    public void onProgressChanged(SeekBar seekbar, int progress,boolean fromTouch) {

    }
    public void onStartTrackingTouch(SeekBar seekbar) {
        mHandler.removeCallbacks(updateTimeTask);
    }
    public void onStopTrackingTouch(SeekBar seekbar) {
        mHandler.removeCallbacks(updateTimeTask);
        vvVideo.seekTo(sbVideo.getProgress());
        updateProgressBar();
    }

    String getVideoTime(long ms)
    {
        ms/=1000;
        return (String.format("%2d:%02d",((ms%3600)/60), ((ms%3600)%60)));
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(PLAYBACK_TIME,vvVideo.getCurrentPosition());
    }
    @Override
    protected void onResume() {
        super.onResume();
        vvVideo.resume();
    }
    @Override
    protected void onStop() {
        super.onStop();

        stopPosition = vvVideo.getCurrentPosition();
        vvVideo.pause();
    }
    protected void onPause(){
        super.onPause();
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.O){
            vvVideo.pause();
        }
    }
}
