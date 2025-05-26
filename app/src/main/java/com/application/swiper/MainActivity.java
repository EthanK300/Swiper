package com.application.swiper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    ImageButton settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.main_activity);
        System.out.println("session type: " + intent.getStringExtra("type"));
        settingsButton = this.findViewById(R.id.settingsgear);
        settingsButton.setOnClickListener(v -> {
            System.out.println("settings clicked");
        });
    }
}
