package com.blogspot.zone4apk.bookstash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MyAccountActivity extends AppCompatActivity {

    ImageView imageView;
    FirebaseAuth mAuth;
    TextView userEmail;
    TextView userName;
    TextView userMobile;
    Button btnSignout;
    String mobno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        imageView = findViewById(R.id.imageView_user);
        userEmail = findViewById(R.id.textView_user_email);
        userName = findViewById(R.id.textView_user_name);
        userMobile = findViewById(R.id.textView_user_mobile);
        btnSignout = findViewById(R.id.btn_sign_out);
        Picasso.with(this)
                .load(R.mipmap.ic_launcher)
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
            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(mAuth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mobno = String.valueOf(dataSnapshot.child("mobno").getValue());
                            userMobile.setText(mobno);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //Do nothing
                        }
                    });
            btnSignout.setVisibility(View.VISIBLE);
        } else {
            userName.setText("");
            userEmail.setText("");
            userMobile.setText("");
            btnSignout.setVisibility(View.GONE);
        }
    }

    public void mViewAllOrder(View view) {
        // onclick listener of view all order link
        startActivity(new Intent(getApplicationContext(), MyOrdersActivity.class));
    }

    public void msignout(View view) {
        //signing out on request
        Snackbar.make(findViewById(R.id.layout_my_account), "Do you want to sign out and exit the app?", Snackbar.LENGTH_SHORT)
                .setAction("Signout", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAuth.signOut();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        updateUi(currentUser);
                        Toast.makeText(getApplicationContext(), "Signed out successfully.", Toast.LENGTH_SHORT).show();
                        finishAffinity();
                        startActivity(new Intent(getApplicationContext(), WelcomeFlashActivity.class));
                    }
                })
                .show();
    }

    public void mViewAllAddress(View view) {
        Intent intent = new Intent(getApplicationContext(), MyAddressActivity.class);
        intent.putExtra("addressSelectRequired", false);
        startActivity(intent);
    }

    public void mViewShoppingCart(View view) {
        startActivity(new Intent(getApplicationContext(), CartActivity.class));
    }

    public void mViewWishlist(View view) {
        startActivity(new Intent(getApplicationContext(), WishlistActivity.class));
    }

    public void mViewAllSubscription(View view) {
        startActivity(new Intent(getApplicationContext(), ProductSubscriptionActivity.class));
    }
}
