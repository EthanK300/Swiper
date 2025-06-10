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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    String userType = "newGuest";
    String urlString = "http://localhost:8000";    // TODO: when testing, replace this and the one in network_securityconfig.xml with the right address
    String pageOn = "start"; // start, login, register
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    OkHttpClient client;
    Intent intent;
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
    TextView loginUserPrompt;
    TextView loginPasswordPrompt;
    TextView registerUserPrompt;
    TextView registerEmailPrompt;
    TextView registerPasswordPrompt;
    TextView registercPasswordPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        context = this.getApplicationContext();
        // initialize login helpers
        sharedPrefs = this.getSharedPreferences("tempData", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        intent = new Intent(this, MainActivity.class);
        client = new OkHttpClient();

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
        // no save exists, create new user / guest by default / load saved state
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

        loginUserPrompt = this.findViewById(R.id.user_login_prompt);
        loginUserEmail = this.findViewById(R.id.user_login_input);
        loginPasswordPrompt = this.findViewById(R.id.user_password_prompt);
        loginUserPassword = this.findViewById(R.id.user_password_input);

        registerUsername = this.findViewById(R.id.register_user_input);
        registerUserPrompt = this.findViewById(R.id.register_user_prompt);
        registerPasswordPrompt = this.findViewById(R.id.register_cpassword_prompt);
        registerEmailPrompt = this.findViewById(R.id.register_email_prompt);
        registerEmail = this.findViewById(R.id.register_email_input);
        registerPassword = this.findViewById(R.id.register_password_input);
        registerPasswordPrompt = this.findViewById(R.id.register_password_prompt);
        registercPassword = this.findViewById(R.id.register_cpassword_input);
        registercPasswordPrompt = this.findViewById(R.id.register_cpassword_prompt);

        // create view sets for login and register
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

        loginSet.connect(R.id.options, ConstraintSet.TOP, R.id.login_group, ConstraintSet.BOTTOM, marginInPx);
        registerSet.connect(R.id.options, ConstraintSet.TOP, R.id.register_group, ConstraintSet.BOTTOM, marginInPx);

        loginGroup.setVisibility(GONE);
        registerGroup.setVisibility(GONE);
        options.setVisibility(GONE);

        if(savedInstanceState.getString("pageOn", "start").equals("login")){
            loginUserEmail.setText(savedInstanceState.getString("user", ""));
            loginUserPassword.setText(savedInstanceState.getString("password", ""));
            loginSet.applyTo(main);
            pageOn = "login";
        }else if(savedInstanceState.getString("pageOn", "start").equals("register")){
            registerUsername.setText(savedInstanceState.getString("user", ""));
            registerEmail.setText(savedInstanceState.getString("email", ""));
            registerPassword.setText(savedInstanceState.getString("password", ""));
            registercPassword.setText(savedInstanceState.getString("cpassword", ""));
            registerSet.applyTo(main);
            pageOn = "register";
        }

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
                boolean exit = false;
                if(user.equals("")){
                    loginUserPrompt.setText("Username/Email (must not be blank)");
                    exit = true;
                }else{
                    loginUserPrompt.setText("Username/Email");
                }
                if(password.equals("")){
                    loginPasswordPrompt.setText("Password (must not be blank)");
                    exit = true;
                }else{
                    loginPasswordPrompt.setText("Password");
                }
                if(exit){
                    return;
                }
                authLogin(user, password);
            }else if(pageOn.equals("register")){
                // attempt to create new account
                String user = registerUsername.getText().toString();
                String email = registerEmail.getText().toString();
                String password = registerPassword.getText().toString();
                String cpassword = registercPassword.getText().toString();
                boolean exit = false;

                if(user.equals("")){
                    registerUserPrompt.setText("Username (must not be blank)");
                    exit = true;
                }else{
                    registerUserPrompt.setText("Username");
                }
                if(email.equals("")){
                    registerEmailPrompt.setText("Email (must not be blank)");
                    exit = true;
                }else{
                    registerEmailPrompt.setText("Email");
                }
                if(password.equals("")){
                    registerPasswordPrompt.setText("Password (must not be blank)");
                    exit = true;
                }else{
                    registerPasswordPrompt.setText("Password");
                }
                // run validation on account
                if(!password.equals(cpassword)){
                    // don't allow, passwords don't match
                    registercPasswordPrompt.setText("Confirm Password (passwords must match)");
                    exit = true;
                }else{
                    registercPasswordPrompt.setText("Confirm Password");
                }
                if(exit){
                    return;
                }
                if(verify("email", email)){
                    // all good, send web request and log in
                    if(verify("username", user)){
                        authRegister(user, email, password);
                    }else{
                        registerUserPrompt.setText("Username (must not contain special characters");
                    }
                }else{
                    registerEmailPrompt.setText("Email (must not contain special characters)");
                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(pageOn.equals("login")){
            outState.putString("pageOn", "login");
            outState.putString("user", loginUserEmail.getText().toString());
            outState.putString("password", loginUserPassword.getText().toString());
        }else if(pageOn.equals("register")){
            outState.putString("pageOn", "register");
            outState.putString("user", registerUsername.getText().toString());
            outState.putString("email", registerEmail.getText().toString());
            outState.putString("password", registerPassword.getText().toString());
            outState.putString("cpassword", registercPassword.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.commit();
    }

    protected void authLogin(String user, String password){
        RequestBody requestBody = new FormBody.Builder()
                .add("username", user)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(urlString + "/login")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                System.out.println("received response: " + response.code() + ", body: " + response.message().toString());
                if(response.isSuccessful()){
                    System.out.println("logged in successfully");
                    runOnUiThread(() -> {
                        startActivity(intent);
                    });
                }else{
                    System.err.println("server response return error on auth login");
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("couldn't get response");
            }
        });
    }

    protected void authRegister(String user, String email, String password){
        RequestBody requestBody = new FormBody.Builder()
                .add("username", user)
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(urlString + "/register")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                System.out.println("received response: " + response.code() + ", body: " + response.message().toString());
                if(response.isSuccessful()){
                    // good
                    System.out.println("registered successfully");
                    runOnUiThread(() -> {
                        startActivity(intent);
                    });
                }else{
                    System.err.println("server response return error on auth register");
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("couldn't get response");
            }
        });
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
