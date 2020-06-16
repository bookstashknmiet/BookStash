package com.blogspot.zone4apk.bookstash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyAccountActivity extends AppCompatActivity {
    private static final String TAG = "VerifyAccountActivity";
    FirebaseAuth mAuth;
    FloatingActionButton buttonVerify;
    ProgressDialog progressDialog;
    Thread thread;
    TextView status;
    static int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        buttonVerify = findViewById(R.id.send_email_verify_btn);
        status = findViewById(R.id.tv_waiting);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                time = 30;
                while (!mAuth.getCurrentUser().isEmailVerified() && time > 0) {
                    Log.i("Verification Status", "Not Verified-".concat(String.valueOf(time)));
                    try {
                        mAuth.getCurrentUser().reload();
                        Thread.sleep(2000);
                        time -= 2;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (mAuth.getCurrentUser().isEmailVerified() && time >= 0) {
                    Log.i("Verification Status", "Verified");
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    finish();
                } else {
                    Log.i("Verification Status", "Session timeout-Logging out ");
                    mAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        });
        thread.start();
    }

    public void sendEmailVerification(View view) {
        // Disable button
        buttonVerify.setVisibility(View.INVISIBLE);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            findViewById(R.id.tv_verify).setVisibility(View.INVISIBLE);
                            findViewById(R.id.tv_waiting).setVisibility(View.VISIBLE);
                            if (thread.isAlive()) {
                                time = 45;
                                //Increasing the verification timeout by 45 seconds
                            }
                            Toast.makeText(getApplicationContext(),
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            findViewById(R.id.tv_waiting).setVisibility(View.INVISIBLE);
                            findViewById(R.id.tv_verify).setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getApplicationContext(),
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                            buttonVerify.setVisibility(View.VISIBLE);
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }
}
