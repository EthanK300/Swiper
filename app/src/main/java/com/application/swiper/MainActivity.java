package com.application.swiper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

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
    FrameLayout frameLayout;
    List<Fragment> fragments = new ArrayList<Fragment>();
    String[] labels = {"Today","This Week", "All", "Calendar"};
    TextView title;
    TextView noContentMessage;
    AppDatabase db;
    DataManager dm;
    boolean hasItems = true;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.main_activity);
        settings = this.findViewById(R.id.settingsgear);
        add = this.findViewById(R.id.add);
        aiAssist = this.findViewById(R.id.aitool);
        profile = this.findViewById(R.id.profile_picture);
        tabLayout = this.findViewById(R.id.tab_layout);
        title = this.findViewById(R.id.pageTitle);
        noContentMessage = this.findViewById(R.id.noContentMessage);
        frameLayout = this.findViewById(R.id.fragment_container);

        System.out.println("session type: " + intent.getStringExtra("type"));
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "swiper-data").build();
        dm = db.dataManager();

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
            Fragment f = PageFragment.newInstance(s);
            fragments.add(f);
            ft.add(R.id.fragment_container, f);
            if(s.equals("Calendar")){
                ft.show(f);
            }else{
                tabLayout.addTab(tabLayout.newTab().setText(s));
                ft.hide(f);
            }
        }
        ft.commit();
        // TODO: fix this so that the page that the user was on last is saved and re-used when they reopen the app
        // TODO: when adding the database calls update the hasItems variable
        ViewGroup tabStrip = (ViewGroup) tabLayout.getChildAt(0);

        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            View tabView = tabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
            params.setMargins(0, 0, 0, 0); // Left and right margin
            tabView.setPadding(0, 0, 0, 0);
            tabView.requestLayout();

        }

        // Handle tab switching
        // TODO: add calendar button functionality to this listener or another equivalent
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                for(Fragment f : fragments){
                    ft.hide(f);
                }
                System.out.println("pos:" + tab.getPosition());
                Fragment f = fragments.get(tab.getPosition());
                String which = f.getArguments().getString("type", "err");
                if(which.equals("err")){
                    System.err.println("error fetching args");
                }else{
                    title.setText(which);
                }
                ft.show(f).commit();
                if(hasItems){
                    frameLayout.setVisibility(VISIBLE);
                    noContentMessage.setVisibility(GONE);
                }else{
                    frameLayout.setVisibility(GONE);
                    noContentMessage.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if(hasItems){
            frameLayout.setVisibility(VISIBLE);
            noContentMessage.setVisibility(GONE);
        }else{
            frameLayout.setVisibility(GONE);
            noContentMessage.setVisibility(VISIBLE);
        }
    }
}
