package com.srdp.harmonystride.activity;

import android.os.Bundle;

import com.srdp.harmonystride.R;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                navigateTo(LoginActivity.class);
                finish();
            }
        };timer.schedule(timerTask,2000);

    }
}