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
    String userType = "newGuest";
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // initialize login helpers
        sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        Intent intent = new Intent(this, MainActivity.class);

        // initialize main application activity based on login status
        if(sharedPrefs != null && sharedPrefs.getBoolean("isLoggedIn", false)){
            // save exists, skip login page
            userType = sharedPrefs.getString("usertype", "newGuest");
            if(userType.equals("loggedInUser")){
                // user account already exists
                intent.putExtra("type", "user");
                intent.putExtra("username",sharedPrefs.getString("accountid", "null"));
                intent.putExtra("password", sharedPrefs.getString("password", "null"));
            }else{
                // guest account already exists
                intent.putExtra("type", "guest");
            }
            startActivity(intent);
            finish();
        }
        // no save exists, create new user / guest by default

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
            intent.putExtra("usertype", "newGuest");
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
    protected void onDestroy() {
        super.onDestroy();
        editor.apply();
    }
}
