package com.blogspot.zone4apk.gwaladairy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.zone4apk.gwaladairy.recyclerViewAddress.Address;
import com.blogspot.zone4apk.gwaladairy.recyclerViewCart.CartItem;
import com.blogspot.zone4apk.gwaladairy.recyclerViewCart.CartItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CartActivity extends AppCompatActivity {

    //Using RecylerView to show the shopping items
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;

    //FirebaseDatabase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseAuth mAuth;

    Long totalProductPrice=0L;

    int itemCount=0;

    private TextView totalPrice, amountPayable,finalPrice,itemsCount;

    CardView continuePannel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mAuth = FirebaseAuth.getInstance();

        //Price Setting-------------------------------------------------------

        totalPrice = findViewById(R.id.totalPrice);
        amountPayable = findViewById(R.id.amountPayable);
        finalPrice = findViewById(R.id.finalPrice);
        itemsCount= findViewById(R.id.txtViewItemsCount);

        //Conitune----------------
        continuePannel = findViewById(R.id.continuePannel);
        continuePannel.setVisibility(View.GONE);

        //Setting recycler view-----------------------------------------------------------
        recyclerView = findViewById(R.id.recyclerview_cart);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("CartDatabase").child(mAuth.getCurrentUser().getUid());
        databaseReference.keepSynced(true);
        Query query = databaseReference.limitToLast(50);
        FirebaseRecyclerOptions<CartItem> options = new FirebaseRecyclerOptions.Builder<CartItem>().setQuery(query, CartItem.class).build();

        adapter = new FirebaseRecyclerAdapter<CartItem, CartItemViewHolder>(options) {
            @NonNull
            @Override
            public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
                return new CartItemViewHolder(view,getApplicationContext());
            }

            @Override
            protected void onBindViewHolder(@NonNull final CartItemViewHolder holder, int position, @NonNull final CartItem model) {


                itemCount++;

                holder.setText_name(model.getName());
                holder.setText_description(model.getDescription());
                holder.setText_price("\u20B9 " + String.valueOf(model.getPrice()));
                holder.setImage(model.getImage_url(), getApplicationContext());
                holder.setText_quantity(model.getQuantity());

                totalProductPrice = totalProductPrice + model.getPrice();

                totalPrice.setText(""+totalProductPrice);
                amountPayable.setText(""+totalProductPrice);
                finalPrice.setText(""+totalProductPrice);
                itemsCount.setText("Price ("+itemCount+" items)");

                holder.setItemId(model.getItemId());
                holder.setPrice(model.getPrice());

                //removing work------------------
                Log.i("Items",""+itemCount);
                holder.setViews(itemsCount, continuePannel);
                CartItemViewHolder.qty = itemCount;

                if(CartItemViewHolder.qty==0 ){
                    continuePannel.setVisibility(View.GONE);
                }else{
                    continuePannel.setVisibility(View.VISIBLE);
                }




                //----------Recycler Click Event------------
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //On Click Events Goes here
                        Toast.makeText(CartActivity.this, "Item is clicked", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
//        if(adapter!=null){
//            continuePannel.setVisibility(View.VISIBLE);
//        }else{
//            continuePannel.setVisibility(View.INVISIBLE);
//        }




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

    public void mCartCheckout(View view) {
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
                totalProductPrice=0L;
                Toast.makeText(this, "Selected " + (address != null ? address.getName() : " ") + "'s address for this delivery", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Failure selecting delivery address. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
