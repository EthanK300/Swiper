package com.application.swiper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements CreateFormSheet.OnFormSubmittedListener{
    Intent intent;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    ExecutorService executor;
    AppDatabase db;
    DataManager dm;
    OkHttpClient client;
    Gson gson;

    String[] labels = {"Today","This Week", "All", "Calendar"};
    boolean hasItems = false;
    int currentTab = -1;
    int prevTab = -1;
    List<Task> tasksList;
    List<Task> shownTasks;

    ImageButton settings;
    ImageButton add;
    ImageButton aiAssist;
    ShapeableImageView profile;
    TabLayout tabLayout;
    TextView title;
    TextView noContentMessage;
    RecyclerView item_container;
    TaskAdapter adapter;
    String userType; // user, newUser, guest, newGuest
    String username;
    String password;

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
        sharedPrefs = this.getSharedPreferences("tempData", MODE_PRIVATE);
        gson = new Gson();
        editor = sharedPrefs.edit();
        executor = Executors.newSingleThreadExecutor();
        client = WebHandler.init();
        item_container = this.findViewById(R.id.item_container);
        tasksList = new ArrayList<Task>();
        shownTasks = new ArrayList<Task>();

        // select starting tab
        for(int i = 0; i < labels.length; i++){
            String s = labels[i];
            if(!s.equals("Calendar")){
                tabLayout.addTab(tabLayout.newTab().setText(s));
            }
        }
        userType = intent.getStringExtra("type");
        System.out.println("session type: " + userType);
        // open and create database connection if the user is a guest
        if(userType.equals("guest") || userType.equals("newGuest")){
            editor.putBoolean("isLoggedIn", true);
            editor.commit();
            executor.execute(() -> {
                db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "swiper-data").build();
                dm = db.dataManager();
                if (dm.getTotalNum() == 0){
                    hasItems = false;
                    System.out.println("no items loaded");
                }else{
                    hasItems = true;
                    tasksList = dm.getAll();
                    updateContentView();
                    System.out.println("items loaded");
                }
                System.out.println("database loaded successfully");
            });
        }else if(userType.equals("user")){
            username = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
            // TODO: create web api connection to grab data from online
            String jsonString = retrieveList();
            if(jsonString.equals("")){
                System.out.println("error retrieving json string");
            }
            Type listType = new TypeToken<List<Task>>(){}.getType();
            tasksList = gson.fromJson(jsonString, listType);
        }else{
            // should only be type: newUser here
        }

        item_container.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(shownTasks);
        item_container.setAdapter(adapter);

        settings.setOnClickListener(v -> {
            System.out.println("settings clicked");
            System.out.println("ctab: " + currentTab);
            System.out.println("tasklist size: " + tasksList.size() + ", shownlist size: " + shownTasks.size());
        });

        add.setOnClickListener(v -> {
            long timestamp = Instant.now().toEpochMilli();
            System.out.println("timestamp for add: " + timestamp);
            CreateFormSheet c = new CreateFormSheet();
            c.show(getSupportFragmentManager(), "formSheet");
        });

        aiAssist.setOnClickListener(v -> {
            System.out.println("aiAssist clicked");
            // temporary testing for times
            tasksList.get(0).dueDate = 0;
            updateContentView();
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

        // TODO: fix this so that the page that the user was on last is saved and re-used when they reopen the app
        ViewGroup tabStrip = (ViewGroup) tabLayout.getChildAt(0);

        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            View tabView = tabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
            params.setMargins(0, 0, 0, 0); // Left and right margin
            tabView.setPadding(0, 0, 0, 0);
            tabView.requestLayout();
            tabView.setBackgroundColor(Color.WHITE);
        }

        // Handle tab switching
        // TODO: add calendar button functionality to this listener or another equivalent
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                tab.view.setBackgroundColor(Color.BLUE);
                // hide views that are out of the time range
                if(tab.getPosition() == 0){             // today
                    System.out.println("selected tab today");
                }else if(tab.getPosition() == 1){       // this week
                    System.out.println("selected tab this week");
                }else if(tab.getPosition() == 2){        // all
                    System.out.println("selected tab all");
                }else if(tab.getPosition() == 3){       // calendar
                    System.out.println("selected tab calendar");
                }else{
                    System.err.println("error reading tab position");
                }
                updateContentView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v = tab.view;
                v.setBackgroundColor(Color.WHITE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        System.out.println("selected default tab 0");
        // select previous tab if there is one
        tabLayout.selectTab(null);
        tabLayout.selectTab(tabLayout.getTabAt(currentTab));
        updateContentView();
    }

    @Override
    protected void onDestroy(){
        editor.putInt("currentTab", currentTab);
        System.out.println("saving tab: " + currentTab);
        System.out.println("save success? : " + editor.commit());
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        System.out.println("onpause called");
        updateDatabase();
        super.onPause();
    }

    protected void addTask(Task t){
        tasksList.add(t);
        executor.execute(() -> {
            dm.addTask(t);
        });
        updateContentView();
    }

    protected void updateContentView(){
        if(prevTab == currentTab){
            return;
        }
        switch(currentTab){
            case 0:     // today
                getTasksBetweenTimes("today");
                break;
            case 1:     // this week
                getTasksBetweenTimes("week");
                break;
            case 2:     //all
                shownTasks.clear();
                shownTasks.addAll(tasksList);
                break;
            case 3:     //calendar
                break;
            default:
                break;
        }
        if(shownTasks.isEmpty()){
            hasItems = false;
            noContentMessage.setVisibility(VISIBLE);
        }else{
            hasItems = true;
            noContentMessage.setVisibility(GONE);
        }
        adapter.notifyDataSetChanged();     // TODO: make this better / more efficient
    }

    protected void getTasksBetweenTimes(String query){
        // today < week < all
        Long start = new Long(0);
        Long end = new Long(0);
        ZonedDateTime startDate = null;
        ZonedDateTime endDate = null;
        LocalDate today = LocalDate.now();
        if(query.equals("today")){
            startDate = today.atStartOfDay(ZoneId.systemDefault());
            endDate = today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
        }else if(query.equals("week")){
            startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).atStartOfDay().atZone(ZoneId.systemDefault());
            endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
        }
        start = startDate.toInstant().toEpochMilli();
        end = endDate.toInstant().toEpochMilli();
        shownTasks.clear();
        System.out.println("cleaning shownTasks list");
        for(int i = 0; i < tasksList.size(); i++){
            long time = tasksList.get(i).dueDate;
            if(time > start && time < end){
                shownTasks.add(tasksList.get(i));
                // within range, display task
            }
        }
        System.out.println("new shownTasks list size: " + shownTasks.size());
    }
    @Override
    public void onFormSubmitted(String name, String description, long dueDate) {
        System.out.println("received data from form in mainactivity");
        Task t = new Task(name, description, dueDate);
        addTask(t);
    }

    // TODO: BEFORE IMPLEMENTING THIS FIND A WAY TO STORE SESSIONS OF SOME TYPE OR USER IDS
    // using cookiejar, backend server will handle via express-session or something similar
    protected String retrieveList(){
        String res = "";
        Request request = new Request.Builder()
                .url(WebHandler.urlString + "/retrieve")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                System.out.println("received response: " + response.code() + ", body: " + response.message().toString());
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("couldn't get response");
            }
        });
        return res;
    }

    protected void updateDatabase(){
        if(userType.equals("user") || userType.equals("newUser")){
            JsonObject root = new JsonObject();
            JsonElement taskListJSON = gson.toJsonTree(tasksList);
            root.addProperty("username" , username);
            root.addProperty("password", password);
            root.add("tasks", taskListJSON);

            String json = root.toString();
            RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(WebHandler.urlString + "/update")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if(response.isSuccessful()){
                        System.out.println("server response returned successful");
                    }else{
                        System.err.println("server response returned unsuccessful");
                    }
                    System.out.println("received response: " + response.code() + ", body: " + response.message().toString());
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    System.out.println("couldn't get response");
                }
            });
        }else{
            // usertype is guest or newGuest
            // TODO: sync current task list to local ROOM database
            executor.execute(() -> {
                dm.updateAll(tasksList);
            });
        }
    }


}
