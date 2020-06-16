package com.blogspot.zone4apk.bookstash;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    EditText userName;
    EditText userAddress;
    EditText userMobile;
    FloatingActionButton addToDbButton;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_bar);
        addToDbButton = findViewById(R.id.add_to_db_btn);
        userName = findViewById(R.id.user_name);
        userAddress = findViewById(R.id.user_address);
        userMobile = findViewById(R.id.user_mobile);

        addToDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabase();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
    }

    private boolean validateForm() {
        //This function validates the form credentials
        if (TextUtils.isEmpty(userName.getText().toString())) {
            Toast.makeText(this, "Name required.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(userAddress.getText().toString())) {
            Toast.makeText(this, "Address required.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(userMobile.getText().toString())) {
            Toast.makeText(this, "Mobile Number required.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!TextUtils.isDigitsOnly(userMobile.getText().toString())) {
            Toast.makeText(this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
            return false;
        } else if ((Long.valueOf(userMobile.getText().toString()) <= 5999999999l) || (Long.valueOf(userMobile.getText().toString()) > 9999999999l)) {
            Toast.makeText(this, "Enter a ten digit mobile number", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;


    }

    private void showProgressDialog() {
        //shows circular progressbar
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.add_to_db_btn).setVisibility(View.GONE);
    }

    private void hideProgressDialog() {
        //hides circular progressbar
        progressBar.setVisibility(View.GONE);
        findViewById(R.id.add_to_db_btn).setVisibility(View.VISIBLE);

    }

    void updateDatabase() {
        //User details Adding to DataBase
        //Checking if the details are valid or not
        if (!validateForm())
            return;

        showProgressDialog();
        //Getting Database refrence
        DatabaseReference mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        //creating Hashmap
        HashMap<String, String> usersDetails = new HashMap<String, String>();
        usersDetails.put("name", userName.getText().toString().trim());
        usersDetails.put("address", userAddress.getText().toString().trim());
        usersDetails.put("mobno", userMobile.getText().toString());
        user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(userName.getText().toString()).build());

        mUserDataBase.setValue(usersDetails).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "updateUserData:success");
                    updateUI(true);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "updateUserData:failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Data update failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(false);
                }

                // [START_EXCLUDE]
                hideProgressDialog();
                // [END_EXCLUDE]
            }


        });

    }

    @Override
    public void onBackPressed() {
        //restricting user from using back button
    }

    private void updateUI(Boolean success) {
        if (success) {
            finish();
            startActivity(new Intent(getApplicationContext(), VerifyAccountActivity.class));
        } else
            Toast.makeText(this, "Try again later.", Toast.LENGTH_SHORT).show();
    }
}
