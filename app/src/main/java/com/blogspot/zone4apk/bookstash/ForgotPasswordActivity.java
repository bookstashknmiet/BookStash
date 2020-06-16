package com.blogspot.zone4apk.bookstash;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextInputEditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email = findViewById(R.id.editText_email);
        mAuth = FirebaseAuth.getInstance();
    }

    public void sendPasswordResetLink(View view) {
        String emailid = email.getText().toString();
        if (TextUtils.isEmpty(emailid)) {
            email.setError("Email required");
        } else {
            findViewById(R.id.password_reset_btn).setEnabled(false);
            mAuth.sendPasswordResetEmail(emailid).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                    } else {
                        findViewById(R.id.password_reset_btn).setEnabled(true);
                        Toast.makeText(ForgotPasswordActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
