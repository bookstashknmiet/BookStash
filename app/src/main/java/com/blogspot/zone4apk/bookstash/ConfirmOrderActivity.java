package com.blogspot.zone4apk.bookstash;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.zone4apk.bookstash.recyclerViewAddress.Address;
import com.blogspot.zone4apk.bookstash.recyclerViewCart.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfirmOrderActivity extends AppCompatActivity implements ConnectivityReciever.ConnectivityRecieverListener {

    TextView name;
    TextView address1;
    TextView address2;
    TextView address3;
    TextView mobile;

    TextView itemCount;
    TextView totalPrice;
    TextView deliveryCharge;
    TextView amountPayable;

    FirebaseAuth mAuth;
    private Address address;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        mAuth = FirebaseAuth.getInstance();

        //Reciever content
        filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        reciever = new ConnectivityReciever();

        Intent orderIntent = getIntent();
        address = (Address) Objects.requireNonNull(orderIntent.getExtras()).getSerializable("addressdataorder");
        bundle = orderIntent.getExtras();
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String timeStamp = simpleDateFormat.format(new Date());

        DatabaseReference orderDatabase = FirebaseDatabase.getInstance().getReference().child("OrderDatabase")
                .child(mAuth.getCurrentUser().getUid())
                .child(timeStamp);
        orderDatabase.keepSynced(true);

        DatabaseReference orderAddress = orderDatabase.child("deliveryAddress");
        DatabaseReference orderPaymentDetail = orderDatabase.child("paymentDetail");
        final DatabaseReference orderProduct = orderDatabase.child("productsOrdered");

        //Setting Delivery Address--------------------------------------------------
        orderAddress.setValue(address);

        //Setting Payment Details---------------------------------------------------
        Map payment = new HashMap();
        payment.put("itemCount", bundle.getInt("itemcountorder"));
        payment.put("totalPrice", bundle.getLong("totalpriceorder"));
        payment.put("deliveryCharges", bundle.getLong("deliverychargeorder"));
        payment.put("amountPaid", bundle.getLong("deliverychargeorder") + bundle.getLong("totalpriceorder"));
        payment.put("status", "Order Placed");
        orderPaymentDetail.updateChildren(payment);

        //Setting Products Ordered---------------------------------------------------
        final DatabaseReference cartDatabase = FirebaseDatabase.getInstance().getReference().child("CartDatabase")
                .child(mAuth.getCurrentUser().getUid());

        cartDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> productList = dataSnapshot.getChildren();
                for (DataSnapshot product : productList) {
                    CartItem cartItem = product.getValue(CartItem.class);
                    orderProduct.child(cartItem.getItemId()).setValue(cartItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Do nothing
                Toast.makeText(ConfirmOrderActivity.this, "Please try again...", Toast.LENGTH_SHORT).show();
            }
        });
        cartDatabase.removeValue();
        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
        finish();
        Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show();
    }

    //------------------------------Managing internet connection status
    //BroadcastReciever
    private ConnectivityReciever reciever;
    IntentFilter filter;

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        ConnectivityReciever connectivityReciever = new ConnectivityReciever();
        connectivityReciever.showSnackbar(isConnected, findViewById(R.id.confirmorder_activity));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(reciever);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
        registerReceiver(reciever, filter);
    }

}
