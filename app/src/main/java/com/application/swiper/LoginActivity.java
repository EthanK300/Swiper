package com.application.swiper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
                // TODO: change this to a more secure local session storage alternative
            }else{
                // guest account already exists
                intent.putExtra("type", "guest");
            }
            startActivity(intent);
            finish();
        }
        // no save exists, create new user / guest by default

        // create references and set starting visibilities
        Button loginButton = this.findViewById(R.id.login);
        Button createButton = this.findViewById(R.id.create);
        Button continueButton = this.findViewById(R.id.signedout);
        Button backButton = this.findViewById(R.id.backButton);
        ConstraintLayout startGroup = this.findViewById(R.id.start_group);
        ConstraintLayout loginGroup = this.findViewById(R.id.login_group);
        ConstraintLayout registerGroup = this.findViewById(R.id.register_group);
        loginGroup.setVisibility(GONE);
        registerGroup.setVisibility(GONE);
        backButton.setVisibility(GONE);

        loginButton.setOnClickListener(v -> {
            startGroup.setVisibility(GONE);
            registerGroup.setVisibility(GONE);
            loginGroup.setVisibility(VISIBLE);
            backButton.setVisibility(VISIBLE);
        });

        createButton.setOnClickListener(v -> {
            startGroup.setVisibility(GONE);
            loginGroup.setVisibility(GONE);
            registerGroup.setVisibility(VISIBLE);
            backButton.setVisibility(VISIBLE);
        });

        continueButton.setOnClickListener(v -> {
            intent.putExtra("usertype", "newGuest");
            startActivity(intent);
            finish();
        });

        backButton.setOnClickListener(v -> {
            loginGroup.setVisibility(GONE);
            registerGroup.setVisibility(GONE);
            startGroup.setVisibility(VISIBLE);
            backButton.setVisibility(GONE);
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
