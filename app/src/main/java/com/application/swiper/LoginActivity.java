package com.application.swiper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class LoginActivity extends AppCompatActivity {
    String userType = "newGuest";
    String pageOn = "start"; // start, login, register
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        context = this.getApplicationContext();
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
        ConstraintLayout main = this.findViewById(R.id.main);
        Button loginButton = this.findViewById(R.id.login);
        Button createButton = this.findViewById(R.id.create);
        Button continueButton = this.findViewById(R.id.signedout);
        Button backButton = this.findViewById(R.id.backButton);
        Button submitButton = this.findViewById(R.id.submit);
        ConstraintLayout startGroup = this.findViewById(R.id.start_group);
        ConstraintLayout loginGroup = this.findViewById(R.id.login_group);
        ConstraintLayout registerGroup = this.findViewById(R.id.register_group);
        ConstraintLayout options = this.findViewById(R.id.options);

        ConstraintSet loginSet = new ConstraintSet();
        loginSet.clone(main);
        ConstraintSet registerSet = new ConstraintSet();
        registerSet.clone(main);

        loginSet.connect(
                R.id.options,                      // view to modify
                ConstraintSet.TOP,                 // which side of the view
                R.id.login_group,                  // target view
                ConstraintSet.BOTTOM,              // target side
                16                                 // margin in pixels
        );
        loginSet.setVisibility(R.id.start_group, GONE);
        loginSet.setVisibility(R.id.register_group, GONE);
        loginSet.setVisibility(R.id.login_group, VISIBLE);
        loginSet.setVisibility(R.id.options, VISIBLE);

        registerSet.connect(
                R.id.options,                      // view to modify
                ConstraintSet.TOP,                 // which side of the view
                R.id.register_group,               // target view
                ConstraintSet.BOTTOM,              // target side
                16                                 // margin in pixels
        );
        registerSet.setVisibility(R.id.start_group, GONE);
        registerSet.setVisibility(R.id.register_group, VISIBLE);
        registerSet.setVisibility(R.id.login_group, GONE);
        registerSet.setVisibility(R.id.options, VISIBLE);

        int optionSpacing = 32; // Spacing between the login/register groups and the options (back button or submit)

        int marginInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                optionSpacing,
                context.getResources().getDisplayMetrics()
        );

        loginGroup.setVisibility(GONE);
        registerGroup.setVisibility(GONE);
        options.setVisibility(GONE);

        loginSet.connect(R.id.options, ConstraintSet.TOP, R.id.login_group, ConstraintSet.BOTTOM, marginInPx);
        registerSet.connect(R.id.options, ConstraintSet.TOP, R.id.register_group, ConstraintSet.BOTTOM, marginInPx);

        // TODO: make animations for this so they don't instantly appear and disappear
        loginButton.setOnClickListener(v -> {
            loginSet.applyTo(main);
        });

        createButton.setOnClickListener(v -> {
            registerSet.applyTo(main);
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
            options.setVisibility(GONE);
        });

        submitButton.setOnClickListener(v -> {

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
