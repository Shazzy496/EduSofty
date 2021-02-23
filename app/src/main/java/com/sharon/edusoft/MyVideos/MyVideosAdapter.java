package com.sharon.edusoft.MyVideos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sharon.edusoft.EditVideoActivity;
import com.sharon.edusoft.R;
import com.sharon.edusoft.Video.VideoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MyVideosAdapter extends FirebaseRecyclerAdapter<MyVideos,MyVideosAdapter.ViewHolder> {

    private Context mContext;
    private List<MyVideos> myVideosList;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String name, bio;
    private String user_id;


    public MyVideosAdapter(FirebaseRecyclerOptions<MyVideos> options,Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyVideosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_video_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i, @NonNull final MyVideos myVideos) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        setVideoDetails(viewHolder, myVideos);
        viewHolder.cvMyVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoIntent = new Intent(mContext, VideoActivity.class);
                videoIntent.putExtra("video_id", myVideos.getVideo_id());
                videoIntent.putExtra("video", myVideos.getVideo());
                videoIntent.putExtra("video_duration", myVideos.getVideoDuration());
                videoIntent.putExtra("video_user_id", myVideos.getUser_id());
                mContext.startActivity(videoIntent);
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete this video?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("videos").child(getRef(i).getKey())
                                .setValue(null)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        viewHolder.ibMyVideosMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, viewHolder.ibMyVideosMoreOptions);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.my_videos_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit_my_videos_menu_item:
                                Intent editVideoIntent = new Intent(mContext, EditVideoActivity.class);
                                editVideoIntent.putExtra("videoId", myVideos.getVideo_id());
                                mContext.startActivity(editVideoIntent);
                                break;
                        }
                        return false;
                    }
                });
            }
        });
    }

    private void setVideoDetails(ViewHolder holder, MyVideos myVideos) {
        long millis = myVideos.getVideoDuration();
        String videoDurationFormat = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        Glide.with(mContext).load(myVideos.getVideoThumbnail()).into(holder.ivMyVideosVideoThumbnail);
        holder.MyVideosVideoTItle.setText(myVideos.getVideoTitle());
        holder.MyVideosVideoDesc.setText(myVideos.getVideoDesc());
        holder.MyVideosVideoDuration.setText(videoDurationFormat);

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy h:mm a", Locale.getDefault());
        holder.MyVideosVideoUploadedDate.setText("Uploaded on: " + sdf.format(myVideos.getTimestamp()));
    }
//
//
//    @Override
//    public int getItemCount() {
//        return myVideosList.size();
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
            MyVideosVideoDuration = itemView.findViewById(R.id.MyVideosVideoDuration);
            delete=itemView.findViewById(R.id.delete);

        }
    }
}
