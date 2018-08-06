package com.blogspot.zone4apk.gwaladairy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.blogspot.zone4apk.gwaladairy.recyclerViewMyOrders.MyOrderItem;
import com.blogspot.zone4apk.gwaladairy.recyclerViewMyOrders.MyOrderItemViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyOrdersActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    ArrayList<MyOrderItem> orderno = new ArrayList<MyOrderItem>();
    ListView listOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        mAuth = FirebaseAuth.getInstance();
        listOrders = findViewById(R.id.listViewTest);

        //Using simple listview to display items in my order activity
        final MyOrderItemViewHolder myOrderItemViewHolder = new MyOrderItemViewHolder(
                this,
                R.layout.item_my_order,
                orderno);

        listOrders.setAdapter(myOrderItemViewHolder);

        //Database stuff
        databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderDatabase")
                .child(mAuth.getCurrentUser().getUid());
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                MyOrderItem details = new MyOrderItem();

                //For taking Addresss------------------
                DataSnapshot deliveryAdd = dataSnapshot.child("deliveryAddress");
                String address = deliveryAdd.child("name").getValue() +
                        "\n" +
                        deliveryAdd.child("addressLine1").getValue() +
                        ", " +
                        deliveryAdd.child("addressLine2").getValue() +
                        "\n" +
                        deliveryAdd.child("city").getValue() +
                        ", " +
                        deliveryAdd.child("state").getValue() +
                        "-" +
                        deliveryAdd.child("pincode").getValue() +
                        "\n" +
                        "Mob: " +
                        deliveryAdd.child("mobile").getValue() + "\n";

                //For taking Status-----------------------
                String orderStatus = "Status : " + dataSnapshot.child("paymentDetail").child("status").getValue();

                String dateStr = dataSnapshot.getKey();
                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyyMMdd-hhmmss").parse(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                details.setOrderstatus(orderStatus);
                details.setAddress(address);
                details.setOrderNo(dataSnapshot.getKey());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy hh:mm a");
                String timeStamp = simpleDateFormat.format(date);

                Log.i("Date", "" + timeStamp);
                details.setTime(timeStamp);

                orderno.add(details);
                myOrderItemViewHolder.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
