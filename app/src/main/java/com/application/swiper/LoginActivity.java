package com.application.swiper;

import android.content.Intent;
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
        if(savedInstanceState != null){

        }
        Button loginButton = (Button)this.findViewById(R.id.login);
        Button createButton = (Button)this.findViewById(R.id.create);
        Button continueButton = (Button)this.findViewById(R.id.signedout);
        loginButton.setOnClickListener(view -> System.out.println("login clicked"));
        createButton.setOnClickListener(view -> System.out.println("create clicked"));
        continueButton.setOnClickListener(view -> System.out.println("continue clicked"));
        
    }
}
