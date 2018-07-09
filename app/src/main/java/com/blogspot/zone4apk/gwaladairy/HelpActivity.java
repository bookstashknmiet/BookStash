package com.blogspot.zone4apk.gwaladairy;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }

    public void mHelplineSupport(View view) {
        Snackbar.make(findViewById(R.id.layout_help_activity), "Do you want to call helpline number?", Snackbar.LENGTH_SHORT)
                .setAction("Call", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri u = Uri.parse("tel:" + getString(R.string.helpline_number));
                        startActivity(new Intent(Intent.ACTION_DIAL, u).setData(u));
                    }
                })
                .show();
    }

    public void mOfficeSupport(View view) {
        Snackbar.make(findViewById(R.id.layout_help_activity), "Do you want to navigate to Gwala Dairy?", Snackbar.LENGTH_SHORT)
                .setAction("Navigate", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri u = Uri.parse("http://maps.google.com/maps?daddr=28.839012,77.576087");
                        startActivity(new Intent(Intent.ACTION_VIEW, u).setData(u));
                    }
                })
                .show();
    }

    public void mChatSupport(View view) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            startActivity(new Intent(this, ChatSupportActivity.class));
        else
            Toast.makeText(this, "Please login for chat support.", Toast.LENGTH_SHORT).show();
    }
}
