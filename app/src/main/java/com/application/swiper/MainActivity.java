package com.application.swiper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    ImageButton settings;
    ImageButton add;
    ImageButton aiAssist;
    ShapeableImageView profile;
    TabLayout tabLayout;
    List<Fragment> fragments = new ArrayList<Fragment>();
    String[] labels = {"Today","This Week", "All"};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.main_activity);
        System.out.println("session type: " + intent.getStringExtra("type"));
        settings = this.findViewById(R.id.settingsgear);
        add = this.findViewById(R.id.add);
        aiAssist = this.findViewById(R.id.aitool);
        profile = this.findViewById(R.id.profile_picture);
        tabLayout = findViewById(R.id.tab_layout);

        settings.setOnClickListener(v -> {
            System.out.println("settings clicked");
        });
        add.setOnClickListener(v -> {
            System.out.println("add clicked");
        });
        aiAssist.setOnClickListener(v -> {
            System.out.println("aiAssist clicked");
        });
        profile.setOnClickListener(v -> {
            System.out.println("profile clicked");
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for(String s : labels){
            tabLayout.addTab(tabLayout.newTab().setText(s));
            Fragment f = PageFragment.newInstance(s);
            fragments.add(f);
            ft.add(R.id.fragment_container, f);
        }
        ft.show(fragments.get(0)).commit();
        // TODO: fix this so that the page that the user was on last is saved and re-used when they reopen the app

        ViewGroup tabStrip = (ViewGroup) tabLayout.getChildAt(0);

        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            View tabView = tabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
            params.setMargins(0, 0, 0, 0); // Left and right margin
            tabView.setPadding(0, 0, 0, 0);
            tabView.requestLayout();

        }

        // Handle tab switching
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                for(Fragment f : fragments){
                    ft.hide(f);
                }
                System.out.println("pos:" + tab.getPosition());
                ft.show(fragments.get(tab.getPosition()));
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
