package com.blogspot.zone4apk.gwaladairy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blogspot.zone4apk.gwaladairy.recyclerViewAddress.Address;

import java.util.Objects;

public class ConfirmOrderActivity extends AppCompatActivity {

    TextView name;
    TextView address1;
    TextView address2;
    TextView address3;
    TextView mobile;

    TextView itemCount;
    TextView totalPrice;
    TextView deliveryCharge;
    TextView amountPayable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        Intent orderIntent = getIntent();
        Address address = (Address) Objects.requireNonNull(orderIntent.getExtras()).getSerializable("addressdataorder");
        Bundle bundle = orderIntent.getExtras();
        Log.d("TAG", String.valueOf(address.getName()));

        //Initializing views
        name = (TextView) findViewById(R.id.textView_name_address);
        address1 = (TextView) findViewById(R.id.textView_line1_address);
        address2 = (TextView) findViewById(R.id.textView_line2_address);
        address3 = (TextView) findViewById(R.id.textView_line3_address);
        mobile = (TextView) findViewById(R.id.textView_mobile_number_address);
        setAddress(address);

        //Initializing views
        itemCount = findViewById(R.id.txtViewItemsCount);
        totalPrice = findViewById(R.id.txt_totalPrice_all_item);
        deliveryCharge = findViewById(R.id.txt_delivery_charges);
        amountPayable = findViewById(R.id.txt_amountPayable);
        setDetails(bundle);
    }

    private void setAddress(Address address) {
        name.setText(address.getName());
        address1.setText(address.getAddressLine1());
        address2.setText(address.getAddressLine2());
        address3.setText(address.getCity()
                .concat(", ")
                .concat(address.getState())
                .concat(" - ")
                .concat(address.getPincode()));
        mobile.setText(address.getMobile());
    }

    private void setDetails(Bundle bundle) {
        switch (bundle.getInt("itemcountorder")) {
            case 1:
                //showing view when there is any item in cart.
                itemCount.setText("Price (1 item)");
                break;
            default:
                //showing view when there is any item in cart.
                itemCount.setText("Price (" + bundle.getInt("itemcountorder") + " items)");
                break;
        }

        deliveryCharge.setText("\u20B9 " + String.valueOf(bundle.getLong("deliverychargeorder")));
        totalPrice.setText("\u20B9 " + String.valueOf(bundle.getLong("totalpriceorder")));
        amountPayable.setText("\u20B9 " + String.valueOf(bundle.getLong("deliverychargeorder") + bundle.getLong("totalpriceorder")));

    }

    public void mPlaceOrder(View view) {
        //Enter code to place order
        //Put all data in cart activity to orders database
        //Remove all data in cart database
    }
}
