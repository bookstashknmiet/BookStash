package com.blogspot.zone4apk.gwaladairy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.zone4apk.gwaladairy.recyclerViewNotifications.NotificationItem;
import com.blogspot.zone4apk.gwaladairy.recyclerViewNotifications.NotificationItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class NotificationsActivity extends AppCompatActivity {

    //Using RecylerView to show the shopping items
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;

    //FirebaseDatabase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        //Setting recycler view-----------------------------------------------------------
        recyclerView = findViewById(R.id.recyclerview_notifications);
        //Reversing the display of Notification data
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("NotificationDatabase");
        databaseReference.keepSynced(true);
        Query query = databaseReference.limitToLast(10).orderByChild("date");
        FirebaseRecyclerOptions<NotificationItem> options = new FirebaseRecyclerOptions.Builder<NotificationItem>().setQuery(query, NotificationItem.class).build();

        adapter = new FirebaseRecyclerAdapter<NotificationItem, NotificationItemViewHolder>(options) {
            @NonNull
            @Override
            public NotificationItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
                return new NotificationItemViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final NotificationItemViewHolder holder, int position, @NonNull final NotificationItem model) {

                Log.i("date ", model.getDate());
                holder.setNotificationMessage(model.getDescription());
                holder.setDate(model.getDate());
                holder.setImage(model.getImageUrl(), getApplicationContext());
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
