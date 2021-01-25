package com.sharon.edusoft.Search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.sharon.edusoft.R;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    // Declare Variables

    Context mContext;
    TextView searchActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Generate sample data
        mContext=SearchActivity.this;
        searchActivity=findViewById(R.id.searchActivity);
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())){
            handleSearch(getIntent().getStringExtra(SearchManager.QUERY));
        }


    }

    private void handleSearch(String stringExtra) {
        searchActivity.setText(stringExtra);
    }


}
