package com.blogspot.zone4apk.bookstash;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blogspot.zone4apk.bookstash.recyclerViewAddress.Address;
import com.blogspot.zone4apk.bookstash.recyclerViewSubscription.SubscriptionItem;
import com.blogspot.zone4apk.bookstash.recyclerViewSubscription.SubscriptionItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

public class ProductSubscriptionActivity extends AppCompatActivity {

    //Using RecylerView to show the shopping items
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;

    //Database For Subscription----------------
    DatabaseReference subsUserDatabase;

    //FirebaseDatabase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    public Long no_of_days = 28L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_subscription);
        mAuth = FirebaseAuth.getInstance();

        //Setting recycler view-----------------------------------------------------------
        recyclerView = findViewById(R.id.recyclerview_productSubscription);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference()
                .child("SubscriptionDatabase")
                .child("ProductDetailsDatabase");
        databaseReference.keepSynced(true);
        Query query = databaseReference.limitToLast(50);
        FirebaseRecyclerOptions<SubscriptionItem> options
                = new FirebaseRecyclerOptions.Builder<SubscriptionItem>()
                .setQuery(query, SubscriptionItem.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<SubscriptionItem, SubscriptionItemViewHolder>(options) {
            @NonNull
            @Override
            public SubscriptionItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription, parent, false);
                return new SubscriptionItemViewHolder(view, getApplicationContext());
            }

            @Override
            protected void onBindViewHolder(@NonNull final SubscriptionItemViewHolder holder, int position, @NonNull final SubscriptionItem model) {
                holder.setText_name(model.getName());
                holder.setText_description(model.getDescription());
                holder.setText_price("\u20B9 " + String.valueOf(model.getPrice()));
                holder.setImage(model.getImageUrl(), getApplicationContext());
                holder.setText_quantity(model.getQuantity());
                holder.setItemId(model.getItemId());
                holder.setText_noOfDays(String.valueOf(model.getNo_of_days()));


                //handling on click
                holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        subsUserDatabase = FirebaseDatabase.getInstance().getReference()
                                .child("SubscriptionDatabase")
                                .child("Users")
                                .child(mAuth.getCurrentUser().getUid())
                                .child(model.getItemId());

                        CharSequence options[] = new CharSequence[]{"Cancel subscription", "Dismiss"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProductSubscriptionActivity.this);
                        builder.setTitle("Are you sure?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //click event handler
                                if (which == 0) {
                                    subsUserDatabase.child("dateOfSubscribe").setValue(ServerValue.TIMESTAMP);
                                    subsUserDatabase.child("status").setValue("Cancelled");
                                    dialog.dismiss();
                                }
                                if (which == 1) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.show();

                    }
                });

                holder.subscribeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Setting no of days in local variable
                        ProductSubscriptionActivity.this.no_of_days = model.getNo_of_days();
                        subsUserDatabase = FirebaseDatabase.getInstance().getReference()
                                .child("SubscriptionDatabase")
                                .child("Users")
                                .child(mAuth.getCurrentUser().getUid())
                                .child(model.getItemId());
                        mSubscriptionCheckout();
                    }
                });

                holder.calculate();
            }
        };
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public void mSubscriptionCheckout() {
        //Obtaining address for delivery of product
        Intent intent = new Intent(getApplicationContext(), MyAddressActivity.class);
        intent.putExtra("addressSelectRequired", true);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Address address = (Address) data.getExtras().getSerializable("ADDRESS_OBJECT");
                Toast.makeText(this, "Selected " + (address != null ? address.getName() : " ") + "'s address for this delivery", Toast.LENGTH_SHORT).show();
                subsUserDatabase.child("deliveryAddress").setValue(address);

                //adding other details to database
                subsUserDatabase.child("subscriptionApplied").setValue(ServerValue.TIMESTAMP);
                subsUserDatabase.child("status").setValue("Applied");
                subsUserDatabase.child("subscriptionStart").setValue("31/12/1999");
                subsUserDatabase.child("balance").setValue(0);
                subsUserDatabase.child("no_of_days").setValue(this.no_of_days);
            } else {
                Toast.makeText(this, "Failure selecting delivery address. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
