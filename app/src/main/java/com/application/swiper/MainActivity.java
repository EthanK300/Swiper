package com.application.swiper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements TaskFormSheet.OnFormSubmittedListener, TaskAction, RecognitionListener {
    Intent intent;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    ExecutorService executor;
    AppDatabase db;
    DataManager dm;
    OkHttpClient client;
    Gson gson;
    private SpeechService speechService;
    private Model model;
    private Recognizer rec;
    long voiceCaptureDelay;
    String capturedText;
    Dialog micOverlay;

    String[] labels = {"Today","This Week", "All"};
    boolean hasItems = false;
    int currentTab = -1;
    int prevTab = -1;
    ArrayList<Task> tasksList;
    ArrayList<Task> shownTasks;

    ImageButton settings;
    ImageButton add;
    ImageButton calendar;
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
        calendar = this.findViewById(R.id.calendar);
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

        // attempt to load voice recognition model

        try {
            File m = unzip(this, "vosk-model-small-en-us-0.15.zip", "vosk-model-small-en-us-0.15");
            System.out.println("trying model path: " + m.getAbsolutePath());
            model = new Model(m.getAbsolutePath() + "/vosk-model-small-en-us-0.15");

            System.out.println("loaded");
        } catch (IOException e) {
            System.out.println("voice recognition loading error");
        }

        // select starting tab
        for(int i = 0; i < labels.length; i++){
            String s = labels[i];
            tabLayout.addTab(tabLayout.newTab().setText(s));
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
                    tasksList = (ArrayList) dm.getAll(); // TODO: watch make sure this doesn't cause problems with List<T>
                    updateContentView();
                    System.out.println("items loaded");
                }
                System.out.println("database loaded successfully");
            });
        }else if(userType.equals("user")){
            username = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
            String jsonString = retrieveList();
            if(jsonString.equals("")){
                System.out.println("error retrieving json string");
            }
            Type listType = new TypeToken<List<Task>>(){}.getType();
            tasksList = gson.fromJson(jsonString, listType);
        }else{
            // should only be type: newUser here
        }

        // bind recyclerview for main task management
        item_container.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(this, shownTasks);
        item_container.setAdapter(adapter);

        calendar.setOnClickListener(v -> {
            // calendar holder
            System.out.println("calendar clicked");
        });

        settings.setOnClickListener(v -> {
            // using this as a temporary test button
            System.out.println("ctab: " + currentTab);
            System.out.println("tasklist size: " + tasksList.size() + ", shownlist size: " + shownTasks.size());
            if(speechService != null) speechService.stop();
        });

        add.setOnClickListener(v -> {
            long timestamp = Instant.now().toEpochMilli();
            System.out.println("timestamp for add: " + timestamp);
            TaskFormSheet c = new TaskFormSheet(-1);
            c.show(getSupportFragmentManager(), "formSheet");
        });

        aiAssist.setOnClickListener(v -> {
            // attempt to recognize audio
            checkPermissions();
        });

        profile.setOnClickListener(v -> {
            System.out.println("profile clicked");
            View profileMenu = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.profile_menu, null);
            PopupWindow popupWindow = new PopupWindow(profileMenu,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);

            Button signoutButton = profileMenu.findViewById(R.id.sign_out);
            signoutButton.setOnClickListener(b -> {
                System.out.println("user clicked sign out");
                popupWindow.dismiss();
            });

            popupWindow.showAsDropDown(v, 0, 0);
        });

        int num = sharedPrefs.getInt("currentTab", -1);
        if(num != -1){
            currentTab = num;
            System.out.println("current tab: " + currentTab);
        }else{
            System.out.println("no current tab exists via shared");
            currentTab = 0;
        }

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

        if (speechService != null) {
            speechService.stop();
            speechService.shutdown();
        }
    }

    @Override
    protected void onPause(){
        System.out.println("onpause called");
        updateDatabase();
        if(speechService != null){
            speechService.stop();
        }
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
                title.setText("Today");
                break;
            case 1:     // this week
                getTasksBetweenTimes("week");
                title.setText("This Week");
                break;
            case 2:     //all
                title.setText("All Tasks");
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
        adapter.notifyDataSetChanged();     // TODO: make this better / more efficient to not use notifyDataSetChanged()
    }

    public void delayTask(int pos) {
        View showing = item_container.getChildAt(pos); // pos should reflect the shownTasks list position as well
        Task t = shownTasks.get(pos);
        System.out.println("DD: " + t.dueDate + ", new DD: " + (t.dueDate + 86400000));
        t.dueDate += 86400000;
        System.out.println("delayed task: " + ((TextView)showing.findViewById(R.id.card_text)).getText().toString());
        updateContentView();
    }

    public void completeTask(int pos) {
        View showing = item_container.getChildAt(pos); // pos should reflect the shownTasks list position as well
        Task t = shownTasks.get(pos);
        System.out.println("completed task: " + ((TextView)showing.findViewById(R.id.card_text)).getText().toString());
        t.done = true;
        updateContentView();
    }

    public void deleteTask(int pos){
        System.out.println("deleting task: " + pos);
        tasksList.remove(pos);
        updateContentView();
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
            if(tasksList.get(i).done){
                continue;
            }
            long time = tasksList.get(i).dueDate;
            if(time > start && time < end){
                shownTasks.add(tasksList.get(i));
                // within range, display task
            }
        }
        System.out.println("new shownTasks list size: " + shownTasks.size());
    }
    @Override
    public void onFormSubmitted(String name, String description, long dueDate, int pos) {
        if(pos == -1){
            // normal create
            System.out.println("received data from form in mainactivity");
            Task t = new Task(name, description, dueDate);
            addTask(t);
        }else{
            // edit the item at position 'pos'
            Task t = tasksList.get(pos);
            t.title = name;
            t.description = description;
            t.dueDate = dueDate;
            updateContentView();
        }

    }

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
            executor.execute(() -> {
                dm.updateAll(tasksList);
            });
        }
    }

    public static File unzip(Context context, String zipAssetName, String targetDirName) throws IOException {
        File targetDir = new File(context.getFilesDir(), targetDirName);
        // return if file already exists
        if (targetDir.exists()) {
            return targetDir;
        }
        // create unzipped file
        targetDir.mkdirs();
        InputStream assetZip = context.getAssets().open(zipAssetName);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(assetZip));
        ZipEntry entry;
        byte[] buffer = new byte[8192];

        while ((entry = zis.getNextEntry()) != null) {
            File outFile = new File(targetDir, entry.getName());

            if (entry.isDirectory()) {
                outFile.mkdirs();
            } else {
                File parent = outFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(outFile);
                int count;
                while ((count = zis.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
            }
            zis.closeEntry();
        }

        zis.close();
        return targetDir;
    }

    @Override
    public void onPartialResult(String hypothesis) {
        // do nothing with partials, only want the full request
    }

    @Override
    public void onResult(String hypothesis) {
        try{
            JSONObject j = new JSONObject(hypothesis);
            capturedText = j.getString("text");
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("attempting to execute: " + capturedText);
        speechService.stop();
        micOverlay.hide();
    }

    @Override
    public void onFinalResult(String hypothesis) {
        // also do nothing with final result, only want the full request
    }

    @Override
    public void onError(Exception exception) {
        exception.printStackTrace();
    }

    @Override
    public void onTimeout() {
        // didn't hear anything, maybe microphone is muted?
        speechService.stop();
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }else{
            attemptRecognition();
        }

    }

    private void attemptRecognition(){
        System.out.println("attempting recognition");
        if(rec == null){
            rec = new Recognizer(model, 16000.0f);
        }
        if(speechService == null){
            try {
                speechService = new SpeechService(rec, 16000.0f);
            }catch(IOException e){
                System.out.println("failed to start voice recognition");
            }
        }
        executor.execute(() -> {
            speechService.startListening(this);
            // listen until person stops speaking
        });
        micOverlay = new Dialog(this);
        micOverlay.setContentView(R.layout.mic_overlay);
        micOverlay.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // allow user to cancel early
        micOverlay.setOnCancelListener(dialogInterface -> {
            speechService.stop();
            System.out.println("user canceled ai mic assist");
        });
        micOverlay.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("granted permissions");
                attemptRecognition();
            } else {
                System.out.println("unable to get permissions");
                // unable to acquire permissions to record audio for the ai assist-er
            }
        }
    }
}