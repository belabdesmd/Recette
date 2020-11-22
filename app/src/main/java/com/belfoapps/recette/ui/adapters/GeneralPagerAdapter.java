package com.belfoapps.recette.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.belfoapps.recette.R;

import java.util.ArrayList;

public class GeneralPagerAdapter extends FragmentPagerAdapter {

    /*************************************** Declarations *****************************************/
    private ArrayList<Fragment> fragments;
    private Context context;
    public static int[] tabIcons = {R.drawable.home, R.drawable.categories,
            R.drawable.saved};

    /*************************************** Constructor ******************************************/
    public GeneralPagerAdapter(@NonNull FragmentManager fm, ArrayList<Fragment> fragments, Context context) {
        super(fm);
        this.fragments = fragments;
        this.context = context;
    }

    /*************************************** Methods **********************************************/
    public View getTabView(int position) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        TextView tv = view.findViewById(R.id.tab_title);
        tv.setText(context.getResources().getStringArray(R.array.main_tab_titles)[position]);
        ImageView img = view.findViewById(R.id.tab_icon);
        img.setImageResource(tabIcons[position]);
        ImageViewCompat.setImageTintList(img,
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.secondaryTextColor)));
        return view;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
