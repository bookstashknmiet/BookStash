package com.blogspot.zone4apk.gwaladairy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class MyAddressActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        RecyclerView recyclerView = findViewById(R.id.recyclerview_my_address);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // recyclerView.setAdapter(adapter);
    }
}
