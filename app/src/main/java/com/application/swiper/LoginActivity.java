package com.application.swiper;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    usertype type = usertype.newUser;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
//
        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        Intent intent = new Intent(this, MainActivity.class);
        if(savedInstanceState != null){
            // save exists, skip login page
            intent.putExtra("usertype", (usertype)savedInstanceState.get("usertype"));
            startActivity(intent);
            finish();
        }
        Button loginButton = this.findViewById(R.id.login);
        Button createButton = this.findViewById(R.id.create);
        Button continueButton = this.findViewById(R.id.signedout);

        loginButton.setOnClickListener(v -> {
            System.out.println("login clicked");
            System.out.println();
        });

        createButton.setOnClickListener(v -> {
            System.out.println("create clicked");
            System.out.println();
        });

        continueButton.setOnClickListener(v -> {
            type = usertype.newGuest;
            startActivity(intent);
            finish();
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
