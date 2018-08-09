package com.blogspot.zone4apk.gwaladairy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.blogspot.zone4apk.gwaladairy.recyclerViewWishlist.WishlistItem;
import com.blogspot.zone4apk.gwaladairy.recyclerViewWishlist.WishlistItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class WishlistActivity extends AppCompatActivity {

    //Using RecylerView to show the shopping items
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;

    //FirebaseDatabase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //Content Frames
    FrameLayout frameLayoutContent;
    FrameLayout frameLayoutNoContent;
    FirebaseAuth mAuth;

    //Main Product DataBase-------------------
    DatabaseReference mainProductDataBase;
    String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        mAuth = FirebaseAuth.getInstance();


        //initializin framelayouts
        frameLayoutContent = findViewById(R.id.framelayout_content_wishlist);
        frameLayoutNoContent = findViewById(R.id.frmamelayout_nocontent_wishlist);
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


                //Contectivity to main productdatabase-------------------
                Log.i("Error Wishlist", model.getItemId());
                mainProductDataBase =FirebaseDatabase.getInstance().getReference().child("ProductDetailsDatabase").child(model.getItemId());
                mainProductDataBase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        price =String.valueOf( dataSnapshot.child("price").getValue());

                        databaseReference.child(model.getItemId()).child("price").setValue(Long.valueOf(price));



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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

        //counting total no of items and updating no content page accordingly
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long i = dataSnapshot.getChildrenCount();
                if (i == 0) {//hiding views when there is no item in cart.
                    frameLayoutNoContent.setVisibility(View.VISIBLE);
                    frameLayoutContent.setVisibility(View.GONE);

                } else {//showing view when there is any item in cart.
                    frameLayoutNoContent.setVisibility(View.GONE);
                    frameLayoutContent.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


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
