package com.blogspot.zone4apk.gwaladairy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class MyOrdersActivity extends AppCompatActivity {

    //Using RecylerView to show the shopping items
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;

    //FirebaseDatabase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;


    ArrayAdapter<String> adapterOrder ;

    ArrayList<String> orderno = new ArrayList<String>();
    ListView listOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        mAuth = FirebaseAuth.getInstance();

//        //Setting recycler view-----------------------------------------------------------
//        recyclerView = findViewById(R.id.recyclerview_myOrders);entUser().getUid());
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference().child("OrderDatabase").child(mAuth.getCurr
//        databaseReference.keepSynced(true);
//        Query query = databaseReference.limitToLast(50);
//        FirebaseRecyclerOptions<MyOrderItem> options = new FirebaseRecyclerOptions.Builder<MyOrderItem>().setQuery(query, MyOrderItem.class).build();
//
//        adapter = new FirebaseRecyclerAdapter<MyOrderItem, MyOrderItemViewHolder>(options) {
//            @NonNull
//            @Override
//            public MyOrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_order, parent, false);
//                return new MyOrderItemViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull final MyOrderItemViewHolder holder, int position, @NonNull final MyOrderItem model) {
//
//                Toast.makeText(MyOrdersActivity.this, "itemFound", Toast.LENGTH_SHORT).show();
//
//            }
//        };
//
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(adapter);
//
//
//
//
//    }
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();

        //Test-----------
        listOrders = findViewById(R.id.listViewTest);

        adapterOrder = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,orderno);

        listOrders.setAdapter(adapterOrder);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderDatabase").child(mAuth.getCurrentUser().getUid());
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.i("Title",""+dataSnapshot.getKey().toString());
                int productCount = (int) dataSnapshot.child("productsOrdered").getChildrenCount();

                adapterOrder.add("You have ordered "+ productCount+" items at date "+dataSnapshot.getKey().toString());

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
