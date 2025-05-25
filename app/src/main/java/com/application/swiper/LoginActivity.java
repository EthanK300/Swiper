package com.application.swiper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    String userType = "newGuest";
    String pageOn = "start"; // start, login, register
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
        Button backButton = this.findViewById(R.id.backButton);

        backButton.setVisibility(GONE);

        loginButton.setOnClickListener(v -> {
            this.findViewById(R.id.start_group).setVisibility(GONE);
            this.findViewById(R.id.register_group).setVisibility(GONE);
            this.findViewById(R.id.login_group).setVisibility(VISIBLE);
            this.findViewById(R.id.backButton).setVisibility(VISIBLE);
        });

        createButton.setOnClickListener(v -> {
            this.findViewById(R.id.start_group).setVisibility(GONE);
            this.findViewById(R.id.login_group).setVisibility(GONE);
            this.findViewById(R.id.register_group).setVisibility(VISIBLE);
            this.findViewById(R.id.backButton).setVisibility(VISIBLE);
        });

        continueButton.setOnClickListener(v -> {
            intent.putExtra("usertype", "newGuest");
            startActivity(intent);
            finish();
        });

        backButton.setOnClickListener(v -> {
            this.findViewById(R.id.login_group).setVisibility(GONE);
            this.findViewById(R.id.register_group).setVisibility(GONE);
            this.findViewById(R.id.start_group).setVisibility(VISIBLE);
            this.findViewById(R.id.backButton).setVisibility(GONE);
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
        if(pageOn.equals("login")){

        }else if(pageOn.equals("register")){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.apply();
    }
}
