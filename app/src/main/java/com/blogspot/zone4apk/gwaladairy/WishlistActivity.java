package com.blogspot.zone4apk.gwaladairy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.zone4apk.gwaladairy.recyclerViewWishlist.WishlistItem;
import com.blogspot.zone4apk.gwaladairy.recyclerViewWishlist.WishlistItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class WishlistActivity extends AppCompatActivity {

    //Using RecylerView to show the shopping items
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;

    //FirebaseDatabase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        mAuth = FirebaseAuth.getInstance();

        //Setting recycler view-----------------------------------------------------------
        recyclerView = findViewById(R.id.recyclerview_wishlist);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("WishlistDatabase").child(mAuth.getCurrentUser().getUid());
        databaseReference.keepSynced(true);
        Query query = databaseReference.limitToLast(50);
        FirebaseRecyclerOptions<WishlistItem> options = new FirebaseRecyclerOptions.Builder<WishlistItem>().setQuery(query, WishlistItem.class).build();

        adapter = new FirebaseRecyclerAdapter<WishlistItem, WishlistItemViewHolder>(options) {
            @NonNull
            @Override
            public WishlistItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist, parent, false);
                return new WishlistItemViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final WishlistItemViewHolder holder, int position, @NonNull final WishlistItem model) {
                holder.setText_name(model.getName());
                holder.setText_description(model.getDescription());
                holder.setText_price("\u20B9 " + String.valueOf(model.getPrice()));
                holder.setImage(model.getImage_url(), getApplicationContext());
                holder.setText_quantity(model.getQuantity());
                holder.setItemId(model.getItemId());
                holder.setPrice(model.getPrice());
            }
        };
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
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
