package com.example.happydog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
public class LoadingSplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(LoadingSplashActivity.this, LoginActivity.class);
                LoadingSplashActivity.this.startActivity(mainIntent);
                LoadingSplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
