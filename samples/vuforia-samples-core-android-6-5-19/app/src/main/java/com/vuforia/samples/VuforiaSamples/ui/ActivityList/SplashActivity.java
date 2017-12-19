package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.content.Intent;

import com.vuforia.samples.VuforiaSamples.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }
}
