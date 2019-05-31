package com.blogspot.zone4apk.bookstash;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.zone4apk.bookstash.recyclerViewAddress.Address;
import com.blogspot.zone4apk.bookstash.recyclerViewCart.CartItem;
import com.blogspot.zone4apk.bookstash.recyclerViewCart.CartItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity implements ConnectivityReciever.ConnectivityRecieverListener {

    //Using RecylerView to show the shopping items
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;

    //FirebaseDatabase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;


    //Price Details TextViews
    TextView finalPrice;
    TextView amountPayable;
    TextView deliveryCharge;
    TextView priceAllItems;
    TextView itemsCount;

    //FrameLayoutCart
    FrameLayout frameLayoutContent;
    FrameLayout frameLayoutNoContent;

    //updated price from ProductDetails
    String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mAuth = FirebaseAuth.getInstance();

        //Reciever content
        filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        reciever = new ConnectivityReciever();

        //Price TextViews initialization
        finalPrice = findViewById(R.id.txt_finalPrice);
        amountPayable = findViewById(R.id.txt_amountPayable);
        deliveryCharge = findViewById(R.id.txt_delivery_charges);
        priceAllItems = findViewById(R.id.txt_totalPrice_all_item);
        itemsCount = findViewById(R.id.txtViewItemsCount);

        //FrameLayout initialization
        frameLayoutNoContent = findViewById(R.id.frmamelayout_nocontent_cart);
        frameLayoutContent = findViewById(R.id.framelayout_content_cart);

        //hiding views when there is no item in cart.
        frameLayoutNoContent.setVisibility(View.VISIBLE);
        frameLayoutContent.setVisibility(View.GONE);

        //Setting recycler view-----------------------------------------------------------
        recyclerView = findViewById(R.id.recyclerview_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                return new CartItemViewHolder(view, getApplicationContext());
            }

            @Override
            protected void onBindViewHolder(@NonNull final CartItemViewHolder holder, int position, @NonNull final CartItem model) {

                //Connectivity to main product database-----------
                FirebaseDatabase.getInstance().getReference()
                        .child("ProductDetailsDatabase")
                        .child(model.getItemId())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                price = String.valueOf(dataSnapshot.child("price").getValue());
                                databaseReference
                                        .child(model.getItemId())
                                        .child("price")
                                        .setValue(Long.valueOf(price));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(CartActivity.this,
                                        "Something went wrong!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                holder.setText_name(model.getName());
                holder.setText_description(model.getDescription());
                holder.setText_price("\u20B9 " + String.valueOf(model.getPrice()));
                holder.setImage(model.getImage_url(), getApplicationContext());
                holder.setText_quantity(model.getQuantity());
                holder.setItemId(model.getItemId());
                holder.setPrice(model.getPrice());
                holder.setProductCount(model.getProduct_count());
            }
        };

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        calculateTotal();
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
                Toast.makeText(this, "Selected " + (address != null ? address.getName() : " ") + "'s address for this delivery", Toast.LENGTH_SHORT).show();

                //Now we need to send address data of placed order to ConfirmOrderActivity
                Intent moveToPaymentIntent = new Intent(getApplicationContext(), ConfirmOrderActivity.class);
                moveToPaymentIntent.putExtra("addressdataorder", data.getExtras().getSerializable("ADDRESS_OBJECT"));
                Bundle bundle = new Bundle();
                bundle.putInt("itemcountorder", totalItemsCount);
                bundle.putLong("totalpriceorder", totalPrice);
                bundle.putLong("deliverychargeorder", delivery);
                moveToPaymentIntent.putExtras(bundle);
                startActivity(moveToPaymentIntent);
                finish();

            } else {
                Toast.makeText(this, "Failure selecting delivery address. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    int totalItemsCount;
    long totalPrice;
    long delivery;

    protected void calculateTotal() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("CartDatabase").child(mAuth.getCurrentUser().getUid());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> productlist = dataSnapshot.getChildren();
                totalPrice = 0L;
                totalItemsCount = 0;//for views
                delivery = 15L;
                for (DataSnapshot product : productlist) {
                    long productPrice = Long.parseLong(String.valueOf(product.child("price").getValue()));
                    long productCount = Long.parseLong(String.valueOf(product.child("product_count").getValue()));
                    long effectivePrice = productPrice * productCount;

                    totalPrice += effectivePrice;//calculating total price
                    totalItemsCount += 1;//calculating total item count
                }
                finalPrice.setText("\u20B9 " + (totalPrice + delivery));
                deliveryCharge.setText("\u20B9 " + delivery);
                amountPayable.setText("\u20B9 " + (totalPrice + delivery));
                priceAllItems.setText("\u20B9 " + totalPrice);

                switch (totalItemsCount) {
                    case 0:
                        //hiding views when there is no item in cart.
                        frameLayoutNoContent.setVisibility(View.VISIBLE);
                        frameLayoutContent.setVisibility(View.GONE);
                        break;
                    case 1:
                        //showing view when there is any item in cart.
                        frameLayoutNoContent.setVisibility(View.GONE);
                        frameLayoutContent.setVisibility(View.VISIBLE);
                        itemsCount.setText("Price (1 item)");
                        break;
                    default:
                        //showing view when there is any item in cart.
                        frameLayoutNoContent.setVisibility(View.GONE);
                        frameLayoutContent.setVisibility(View.VISIBLE);
                        itemsCount.setText("Price (" + totalItemsCount + " items)");
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finish();
                Toast.makeText(CartActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //------------------------------Managing internet connection status
    //BroadcastReciever
    private ConnectivityReciever reciever;
    IntentFilter filter;

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        ConnectivityReciever connectivityReciever = new ConnectivityReciever();
        connectivityReciever.showSnackbar(isConnected, findViewById(R.id.cart_activity));
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
