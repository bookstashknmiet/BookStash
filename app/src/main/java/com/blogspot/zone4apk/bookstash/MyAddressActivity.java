package com.blogspot.zone4apk.bookstash;

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
import com.blogspot.zone4apk.bookstash.recyclerViewAddress.AddressViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyAddressActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter adapter;
    FirebaseAuth mAuth;
    boolean addressSelectRequired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        addressSelectRequired = getIntent().getBooleanExtra("addressSelectRequired", false);
        if (addressSelectRequired)
            Toast.makeText(this, "Select the delivery address to complete your order", Toast.LENGTH_SHORT).show();
        mAuth = FirebaseAuth.getInstance();
        RecyclerView recyclerView = findViewById(R.id.recyclerview_my_address);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("AddressData");
        reference.keepSynced(true);

        Query query = reference.child(mAuth.getCurrentUser().getUid()).limitToLast(50);
        FirebaseRecyclerOptions<Address> options = new FirebaseRecyclerOptions.Builder<Address>().setQuery(query, Address.class).build();
        adapter = new FirebaseRecyclerAdapter<Address, AddressViewHolder>(options) {

            @NonNull
            @Override
            public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
                return new AddressViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AddressViewHolder holder, int position, @NonNull final Address model) {
                holder.setName(model.getName());
                holder.setAddressLine(
                        model.getAddressLine1(),
                        model.getAddressLine2(),
                        model.getCity().concat(", ").concat(model.getState()).concat(" - ").concat(model.getPincode())
                );

                holder.setMobileNumber(model.getMobile());
                holder.setItemId(model.getItemId());
                if (addressSelectRequired)
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("ADDRESS_OBJECT", model);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    });
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

    public void mAddAddress(View view) {
        startActivity(new Intent(getApplicationContext(), EditAddressActivity.class));
    }
}
