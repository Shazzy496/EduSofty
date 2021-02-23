package com.sharon.edusoft.Discover;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.sharon.edusoft.R;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;

    public  ViewPagerAdapter(FragmentActivity context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.layout_screen,container,false);

        ImageView logo=view.findViewById(R.id.logo);
        TextView intro_title=view.findViewById(R.id.intro_title);
        TextView intro_desc=view.findViewById(R.id.intro_desc);
        ImageView ind1=view.findViewById(R.id.ind1);
        ImageView ind2=view.findViewById(R.id.ind2);
        ImageView ind3=view.findViewById(R.id.ind3);
        ImageView back=view.findViewById(R.id.back);
        ImageView next=view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          DiscoverFragment.viewPager.setCurrentItem(position+1);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscoverFragment.viewPager.setCurrentItem(position-1);
            }
        });
        Button getStarted=view.findViewById(R.id.getStarted);
        switch (position){
            case 0:
            logo.setImageResource(R.drawable.english);
            ind1.setImageResource(R.drawable.selected);
            ind2.setImageResource(R.drawable.unselected);
            ind3.setImageResource(R.drawable.unselected);
            intro_title.setText("English Setbooks");
            intro_desc.setText("EduSoft contains all English setbooks plays as well as their respective pdfs that have been done over the years.");
           back.setVisibility(View.GONE);
           break;
            case 1:
                logo.setImageResource(R.drawable.kiswa);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.selected);
                ind3.setImageResource(R.drawable.unselected);
                intro_title.setText("Kiswahili Setbooks");
                intro_desc.setText("EduSoft contains all Kiswahili setbooks plays as well as their respective pdfs done over the years.");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                break;
            case 2:
                logo.setImageResource(R.drawable.music);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.selected);
                intro_title.setText("Drama");
                intro_desc.setText("This application brings you drama from different traveling theatres, stream the videos to educate yourself.");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.GONE);
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
