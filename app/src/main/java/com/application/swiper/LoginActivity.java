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
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
//        startActivity(new Intent(this, MainActivity.class));
        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        if(savedInstanceState != null){
            // save exists, skip login page
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra();
            startActivity(intent);
            finish();
        }
        Button loginButton = (Button)this.findViewById(R.id.login);
        Button createButton = (Button)this.findViewById(R.id.create);
        Button continueButton = (Button)this.findViewById(R.id.signedout);
        loginButton.setOnClickListener(view -> System.out.println("login clicked"));
        createButton.setOnClickListener(view -> System.out.println("create clicked"));
        continueButton.setOnClickListener(view -> System.out.println("continue clicked"));

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
