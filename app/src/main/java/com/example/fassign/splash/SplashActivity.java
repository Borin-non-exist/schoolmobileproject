package com.example.fassign.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fassign.R;
import com.example.fassign.welcome.WelcomeActivity;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_TIME_OUT = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
