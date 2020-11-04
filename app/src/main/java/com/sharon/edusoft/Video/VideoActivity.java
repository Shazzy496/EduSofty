package com.sharon.edusoft.Video;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sharon.edusoft.Channel.ChannelActivity;
import com.sharon.edusoft.R;
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

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private TextView VideoTitle, VideoDateUploaded, VideoChannelName, VideoNoComments, VideoDesc, VideoCategoryName, VideoViews;
    private CircleImageView civVideoChannelProfilePic, civVideoCommentProfilePic, civVideoCommentProfilePic2;
    private CardView cvVideoChannel;
    private NestedScrollView nswVideoDetails;


    private VideoView vvVideo;
    private ProgressBar pbVideo;
    private SeekBar sbVideo;
    private TextView VideoDuration, VideoCurrentVIdeoTIme;
    private ImageButton ibVideoPlayPause, ibVideoFullScreen,imageButton3;
    private FrameLayout flVideoBottomPanel;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private RecyclerView rvVideoComments;
    private List<VideoComments> videoCommentsList = new ArrayList<>();
    private VideoCommentsAdapter videoCommentsAdapter;
    private LinearLayoutManager linearLayoutManager;

    private String profile_pic;
    private String user_id;
    String videoUri;
    private String video_title;
    private String video_desc;
    private Uri video;
    private String video_thumbnail;
    private String video_user_id;
    private String video_category_name;
    private long video_width;
    private long video_height;
    private String channel_name, comment_id;
    private long video_uploaded_date;
    private String videoId;
    private long video_duration;
    private int stopPosition;
    public boolean openRepliesFragment = false, isVideoPlaying = true;
    private String video_id;
    public int videoProgess;



    MediaPlayer mediaPlayer;
    Handler mHandler;

    MediaSessionCompat mediaSessionCompat;
    NotificationManager notificationManager;
    PendingIntent pausePendingIntent, replay10sPendingIntent, forward10sPendingIntent, likePendingIntent, dislikePendingIntent;

    private static final int CONTENT_VIEW_ID_FOR_VIDEO = 1;
    private static final int CONTENT_VIEW_ID_FOR_REACTION = 2;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    private FragmentTransaction fragmentTransactionVideoComments = fragmentManager.beginTransaction();
    private VideoPlayerFragment videoPlayerFragment;
    private VideoReactionFragment videoReactionFragment;
    private VideoCommentsFragment videoCommentsFragment;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mContext = VideoActivity.this;

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        MediaPlayer mediaPlayer=MediaPlayer.create(this, Uri.parse(String.valueOf(video)));


        Bundle bundle = getIntent().getExtras();
        videoId = bundle.getString("video_id");
        stopPosition =bundle.getInt("stopPosition");
        video_duration = bundle.getLong("video_duration");
        video = Uri.parse(bundle.getString("video"));

        LocalBroadcastManager.getInstance(this).registerReceiver(openRepliesFragmentBroadcastReceiver,
                new IntentFilter("openRepliesFragment"));

        Bundle setVideoPLayerBundle = new Bundle();
        setVideoPLayerBundle.putString("video_id", videoId);
        setVideoPLayerBundle.putString("video_duration", String.valueOf(video_duration));
        setVideoPLayerBundle.putString("video", String.valueOf(video));
        setVideoPLayerBundle.putString("stopPosition", String.valueOf(stopPosition));
        setVideoPLayerBundle.putString("video_user_id", video_user_id);

        videoPlayerFragment = new VideoPlayerFragment();
        videoReactionFragment = new VideoReactionFragment();
        videoCommentsFragment = new VideoCommentsFragment();

        videoReactionFragment.setArguments(setVideoPLayerBundle);
        fragmentTransaction.add(R.id.flVideoReaction, videoReactionFragment, "HELLO-REACTION");

        videoCommentsFragment.setArguments(setVideoPLayerBundle);
        fragmentTransaction.add(R.id.flVideoComments, videoCommentsFragment, "HELLO-COMMENTS");

        fragmentTransaction.commit();

        ivVideoBG = findViewById(R.id.ivVideoBG);
        nswVideoDetails = findViewById(R.id.nswVideoDetails);
        VideoTitle = findViewById(R.id.VideoTitle);
        VideoDateUploaded = findViewById(R.id.VideoDateUploaded);
        VideoChannelName = findViewById(R.id.VideoChannelName);
        VideoNoComments = findViewById(R.id.VideoNoComments);
        civVideoChannelProfilePic = findViewById(R.id.civVideoChannelProfilePic);
        civVideoCommentProfilePic = findViewById(R.id.civVideoCommentProfilePic);
        VideoDesc = findViewById(R.id.VideoDesc);
        flVideoComment1 = findViewById(R.id.flVideoComment1);
        flVideoComments = findViewById(R.id.flVideoComments);
        VideoCategoryName = findViewById(R.id.VideoCategoryName);
        VideoViews = findViewById(R.id.VideoViews);
        cvVideoChannel = findViewById(R.id.cvVideoChannel);

        vvVideo = findViewById(R.id.vvVideo);
        pbVideo = findViewById(R.id.pbVideo);
        sbVideo = findViewById(R.id.sbVideo);
        VideoDuration = findViewById(R.id.VideoDuration);
        VideoCurrentVIdeoTIme = findViewById(R.id.VideoCurrentVIdeoTIme);
        ibVideoPlayPause = findViewById(R.id.ibVideoPlayPause);
        flVideoBottomPanel = findViewById(R.id.flVideoBottomPanel);
        ibVideoFullScreen = findViewById(R.id.ibVideoFullScreen);
        videoFL = findViewById(R.id.videoFL);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        rvVideoComments = findViewById(R.id.rvVideoComments);
        videoCommentsAdapter = new VideoCommentsAdapter(mContext, videoCommentsList);
        linearLayoutManager = new LinearLayoutManager(mContext);
        rvVideoComments.setAdapter(videoCommentsAdapter);
        rvVideoComments.setLayoutManager(linearLayoutManager);

        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, vvVideo.getDuration());
        mediaSessionCompat = new MediaSessionCompat(mContext, "tag");
        mediaSessionCompat.setMetadata(builder.build());

        flVideoComments.setVisibility(View.GONE);




        setVideo();
        setViewForVideo();
        setVideoBufferUpdate();
        setVideOtherDetails();
        setVideoWatchTime();
        getVideoDetails();
        checkVideoHasCommentOrNot();
        getComments();
        setViews();



        cvVideoChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent channelIntent = new Intent(mContext, ChannelActivity.class);
                channelIntent.putExtra("channel_id", video_user_id);
                startActivity(channelIntent);
            }
        });

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







    }


    private void setVideoWatchTime() {
        String watch_time_id = mDatabase.child("videos").child(videoId).child("watch_time").push().getKey();
        HashMap<String, Object> mVideosWatchTimeDataMap = new HashMap<>();
        mVideosWatchTimeDataMap.put("video_id", videoId);
        mVideosWatchTimeDataMap.put("user_id", user_id);
        mVideosWatchTimeDataMap.put("video_stop_position", vvVideo.getCurrentPosition());
        mVideosWatchTimeDataMap.put("video_duration", vvVideo.getDuration());
        mVideosWatchTimeDataMap.put("timestamp", System.currentTimeMillis());



    }
    void setMediaPlayer(){
        mediaPlayer.stop();
    }

    private void setViews() {
        mDatabase.child("videos").child(videoId).child("views").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int view_count = (int) dataSnapshot.getChildrenCount();

                if (view_count == 1) {
                    VideoViews.setText(view_count + " view");
                } else {
                    VideoViews.setText(view_count + " views");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setViewForVideo() {
        if (vvVideo.isPlaying()) {
            String view_id = mDatabase.child("videos").child(videoId).child("views").push().getKey();
            HashMap<String, Object> mVideoViewDataMap = new HashMap<>();
            mVideoViewDataMap.put("video_id", videoId);
            mVideoViewDataMap.put("user_id", user_id);
            mVideoViewDataMap.put("timestamp", System.currentTimeMillis());
            mVideoViewDataMap.put("view_id", view_id);
            mDatabase.child("videos").child(videoId).child("views").child(view_id).updateChildren(mVideoViewDataMap);
        }
    }

    private void openCommentDialog() {
        final Dialog commentDialog = new Dialog(VideoActivity.this);
        commentDialog.setContentView(R.layout.dialog_comment_layout);
        commentDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        commentDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        commentDialog.show();

        CardView cvVideoCommentCancel = commentDialog.findViewById(R.id.cvVideoCommentCancel);
        CardView cvVideoComment = commentDialog.findViewById(R.id.cvVideoComment);
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
                String comment = etVideoComment.getText().toString();

                if (comment.isEmpty()) {
                    etVideoComment.setError("Cannot post empty comment");
                } else {
                    String comment_id = mDatabase.child("videos").child(videoId).child("comment").push().getKey();
                    HashMap<String, Object> mCommentDataMap = new HashMap<>();
                    mCommentDataMap.put("comment", comment);
                    mCommentDataMap.put("comment_id", comment_id);
                    mCommentDataMap.put("timestamp", System.currentTimeMillis());
                    mCommentDataMap.put("user_id", user_id);
                    mCommentDataMap.put("video_id", videoId);
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
        });
    }

    private void setVideOtherDetails() {
        long millis = video_duration;
        String videoDurationFormat = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
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
            //sets video to particular position in milliseconds
            vvVideo.seekTo(stopPosition);
            vvVideo.start();
            mHandler = new Handler();
            updateProgressBar();
        } else {
            vvVideo.setVideoPath(String.valueOf(video));
            vvVideo.seekTo(stopPosition);
            vvVideo.pause();
            ibVideoPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
            mHandler = new Handler();
            updateProgressBar();
        }
    }

    public void showNotification(Context context, String title, String messageBody, Bitmap videoThumbnailBitmap) {

        //Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_app_notification_icon);

        String channel_id = createNotificationChannel(context);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channel_id)
                .setContentTitle(title)
                .setContentText(messageBody)
                /*.setLargeIcon(largeIcon)*/
                .setSmallIcon(R.drawable.ic_launcher_foreground) //needs white icon with transparent BG (For all platforms)
                // Add media control buttons that invoke intents in your media service
                .addAction(R.drawable.ic_thumb_down_black_24dp, "Dislike", dislikePendingIntent) // #0
                .addAction(R.drawable.ic_replay_10_black_24dp, "Replay 10s", replay10sPendingIntent) // #1
                .addAction(R.drawable.ic_pause_black_24dp, "Pause", pausePendingIntent) // #2
                .addAction(R.drawable.ic_forward_10_black_24dp, "Forward 10s", forward10sPendingIntent)  // #3
                .addAction(R.drawable.ic_thumb_up_black_24dp, "Like", likePendingIntent)     // #4
                // Apply the media style template
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(2 /* #1: pause button */)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setLargeIcon(videoThumbnailBitmap);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) ((new Date(System.currentTimeMillis()).getTime() / 1000L) % Integer.MAX_VALUE) /* ID of notification */, notificationBuilder.build());
    }

    public static String createNotificationChannel(Context context) {

        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.
            String channelId = "Channel_id";

            // The user-visible name of the channel.
            CharSequence channelName = "Application_name";
            // The user-visible description of the channel.
            String channelDescription = "Application_name Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            boolean channelEnableVibrate = true;
//            int channelLockscreenVisibility = Notification.;

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
//            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            // Adds NotificationChannel to system. Attempting to create an existing notification

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            // Returns null for pre-O (26) devices.
            return null;
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
                    videoCommentsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getVideoDetails() {
        mDatabase.child("videos").child(videoId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                video_title = dataSnapshot.child("videoTitle").getValue(String.class);
                video_desc = dataSnapshot.child("videoDescription").getValue(String.class);
                video_thumbnail = dataSnapshot.child("videoThumbnail").getValue(String.class);
                video_uploaded_date = (long) dataSnapshot.child("timestamp").getValue();
                video_user_id = dataSnapshot.child("user_id").getValue(String.class);
                video_width = (Math.toIntExact(Long.valueOf(String.valueOf(dataSnapshot.child("videoWidth").getValue()))));
                video_height = (Math.toIntExact(Long.valueOf(String.valueOf(dataSnapshot.child("videoHeight").getValue()))));
                video_category_name = dataSnapshot.child("videoCategory").getValue(String.class);
                video_duration = (long) dataSnapshot.child("videoDuration").getValue();


                Glide.with(getApplicationContext()).load(video_thumbnail).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).into(ivVideoBG);
                VideoTitle.setText(video_title);
                VideoCategoryName.setText(video_category_name);

                if (video_desc.equals("")) {
                    VideoDesc.setText("No video description");
                    VideoDesc.setTypeface(null, Typeface.ITALIC);
                } else {
                    VideoDesc.setText(video_desc);
                }

                Calendar calendar = Calendar.getInstance();
                TimeZone tz = TimeZone.getDefault();
                calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                VideoDateUploaded.setText(sdf.format(video_uploaded_date));


                try {
                    URL url = new URL(video_thumbnail);
                    videoThumbnailBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    System.out.println(e);
                }

                mDatabase.child("users").child(video_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        channel_name = dataSnapshot.child("name").getValue().toString();
                        profile_pic = dataSnapshot.child("profile_image").getValue().toString();

                        VideoChannelName.setText(channel_name);
                        Glide.with(getApplicationContext()).load(profile_pic).into(civVideoChannelProfilePic);
                        Glide.with(getApplicationContext()).load(profile_pic).into(civVideoCommentProfilePic);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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
            video_id = intent.getStringExtra("video_id");
//            Toast.makeText(mContext, String.valueOf(openRepliesFragment), Toast.LENGTH_SHORT).show();

            if (openRepliesFragment == true) {
                Intent openRepliesIntent = new Intent("showReplies");
                openRepliesIntent.putExtra("comment_id", comment_id);
                openRepliesIntent.putExtra("video_id", video_id);
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
