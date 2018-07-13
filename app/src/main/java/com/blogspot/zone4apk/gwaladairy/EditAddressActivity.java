package com.blogspot.zone4apk.gwaladairy;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditAddressActivity extends AppCompatActivity {


    private static final String TAG = "EditAddressActivity";
    EditText name;
    EditText mobileNumber;
    EditText addrLine1;
    EditText addrLine2;
    EditText addrCity;
    EditText addrState;
    EditText addrPincode;
    EditText addrLandmark;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        name = findViewById(R.id.edit_name_address);
        mobileNumber = findViewById(R.id.edit_mobile_number_address);
        addrLine1 = findViewById(R.id.edit_line1_address);
        addrLine2 = findViewById(R.id.edit_line2_address);
        addrCity = findViewById(R.id.edit_city_address);
        addrState = findViewById(R.id.edit_state_address);
        addrPincode = findViewById(R.id.edit_pin_address);
        addrLandmark = findViewById(R.id.edit_landmark_address);
        progressDialog = new ProgressDialog(this);

        //initailizing auth
        mAuth = FirebaseAuth.getInstance();

    }

    private boolean validateForm() {
        //This function validates the form credentials
        if (TextUtils.isEmpty(name.getText().toString())) {
            Toast.makeText(this, "Name required.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(addrLine1.getText().toString())) {
            Toast.makeText(this, "Flat no required.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(addrLine2.getText().toString())) {
            Toast.makeText(this, "Street name required.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(addrCity.getText().toString())) {
            Toast.makeText(this, "City required.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(addrState.getText().toString())) {
            Toast.makeText(this, "State required.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(addrPincode.getText().toString())) {
            Toast.makeText(this, "Pincode required.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!TextUtils.isDigitsOnly(mobileNumber.getText().toString())) {
            Toast.makeText(this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
            return false;
        } else if ((Long.valueOf(mobileNumber.getText().toString()) <= 5999999999l) || (Long.valueOf(mobileNumber.getText().toString()) > 9999999999l)) {
            Toast.makeText(this, "Enter a ten digit mobile number", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;


    }

    public void mSaveAddress(View view) {
        //User details Adding to DataBase
        //Checking if the details are valid or not
        if (!validateForm())
            return;

        progressDialog.show();
        //Getting Database refrence
        DatabaseReference mAddressDataBase = FirebaseDatabase.getInstance().getReference().child("AddressData").child(mAuth.getCurrentUser().getUid()).push();

        String pushId = mAddressDataBase.getKey();
        //creating Hashmap
        Map addressData = new HashMap<String, String>();
        addressData.put("name", name.getText().toString().trim());
        addressData.put("mobile", mobileNumber.getText().toString().trim());
        addressData.put("addressLine1", addrLine1.getText().toString().trim());
        addressData.put("addressLine2", addrLine2.getText().toString().trim());
        addressData.put("city", addrCity.getText().toString().trim());
        addressData.put("state", addrState.getText().toString().trim());
        addressData.put("pincode", addrPincode.getText().toString());
        addressData.put("landmark", addrLandmark.getText().toString().trim());
        addressData.put("itemId", pushId);

        mAddressDataBase.updateChildren(addressData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "updateUserData:success");
                    updateUI(true);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "updateUserData:failure", databaseError.toException());
                    Toast.makeText(getApplicationContext(), "Data update failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(false);
                }

                // [START_EXCLUDE]
                progressDialog.dismiss();
                // [END_EXCLUDE]
            }


        });

    }

    private void updateUI(boolean success) {
        if (success) {
            finish();
        } else {
            Toast.makeText(this, "Something went wrong. Pls try again later.", Toast.LENGTH_SHORT).show();
        }
    }
}
