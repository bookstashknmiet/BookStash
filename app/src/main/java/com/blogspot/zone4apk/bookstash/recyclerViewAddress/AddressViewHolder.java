package com.blogspot.zone4apk.bookstash.recyclerViewAddress;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.zone4apk.bookstash.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddressViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView addressLine1;
    TextView addressLine2;
    TextView addressLine3;
    TextView mobileNumber;
    ImageView removeAddress;
    String itemId;
    private FirebaseAuth mAuth;

    public AddressViewHolder(View itemView) {
        super(itemView);

        mAuth = FirebaseAuth.getInstance();

        name = (TextView) itemView.findViewById(R.id.textView_name_address);
        addressLine1 = (TextView) itemView.findViewById(R.id.textView_line1_address);
        addressLine2 = (TextView) itemView.findViewById(R.id.textView_line2_address);
        addressLine3 = (TextView) itemView.findViewById(R.id.textView_line3_address);
        mobileNumber = (TextView) itemView.findViewById(R.id.textView_mobile_number_address);
        removeAddress = (ImageView) itemView.findViewById(R.id.button_remove_address);
        removeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mAddressDatabase = FirebaseDatabase.getInstance().getReference().child("AddressData").child(mAuth.getCurrentUser().getUid());
                mAddressDatabase.child(itemId).removeValue();
            }
        });
    }

    public void setName(String delivery_name) {
        name.setText(delivery_name);
    }

    public void setAddressLine(String line1, String line2, String line3) {
        addressLine1.setText(line1);
        addressLine2.setText(line2);
        addressLine3.setText(line3);
    }

    public void setMobileNumber(String delivery_mobile) {
        mobileNumber.setText(delivery_mobile);
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

}
