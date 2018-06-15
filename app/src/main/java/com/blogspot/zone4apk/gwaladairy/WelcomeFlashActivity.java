package com.blogspot.zone4apk.gwaladairy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static java.lang.Thread.sleep;

public class WelcomeFlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().
        setContentView(R.layout.activity_welcome_flash);

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    finish();
                }
            }
        });
        thread.start();
    }
}
