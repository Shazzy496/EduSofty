package com.sharon.edusoft.SetbookPdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharon.edusoft.AdminPanel.Pdf;
import com.sharon.edusoft.R;


import java.util.ArrayList;
import java.util.List;

public class SetbookPdf extends AppCompatActivity {

    private Toolbar setbooktoolbar;
    private RecyclerView listView;
    private List<Pdf> uploadList=new ArrayList<>();
    private SetbookPdfAdapter setbookpdfAdapter;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private Context mContext;
//    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setbookpdf);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext= SetbookPdf.this;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");
        listView = findViewById(R.id.listView);
        setbookpdfAdapter=new SetbookPdfAdapter(mContext,uploadList);
        linearLayoutManager = new LinearLayoutManager(mContext);
        listView.setAdapter(setbookpdfAdapter);
        listView.setLayoutManager(linearLayoutManager);

        setbooktoolbar = findViewById(R.id.setbooktoolbar);
        setbooktoolbar.setTitle("SetBook Pdf");
        setSupportActionBar(setbooktoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setbooktoolbar .setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        setbooktoolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }});

        if (currentUser != null) {
            String user_id = currentUser.getUid();

        }

        getAllPdf();

    }
    private void getAllPdf() {
        mDatabase.child("UploadedPdf").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                    Pdf pdff=snapshot.getValue(Pdf.class);
                    uploadList.add(pdff);
                    setbookpdfAdapter.notifyDataSetChanged();

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


}
