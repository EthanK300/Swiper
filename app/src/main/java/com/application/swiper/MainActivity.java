package com.application.swiper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    ImageButton settings;
    ImageButton add;
    ImageButton aiAssist;
    ShapeableImageView profile;
    TabLayout tabLayout;
    List<Fragment> fragments;

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

        tabLayout.addTab(tabLayout.newTab().setText("Today"));
        tabLayout.addTab(tabLayout.newTab().setText("This Week"));
        tabLayout.addTab(tabLayout.newTab().setText("All"));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new PageFragment()) // TODO: finish this so that fragments work
                .commit();

        // Handle tab switching
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected = null;

                // TODO: finish this so that the fragments work
                switch (tab.getPosition()) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selected)
                        .commit();
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
