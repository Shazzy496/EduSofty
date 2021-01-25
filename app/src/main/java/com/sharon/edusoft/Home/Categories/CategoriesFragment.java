package com.sharon.edusoft.Home.Categories;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharon.edusoft.R;
import com.sharon.edusoft.StudDashboard;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {
    private Toolbar fragmenttoolbar;
    private Context mContext;
    private RecyclerView categoryNameRV;
    private List<CategoryName> categoryNameList = new ArrayList<>();
    private CategoryNameAdapter categoryNameAdapter;
    private LinearLayoutManager linearLayoutManager;


    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        mContext = getActivity();
        fragmenttoolbar=view.findViewById(R.id.fragmenttoolbar);
        categoryNameRV = view.findViewById(R.id.categoryNameRV);
        categoryNameAdapter = new CategoryNameAdapter(mContext, categoryNameList);
        linearLayoutManager = new LinearLayoutManager(mContext);
        categoryNameRV.setAdapter(categoryNameAdapter);
        categoryNameRV.setLayoutManager(linearLayoutManager);
        fragmenttoolbar.setTitle("Video Category");
        ((AppCompatActivity)getActivity()).setSupportActionBar(fragmenttoolbar);
        fragmenttoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        fragmenttoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), StudDashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        categoryNameRV.setHasFixedSize(true);

        categoryNameList.clear();

        initCategoryNames();



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    private void initCategoryNames() {
        CategoryName categoryNameKiswahili_Setbooks = new CategoryName("Kiswahili Setbook Plays");
        CategoryName categoryNameEnglish_Setbooks = new CategoryName("English Setbook Plays");
        CategoryName categoryNameOther_plays = new CategoryName("Other Plays");
        CategoryName categoryNameDrama = new CategoryName("Drama");

        categoryNameList.add(categoryNameKiswahili_Setbooks);
        categoryNameList.add(categoryNameEnglish_Setbooks);
        categoryNameList.add(categoryNameOther_plays);
        categoryNameList.add(categoryNameDrama);
        categoryNameAdapter.notifyDataSetChanged();
    }
}
