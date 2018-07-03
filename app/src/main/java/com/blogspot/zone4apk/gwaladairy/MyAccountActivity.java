package com.blogspot.zone4apk.gwaladairy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MyAccountActivity extends AppCompatActivity {

    ImageView imageView;
    FirebaseAuth mAuth;
    TextView userEmail;
    TextView userName;
    Button btnSignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        imageView = findViewById(R.id.imageView_user);
        userEmail = findViewById(R.id.textView_user_email);
        userName=findViewById(R.id.textView_user_name);
        btnSignout=findViewById(R.id.btn_sign_out);
        Picasso.with(this)
                .load(R.drawable.gwala)
                .transform(new CropCircleTransformation())
                .into(imageView);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUi(currentUser);

    }

    private void updateUi(FirebaseUser user) {
        if (user != null) {
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
            btnSignout.setVisibility(View.VISIBLE);
        } else {
            userName.setText(". . .");
            userEmail.setText(". . . . .");
            btnSignout.setVisibility(View.GONE);
        }
    }

    public void mViewAllOrder(View view) {
        // onclick listener of view all order link
        startActivity(new Intent(getApplicationContext(),MyOrdersActivity.class));
        finish();
    }

    public void msignout(View view) {
        //signing out on request
        mAuth.signOut();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUi(currentUser);
        Toast.makeText(this, "Signed out successfully.", Toast.LENGTH_SHORT).show();
    }
}
