package com.sharon.edusoft.AdminPanel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.sharon.edusoft.MyVideos.MyVideos;
import com.sharon.edusoft.R;
import com.sharon.edusoft.SetbookPdf.SetbookPdf;
import com.sharon.edusoft.SetbookPdf.SetbookPdfAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminViewPdf extends AppCompatActivity {
    private Toolbar mySetbooks;
    private RecyclerView mySetbooksListView;
    private List<Pdf> uploadList=new ArrayList<>();
    private AdminViewPdfAdapter adminViewPdfAdapter;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_pdf);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext= AdminViewPdf.this;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");
        mySetbooksListView = findViewById(R.id.mySetbooksListView);
        FirebaseRecyclerOptions<Pdf> options =
                new FirebaseRecyclerOptions.Builder<Pdf>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("UploadedPdf"), Pdf.class)
                        .build();
        adminViewPdfAdapter=new AdminViewPdfAdapter(options,this);
        linearLayoutManager = new LinearLayoutManager(mContext);
        mySetbooksListView.setAdapter(adminViewPdfAdapter);
        mySetbooksListView.setLayoutManager(linearLayoutManager);

        mySetbooks = findViewById(R.id.mySetbooks);
        mySetbooks.setTitle("SetBook Pdf");
        setSupportActionBar(mySetbooks);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mySetbooks.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        mySetbooks.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }});
        getAllPdf();

    }

    private void getAllPdf() {
        mDatabase.child("UploadedPdf").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Pdf pdff = snapshot.getValue(Pdf.class);
                    uploadList.add(pdff);
                    adminViewPdfAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        adminViewPdfAdapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adminViewPdfAdapter.stopListening();
    }
}
