package com.blogspot.zone4apk.bookstash;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    // [END declare_auth]
    EditText emailField;
    EditText passField;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_bar);
        emailField = findViewById(R.id.user_email);
        passField = findViewById(R.id.user_password);


        findViewById(R.id.sign_up_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onclick listener for signup button
                createAccount(emailField.getText().toString(), passField.getText().toString());
            }
        });

    }

    private void showProgressDialog() {
        //shows circular progressbar
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.sign_up_btn).setVisibility(View.GONE);
    }

    private void hideProgressDialog() {
        //hides circular progressbar
        progressBar.setVisibility(View.GONE);
        findViewById(R.id.sign_up_btn).setVisibility(View.VISIBLE);

    }

    private boolean validateForm() {
        //This function validates the form credentials
        if (TextUtils.isEmpty(emailField.getText().toString())) {
            Toast.makeText(this, "Email required.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(passField.getText().toString())) {
            Toast.makeText(this, "Password required.", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;

    }


    private void createAccount(String email, String password) {
        //deals with the create user account action performed by user
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void updateUI(FirebaseUser user) {
        //updates the changes occuring due to active user authentication
        hideProgressDialog();
        if (user != null) {
            Toast.makeText(this, "Signed in as :" + user.getEmail(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            this.finish();
        } else {
            Toast.makeText(this, "Null user", Toast.LENGTH_SHORT).show();
        }
    }

}
