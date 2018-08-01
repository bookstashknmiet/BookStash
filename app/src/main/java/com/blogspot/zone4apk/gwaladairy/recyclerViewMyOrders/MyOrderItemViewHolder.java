package com.blogspot.zone4apk.gwaladairy.recyclerViewMyOrders;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blogspot.zone4apk.gwaladairy.R;

import java.util.List;

/**
 * Created by AMIT on 7/27/2018.
 */

public class MyOrderItemViewHolder  extends ArrayAdapter {
    private List<MyOrderItem> myOrderDetailsList;
    private int resource;
    private Context context;

    public MyOrderItemViewHolder(@NonNull Context context, @LayoutRes int resource, @NonNull List<MyOrderItem> myOrderDetails) {
        super(context, resource, myOrderDetails);

        this.context = context;
        this.resource = resource;
        this.myOrderDetailsList = myOrderDetails;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        MyOrderItem details = myOrderDetailsList.get(position);

        View view = LayoutInflater.from(context).inflate(resource,parent,false);

        TextView orderid = view.findViewById(R.id.textView_orderId);

        TextView orderTime = view.findViewById(R.id.textview_date_of_order);


        orderid.setText("Order No. "+details.getOrderNo());
        orderTime.setText(details.getTime());

        return view;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return myOrderDetailsList.get(position);
    }

}
