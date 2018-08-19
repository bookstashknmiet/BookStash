package com.blogspot.zone4apk.gwaladairy.recyclerViewSubscription;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.zone4apk.gwaladairy.R;
import com.blogspot.zone4apk.gwaladairy.recyclerViewAddress.Address;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;

public class SubscriptionItemViewHolder extends RecyclerView.ViewHolder {

    TextView text_name;
    TextView text_description;
    TextView text_price;
    TextView text_quantity;
    ImageView image;
    String itemId;
    TextView text_noOfDays;
    ImageView imageSubscribed;

    //Buttons
    public TextView subscribeBtn, cancelBtn;

    //Detail Layouts
    LinearLayout addressDetailsLayout;
    LinearLayout subscriptionDetailLayout;

    //Address data fields
    public TextView name;
    public TextView address1;
    public TextView address2;
    public TextView address3;
    public TextView mobile;

    //Subscription dates-----------------
    TextView startDate;
    TextView endDate;
    TextView balance;


    //Database For Subscription----------------
    DatabaseReference subsUserDatabase;
    FirebaseAuth mAuth;

    Context context;


    public SubscriptionItemViewHolder(final View itemView, Context context) {
        super(itemView);
        this.context = context;

        //Product details
        text_name = (TextView) itemView.findViewById(R.id.product_name);
        text_description = (TextView) itemView.findViewById(R.id.product_description);
        text_price = (TextView) itemView.findViewById(R.id.product_price);
        text_quantity = (TextView) itemView.findViewById(R.id.product_quantity);
        image = (ImageView) itemView.findViewById(R.id.product_image);
        text_noOfDays = (TextView) itemView.findViewById(R.id.product_no_of_days);

        //action buttons
        subscribeBtn = itemView.findViewById(R.id.button_subscribe);
        cancelBtn = itemView.findViewById(R.id.button_cancel);
        imageSubscribed = itemView.findViewById(R.id.image_subscribed);

        //Grouped Layouts
        addressDetailsLayout = itemView.findViewById(R.id.subs_more);
        subscriptionDetailLayout = itemView.findViewById(R.id.dates);

        //Subscription details
        startDate = itemView.findViewById(R.id.textView_start_dt);
        endDate = itemView.findViewById(R.id.textView_end_dt);
        balance = itemView.findViewById(R.id.textView_balnce);

        //Address objects
        name = itemView.findViewById(R.id.textView_name_address);
        address1 = itemView.findViewById(R.id.textView_line1_address);
        address2 = itemView.findViewById(R.id.textView_line2_address);
        address3 = itemView.findViewById(R.id.textView_line3_address);
        mobile = itemView.findViewById(R.id.textView_mobile_number_address);

        //Setting visibility
        cancelBtn.setVisibility(View.GONE);
        addressDetailsLayout.setVisibility(View.GONE);
        subscriptionDetailLayout.setVisibility(View.GONE);
        imageSubscribed.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();

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
    }

    private void showAddress() {
        //Using setAddress to show the delivery address after obtaining it from subscription database
        FirebaseDatabase.getInstance().getReference()
                .child("SubscriptionDatabase")
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(itemId)
                .child("deliveryAddress")
                .addValueEventListener(new ValueEventListener() {
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

    public void setText_noOfDays(String noOfDays) {
        text_noOfDays.setText("Subscription valid for " + noOfDays + "days");
    }

    public void calculate() {
        subsUserDatabase = FirebaseDatabase.getInstance().getReference()
                .child("SubscriptionDatabase")
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        subsUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = (String) dataSnapshot.child(itemId).child("status").getValue();
                if (status != null) {
                    //when user database exists on server
                    switch (status) {
                        case "Applied":
                            imageSubscribed.setVisibility(View.GONE);
                            cancelBtn.setVisibility(View.VISIBLE);
                            subscribeBtn.setEnabled(false);
                            subscribeBtn.setText("Subscription Requested");
                            addressDetailsLayout.setVisibility(View.VISIBLE);
                            subscriptionDetailLayout.setVisibility(View.GONE);
                            showAddress();
                            break;

                        case "Subscribed":
                            imageSubscribed.setVisibility(View.VISIBLE);
                            cancelBtn.setVisibility(View.GONE);
                            subscribeBtn.setEnabled(false);
                            subscribeBtn.setText("Subscribed");
                            addressDetailsLayout.setVisibility(View.VISIBLE);
                            subscriptionDetailLayout.setVisibility(View.VISIBLE);
                            showAddress();

                            //Balance fecthing----------------------
                            long subscriptionDuration = Long.parseLong(String.valueOf(dataSnapshot.child(itemId).child("no_of_days").getValue()));
                            long balance = Long.parseLong(String.valueOf(dataSnapshot.child(itemId).child("balance").getValue()));

                            //Date stuffs-----------------
                            //In string formate
                            String startDateSubscription = String.valueOf(dataSnapshot.child(itemId).child("subscriptionStart").getValue());
                            String endDateSubscription;

                            //In date format
                            Date staratDate = null;
                            Date endDate = null;

                            try {
                                staratDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDateSubscription);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            //finding end subscription date
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(staratDate);
                            cal.add(Calendar.DAY_OF_MONTH, ((int) subscriptionDuration + (int) balance));
                            endDate = (Date) cal.getTime();
                            endDateSubscription = new SimpleDateFormat("dd/MM/yyyy").format(endDate);

                            //setting values
                            startDate.setText("Subscription Start Date :\t" + startDateSubscription);
                            SubscriptionItemViewHolder.this.endDate.setText("Subscription End date :\t" + endDateSubscription);
                            SubscriptionItemViewHolder.this.balance.setText("Balance : \t" + balance);
                            break;

                        case "Cancelled":
                            imageSubscribed.setVisibility(View.GONE);
                            cancelBtn.setVisibility(View.GONE);
                            subscribeBtn.setEnabled(true);
                            subscribeBtn.setText("Subscribe");
                            addressDetailsLayout.setVisibility(View.GONE);
                            subscriptionDetailLayout.setVisibility(View.GONE);
                            break;

                        default:
                            imageSubscribed.setVisibility(View.GONE);
                            cancelBtn.setVisibility(View.GONE);
                            subscribeBtn.setEnabled(true);
                            subscribeBtn.setText("Subscribe");
                            addressDetailsLayout.setVisibility(View.GONE);
                            subscriptionDetailLayout.setVisibility(View.GONE);
                            break;
                    }
                } else {
                    //When user database is deleted on server
                    imageSubscribed.setVisibility(View.GONE);
                    cancelBtn.setVisibility(View.GONE);
                    subscribeBtn.setEnabled(true);
                    subscribeBtn.setText("Subscribe");
                    addressDetailsLayout.setVisibility(View.GONE);
                    subscriptionDetailLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
