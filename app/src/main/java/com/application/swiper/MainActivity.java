package com.application.swiper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    boolean hasItems = false;
    boolean isGuest;
    List<Task> tasksList;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    int currentTab = -1;
    int prevTab = -1;
    ExecutorService executor;

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
        sharedPrefs = this.getSharedPreferences("tempData", MODE_PRIVATE);
        editor = sharedPrefs.edit();
        executor = Executors.newSingleThreadExecutor();


        System.out.println("session type: " + intent.getStringExtra("type"));
        // open and create database connection if the user is a guest
        if(intent.getStringExtra("type").equals("guest") || intent.getStringExtra("type").equals("newGuest")){
            editor.putBoolean("isLoggedIn", true);
            editor.commit();
            isGuest = true;
            // this is a background task thread - for database calls. if need to escape it, use runOnUiThread()
            executor.execute(() -> {
                db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "swiper-data").build();
                dm = db.dataManager();
                if (dm.getTotalNum() == 0){
                    hasItems = false;
                    System.out.println("no items loaded");
                }else{
                    hasItems = true;
                    tasksList = dm.getAll();
                    System.out.println("items loaded");
                }
                System.out.println("database loaded successfully");
            });
        }else{
            isGuest = false;
            // TODO: create web api connection to grab data from online
        }

        settings.setOnClickListener(v -> {
            System.out.println("settings clicked");
        });

        // TODO: finish the ui part of this
        add.setOnClickListener(v -> {
            System.out.println("add clicked");
            String name = "ab";
            String description = "ac";
            long dueDate = 10;
            Task t = new Task(name, description, dueDate);
            // TODO: make ui show up for task addition
            executor.execute(() -> {
                addTask(t);
            });
        });

        aiAssist.setOnClickListener(v -> {
            System.out.println("aiAssist clicked");
        });
        profile.setOnClickListener(v -> {
            System.out.println("profile clicked");
        });

        int num = sharedPrefs.getInt("currentTab", -1);
        if(num != -1){
            currentTab = num;
            System.out.println("current tab: " + currentTab);
        }else{
            System.out.println("no current tab exists via shared");
            currentTab = 0;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for(int i = 0; i < labels.length; i++){
            String s = labels[i];
            Fragment f = PageFragment.newInstance(s);
            fragments.add(f);
            ft.add(R.id.fragment_container, f);
            if(!s.equals("Calendar")){
                tabLayout.addTab(tabLayout.newTab().setText(s));
            }
            if(i == currentTab){
                ft.show(f);
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                tab.select();
            }else{
                ft.hide(f);
            }
        }
        ft.commit();

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
        // TODO: add calendar button functionality to this listener or another equivalent
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // hide existing fragments and show the correct one
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                for(Fragment f : fragments){
                    ft.hide(f);
                }
                currentTab = tab.getPosition();
                System.out.println("pos:" + currentTab);
                Fragment f = fragments.get(currentTab);

                // update the title
                String which = f.getArguments().getString("type", "err");
                if(which.equals("err")){
                    System.err.println("error fetching args");
                }else{
                    title.setText(which);
                }
                ft.show(f).commit();
                updateContentView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        updateContentView();
    }

    @Override
    protected void onDestroy(){
        editor.putInt("currentTab", currentTab);
        System.out.println("saving tab: " + currentTab);
        System.out.println("save success? : " + editor.commit());
        super.onDestroy();
    }

    protected void addTask(Task t){
        dm.addTask(t);
    }

    protected void updateContentView(){
        if(hasItems){

            if(prevTab == currentTab){
                return;
            }
            switch(currentTab){
                case 0:     // today
                    tasksList = getTasksBetweenTimes("today");
                    break;
                case 1:     // this week
                    tasksList = getTasksBetweenTimes("week");
                    break;
                case 2:     //all
                    break;
                case 3:     //calendar
                    break;
                default:
                    break;
            }
            frameLayout.setVisibility(VISIBLE);
            noContentMessage.setVisibility(GONE);
        }else{
            frameLayout.setVisibility(GONE);
            noContentMessage.setVisibility(VISIBLE);
        }
    }

    protected List<Task> getTasksBetweenTimes(String query){
        Long start = new Long(0);
        Long end = new Long(0);
        ZonedDateTime startDate = null;
        ZonedDateTime endDate = null;
        LocalDate today = LocalDate.now();
        if(query.equals("today")){

            startDate = today.atStartOfDay(ZoneId.systemDefault());
            endDate = ZonedDateTime.now();

        }else if(query.equals("week")){
            LocalDate sunday = today.with(DayOfWeek.SUNDAY);
            LocalDate nextSunday = sunday.plusWeeks(1);

            startDate = sunday.atStartOfDay(ZoneId.systemDefault());
            endDate = nextSunday.atStartOfDay(ZoneId.systemDefault());
        }
        start = startDate.toInstant().toEpochMilli();
        end = endDate.toInstant().toEpochMilli();
        return dm.getBetween(start, end);
    }
}
