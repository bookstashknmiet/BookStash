package com.blogspot.zone4apk.gwaladairy.recyclerViewSubscription;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.blogspot.zone4apk.gwaladairy.GetTimeLeft;
import com.blogspot.zone4apk.gwaladairy.R;
import com.blogspot.zone4apk.gwaladairy.recyclerViewAddress.Address;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;

/**
 * Created by AMIT on 8/13/2018.
 */

public class SubscriptionItemViewHolder extends RecyclerView.ViewHolder {

    TextView text_name;
    TextView text_description;
    TextView text_price;
    TextView text_quantity;
    ImageView image;
    String itemId;
    public TextView subscribeBtn, cancelBtn;

    LinearLayout addressDetails, dateLayout;

    public TextView name;
    public TextView address1;
    public TextView address2;
    public TextView address3;
    public TextView mobile;

    //Dates-----------------
    TextView startDate, endDate, balance;


    //Database For Subscription----------------
    DatabaseReference subsUserDatabase;
    FirebaseAuth mAuth;

    Context applicationContext;


    public SubscriptionItemViewHolder(final View itemView, Context context) {
        super(itemView);
        this.applicationContext = context;
        text_name = (TextView) itemView.findViewById(R.id.product_name);
        text_description = (TextView) itemView.findViewById(R.id.product_description);
        text_price = (TextView) itemView.findViewById(R.id.product_price);
        text_quantity = (TextView) itemView.findViewById(R.id.product_quantity);
        image = (ImageView) itemView.findViewById(R.id.product_image);
        subscribeBtn = itemView.findViewById(R.id.button_subscribe);
        cancelBtn = itemView.findViewById(R.id.button_cancel);
        addressDetails = itemView.findViewById(R.id.subs_more);
        startDate = itemView.findViewById(R.id.textView_start_dt);
        endDate = itemView.findViewById(R.id.textView_end_dt);
        balance = itemView.findViewById(R.id.textView_balnce);
        dateLayout = itemView.findViewById(R.id.dates);
        //Address objects
        name = itemView.findViewById(R.id.textView_name_address);
        address1 = itemView.findViewById(R.id.textView_line1_address);
        address2 = itemView.findViewById(R.id.textView_line2_address);
        address3 = itemView.findViewById(R.id.textView_line3_address);
        mobile = itemView.findViewById(R.id.textView_mobile_number_address);

        mAuth = FirebaseAuth.getInstance();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subsUserDatabase = FirebaseDatabase.getInstance().getReference().child("SubscriptionDatabase").child("Users").
                        child(mAuth.getCurrentUser().getUid().toString()).child(itemId);
                subsUserDatabase.child("dateOfSubscribe").setValue(ServerValue.TIMESTAMP);
                subsUserDatabase.child("status").setValue("Cancelled");
            }
        });
    }

    public void setText_name(String name) {
        text_name.setText(name);
    }

    public void setText_description(String description) {
        text_description.setText(description);
    }

    public void setText_price(String price) {
        text_price.setText(price);
    }

    public void setImage(String imageUrl, Context ctx) {
        Picasso.with(ctx)
                .load(imageUrl)
                .transform(new CropSquareTransformation())
                .into(image);

    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
        subsUserDatabase = FirebaseDatabase.getInstance().getReference().child("SubscriptionDatabase").child("Users").child(mAuth.getCurrentUser().getUid().toString()).child(itemId);
        subsUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = (String) dataSnapshot.child("status").getValue();


                try {
                    if (status.equals("Applied")) {
                        cancelBtn.setVisibility(View.VISIBLE);
                        subscribeBtn.setEnabled(false);
                        subscribeBtn.setText("Subscription Applied");
                        addressDetails.setVisibility(View.VISIBLE);
                        dateLayout.setVisibility(View.GONE);
                        showingAddress();

                        //   subscribeBtn.setText("Subscribed ("+getTimeLeft.getTimeLeft(dOfOrder, Long.parseLong(ServerValue.TIMESTAMP))+"days left)");
                    } else if (status.equals("Subscribed")) {
                        cancelBtn.setVisibility(View.GONE);
                        subscribeBtn.setEnabled(false);
                        subscribeBtn.setText("Subscribed");
                        addressDetails.setVisibility(View.VISIBLE);
                        dateLayout.setVisibility(View.VISIBLE);
                        showingAddress();
                        subsUserDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {


                                    //Balance fecthing----------------------
                                    long bal = Long.parseLong(String.valueOf(dataSnapshot.child("balance").getValue()));

                                    //Date stuffs-----------------
                                    Date endDateInDate;
                                    String stDate = String.valueOf(dataSnapshot.child("subscriptionStart").getValue());
                                    try {
                                        Date staratDate = new SimpleDateFormat("dd/MM/yyyy").parse(stDate);
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(staratDate);
                                        cal.add(Calendar.DAY_OF_MONTH, (28 + (int) bal)); // add 28 days
                                        endDateInDate = (Date) cal.getTime();
                                        startDate.setText("Start Date : " + stDate);
                                        endDate.setText("End date : " + new SimpleDateFormat("dd/MM/yyyy").format(endDateInDate));
                                        balance.setText("Balance : " + bal);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }catch (Exception e){

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    } else if (status.equals("Cancelled")) {
                        cancelBtn.setVisibility(View.GONE);
                        subscribeBtn.setEnabled(true);
                        subscribeBtn.setText("Subscribe");
                        addressDetails.setVisibility(View.GONE);
                        dateLayout.setVisibility(View.GONE);
                    } else {
                        cancelBtn.setVisibility(View.GONE);
                        subscribeBtn.setEnabled(true);
                        subscribeBtn.setText("Subscribe");
                        addressDetails.setVisibility(View.GONE);
                        dateLayout.setVisibility(View.GONE);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showingAddress() {

        FirebaseDatabase.getInstance().getReference().child("SubscriptionDatabase").child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(itemId).child("deliveryAddress").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                setAddress(dataSnapshot.getValue(Address.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setText_quantity(String quantity) {
        text_quantity.setText(quantity);
    }

    public void setAddress(Address address) {
        if (address != null) {
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
    }
}
