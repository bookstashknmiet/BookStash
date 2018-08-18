package com.blogspot.zone4apk.gwaladairy;

import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blogspot.zone4apk.gwaladairy.recyclerViewAddress.Address;
import com.blogspot.zone4apk.gwaladairy.recyclerViewCart.CartItem;
import com.blogspot.zone4apk.gwaladairy.recyclerViewCart.CartItemViewHolder;
import com.blogspot.zone4apk.gwaladairy.recyclerViewSubscription.SubscriptionItem;
import com.blogspot.zone4apk.gwaladairy.recyclerViewSubscription.SubscriptionItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

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

    Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_subscription);

        mAuth = FirebaseAuth.getInstance();
        //Setting recycler view-----------------------------------------------------------
        recyclerView = findViewById(R.id.recyclerview_productSubscription);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("SubscriptionDatabase").child("ProductDetailsDatabase");
        databaseReference.keepSynced(true);
        Query query = databaseReference.limitToLast(50);
        FirebaseRecyclerOptions<SubscriptionItem> options = new FirebaseRecyclerOptions.Builder<SubscriptionItem>().setQuery(query, SubscriptionItem.class).build();

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


                //Database work from here-------------------
                subsUserDatabase = FirebaseDatabase.getInstance().getReference().child("SubscriptionDatabase").child("Users").
                        child(mAuth.getCurrentUser().getUid().toString()).child(model.getItemId());
                holder.subscribeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSubscriptionCheckout(view);

                    }
                });
            }
        };
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public void mSubscriptionCheckout(View view) {
        Intent intent = new Intent(getApplicationContext(), MyAddressActivity.class);
        intent.putExtra("addressSelectRequired", true);
        startActivityForResult(intent, 1);
        // Toast.makeText(this, "Please choose an address to complete your order.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Address address = (Address) data.getExtras().getSerializable("ADDRESS_OBJECT");
                Toast.makeText(this, "Selected " + (address != null ? address.getName() : " ") + "'s address for this delivery", Toast.LENGTH_SHORT).show();
                subsUserDatabase.child("subscriptionApplied").setValue(ServerValue.TIMESTAMP);
                subsUserDatabase.child("status").setValue("Applied");
                subsUserDatabase.child("subscriptionStart").setValue("dd/mm/yyyy");
                subsUserDatabase.child("balance").setValue(0);
                subsUserDatabase.child("deliveryAddress").setValue(address);
                //this.address=address;

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
