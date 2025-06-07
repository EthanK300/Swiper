package com.application.swiper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class LoginActivity extends AppCompatActivity {
    String userType = "newGuest";
    String pageOn = "start"; // start, login, register
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    Context context;
    Button loginButton;
    Button createButton;
    Button continueButton;
    Button backButton;
    Button submitButton;
    ConstraintLayout main;
    ConstraintLayout startGroup;
    ConstraintLayout loginGroup;
    ConstraintLayout registerGroup;
    ConstraintLayout options;
    ConstraintSet loginSet;
    ConstraintSet registerSet;
    EditText loginUserEmail;
    EditText loginUserPassword;
    EditText registerUsername;
    EditText registerEmail;
    EditText registerPassword;
    EditText registercPassword;
    TextView cpasswordPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        context = this.getApplicationContext();
        // initialize login helpers
        sharedPrefs = this.getSharedPreferences("tempData", Context.MODE_PRIVATE);
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
        main = this.findViewById(R.id.main);
        loginButton = this.findViewById(R.id.login);
        createButton = this.findViewById(R.id.create);
        continueButton = this.findViewById(R.id.signedout);
        backButton = this.findViewById(R.id.backButton);
        submitButton = this.findViewById(R.id.submit);
        startGroup = this.findViewById(R.id.start_group);
        loginGroup = this.findViewById(R.id.login_group);
        registerGroup = this.findViewById(R.id.register_group);
        options = this.findViewById(R.id.options);
        loginUserEmail = this.findViewById(R.id.user_login_input);
        loginUserPassword = this.findViewById(R.id.user_password_input);
        registerUsername = this.findViewById(R.id.register_user_input);
        registerEmail = this.findViewById(R.id.register_email_input);
        registerPassword = this.findViewById(R.id.register_password_input);
        registercPassword = this.findViewById(R.id.register_cpassword_input);
        cpasswordPrompt = this.findViewById(R.id.register_cpassword_prompt);

        loginSet = new ConstraintSet();
        loginSet.clone(main);
        registerSet = new ConstraintSet();
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
            pageOn = "login";
        });

        createButton.setOnClickListener(v -> {
            registerSet.applyTo(main);
            pageOn = "register";
        });

        continueButton.setOnClickListener(v -> {
            intent.putExtra("type", "newGuest");
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
            System.out.println("continue button clicked");
            if(pageOn.equals("login")){
                // attempt to login
                String user = loginUserEmail.getText().toString();
                String password = loginUserPassword.getText().toString();
                System.out.println("entered with user: " + user + ", password: " + password);
            }else if(pageOn.equals("register")){
                // attempt to create new account
                String user = registerUsername.getText().toString();
                String email = registerEmail.getText().toString();
                String password = registerPassword.getText().toString();
                String cpassword = registercPassword.getText().toString();
                // run validation on account
                if(!password.equals(cpassword)){
                    // don't allow, passwords don't match
                    cpasswordPrompt.setText("Confirm Password (passwords must match)");
                    return;
                }
                if(verify("email", email) && verify("username", "user")){
                    // all good, send web request and log in
                    
                }
            }
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
        editor.commit();
    }

    protected boolean verify(String toTest, String arg){
        if(arg.equals("email")){
            // ensure email is formatted correctly
            boolean containsAt = false;
            for(int i = 0; i < toTest.length(); i++){
                if(toTest.charAt(i) == '@'){
                    containsAt = true;
                }else if(!Character.isLetterOrDigit(toTest.charAt(i))){
                    return false;
                }
            }
            return containsAt;
        }else{
            //ensure username is formatted correctly
            for(int i = 0; i < toTest.length(); i++){
                if(!Character.isLetterOrDigit(toTest.charAt(i))){
                    return false;
                }
            }
            return true;
        }
    }
}
