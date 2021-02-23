package com.sharon.edusoft.SetbookPdf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharon.edusoft.AdminPanel.Pdf;

import com.sharon.edusoft.DarajaMpesa.MpesaActivity;
import com.sharon.edusoft.LoginActivity;
import com.sharon.edusoft.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
public class SetbookPdfAdapter extends RecyclerView.Adapter<SetbookPdfAdapter.ViewHolder> implements Filterable {
    private Context mContext;
    private List<Pdf> uploadList;
    private List<Pdf> uploadListFull;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private String ActivityIdentity="SetBookMpesa";


    public SetbookPdfAdapter(Context mContext, List<Pdf> uploadList){
        this.mContext=mContext;
        this.uploadList=uploadList;
        this.uploadListFull=new ArrayList<>(uploadList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerowdesign,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Pdf pdff=uploadList.get(position);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("UploadedPdf");
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        holder.header.setText(pdff.getName());

        if (currentUser != null) {
            String user_id = currentUser.getUid();
        }
        setPdfName(holder, pdff);

        holder.widgetCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("UserPdf transactions");
                    reference.orderByChild("id").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                         Intent intent=new Intent(holder.widgetCard.getContext(), ViewPdf.class);
                           intent.putExtra("name",pdff.getName());
                          intent.putExtra("url",pdff.getUrl());
                         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           holder.widgetCard.getContext().startActivity(intent);
                            } else {
                                 if (ActivityIdentity!=null) {
                                     Intent mpesaIntent = new Intent(mContext.getApplicationContext(), MpesaActivity.class);
                                     mpesaIntent.putExtra("identity",ActivityIdentity);
                                     mpesaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                     mContext.startActivity(mpesaIntent);
                                 }else {
                                     Toast.makeText(mContext.getApplicationContext(),"NULL IDENTITY",Toast.LENGTH_LONG).show();
                                 }
                            } }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


            }
        });
    }

    private void setPdfName(ViewHolder holder, Pdf pdff) {
        holder.header.setText(pdff.getName());
    }

    @Override
    public int getItemCount() {
        return uploadList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filt=new FilterResults();
            if (constraint==null|constraint.length()==0){
                filt.count=uploadListFull.size();
                filt.values=new ArrayList(uploadListFull);
            }else{
                String  search=constraint.toString().toLowerCase();
                List<Pdf> filteredList=new ArrayList<>();
                for (Pdf item : uploadListFull) {
                    if (item.getName().toLowerCase().contains(search)) {
                        filteredList.add(item);
                    }
                }
                filt.count=filteredList.size();
                filt.values=filteredList;
            }
            return filt;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            uploadList.clear();
           uploadList=(List<Pdf>) results.values;
            notifyDataSetChanged();
        }
    };

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
            delete.setVisibility(View.GONE);

        }
    }
}
