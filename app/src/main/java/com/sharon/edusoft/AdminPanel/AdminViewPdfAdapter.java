package com.sharon.edusoft.AdminPanel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharon.edusoft.MyVideos.MyVideos;
import com.sharon.edusoft.R;
import com.sharon.edusoft.SetbookPdf.ViewPdf;

import java.util.List;

public class AdminViewPdfAdapter extends FirebaseRecyclerAdapter<Pdf,AdminViewPdfAdapter.ViewHolder> {
    private Context mContext;
    private List<Pdf> uploadList;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    public AdminViewPdfAdapter(FirebaseRecyclerOptions<Pdf> options,Context mContext){
        super(options);
        this.mContext=mContext;
    }
    @NonNull
    @Override
    public AdminViewPdfAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerowdesign,parent,false);
        return  new ViewHolder(view);
    }
    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i, @NonNull final Pdf pdf) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("UploadedPdf");
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        viewHolder.header.setText(pdf.getName());
        viewHolder.widgetCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(viewHolder.widgetCard.getContext(), ViewPdf.class);
                intent.putExtra("name",pdf.getName());
                intent.putExtra("url",pdf.getUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                viewHolder.widgetCard.getContext().startActivity(intent);
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
                        FirebaseDatabase.getInstance().getReference().child("UploadedPdf").child(getRef(i).getKey())
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

    }
//    @Override
//    public int getItemCount() {
//        return uploadList.size();
//    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView header;
        ImageView myImage;
        CardView widgetCard;
        ImageButton delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            header=itemView.findViewById(R.id.header);
            myImage=itemView.findViewById(R.id.myImage);
            widgetCard=itemView.findViewById(R.id.widgetCard);
            delete=itemView.findViewById(R.id.delete);
        }
    }
}
