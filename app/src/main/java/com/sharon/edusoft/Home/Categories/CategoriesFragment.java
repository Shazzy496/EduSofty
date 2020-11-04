package com.sharon.edusoft.Home.Categories;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharon.edusoft.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

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

        categoryNameRV = view.findViewById(R.id.categoryNameRV);
        categoryNameAdapter = new CategoryNameAdapter(mContext, categoryNameList);
        linearLayoutManager = new LinearLayoutManager(mContext);
        categoryNameRV.setAdapter(categoryNameAdapter);
        categoryNameRV.setLayoutManager(linearLayoutManager);
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
        CategoryName categoryNameComedy = new CategoryName("Comedy");
        CategoryName categoryNamePoems = new CategoryName("Poems");
        CategoryName categoryNameAutosMusic = new CategoryName("Music");
        CategoryName categoryNameDrama = new CategoryName("Drama");

        categoryNameList.add(categoryNameKiswahili_Setbooks);
        categoryNameList.add(categoryNameEnglish_Setbooks);
        categoryNameList.add(categoryNameOther_plays);
        categoryNameList.add(categoryNameComedy);
        categoryNameList.add(categoryNamePoems);
        categoryNameList.add(categoryNameAutosMusic);
        categoryNameList.add(categoryNameDrama);
        categoryNameAdapter.notifyDataSetChanged();
    }
}
