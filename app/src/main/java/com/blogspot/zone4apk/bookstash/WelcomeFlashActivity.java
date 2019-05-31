package com.blogspot.zone4apk.bookstash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import static java.lang.Thread.sleep;

public class WelcomeFlashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().
                setContentView(R.layout.activity_welcome_flash);
        mAuth = FirebaseAuth.getInstance();

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseDatabase.getInstance().goOnline();
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (currentUser != null)
                        if (currentUser.isEmailVerified())
                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                        else
                            startActivity(new Intent(getApplicationContext(), VerifyAccountActivity.class));
                    else
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        });
        thread.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
    }
}
