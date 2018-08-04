package com.blogspot.zone4apk.gwaladairy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // To check the authenticity of this appplication the default username is "abc@gmail.com" and
    // default password is "123456"
    private static final String TAG = "LoginActivity";
    EditText emailField;
    EditText passField;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.user_email);
        passField = findViewById(R.id.user_password);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");


        findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onclick listener for signin buttton
                signIn(emailField.getText().toString(), passField.getText().toString());
            }
        });
        findViewById(R.id.sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onclick listener for signup hyperlink
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                finish();

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
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

    private void signIn(String email, String password) {
        //Function deals with the signin action initiated by signin button
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        hideProgressDialog();
                    }
                });
        // [END sign_in_with_email]
    }

    private void showProgressDialog() {
        //shows circular progressbar
        progressDialog.show();
        findViewById(R.id.sign_in).setVisibility(View.GONE);
    }

    private void updateUI(FirebaseUser user) {
        //deals with the changes occuring due to valid user
        hideProgressDialog();
        if (user != null) {
            if (mAuth.getCurrentUser().isEmailVerified())
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            else
                startActivity(new Intent(getApplicationContext(), VerifyAccountActivity.class));
            this.finish();
        }
    }

    private void hideProgressDialog() {
        //hides circular progressbar
        progressDialog.dismiss();
        findViewById(R.id.sign_in).setVisibility(View.VISIBLE);

    }


    public void mForgotPassword(View view) {
        startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
    }
}
