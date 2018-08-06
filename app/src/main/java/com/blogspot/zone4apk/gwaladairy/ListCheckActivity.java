package com.blogspot.zone4apk.gwaladairy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListCheckActivity extends AppCompatActivity {

    ListView listView;

    ArrayList<String> listItems = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_check);

        listView = findViewById(R.id.listViewcheck);
        TextView orderNo = findViewById(R.id.textView_orderId);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListCheckActivity.this, "Item clicked is " + position, Toast.LENGTH_SHORT).show();
            }
        });

        ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.item_my_order, listItems);
        listView.setAdapter(adapter);

        for (int i = 0; i < 25; i++) {
            listItems.add(String.valueOf(i));

        }
    }
}
