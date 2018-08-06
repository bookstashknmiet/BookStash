package com.blogspot.zone4apk.gwaladairy.recyclerViewMyOrders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blogspot.zone4apk.gwaladairy.R;

import java.util.ArrayList;

public class MyOrderItemViewHolder extends RecyclerView.ViewHolder {

    ArrayList<MyOrderItem> myOrderItems;
    Context context;
    public TextView orderid;
    public TextView orderTime;
    public TextView orderAddress;
    public TextView orderStatus;
    public TextView cancleBtn;
    public TextView amountpaid;
    public TextView totalprice;
    public TextView itemcount;
    public TextView deliverycharges;
    public RecyclerView recyclerViewOrderItem;
    public ImageSwitcher imageSwitcher;
    public LinearLayout moreLayout;

    public MyOrderItemViewHolder(View itemView, ArrayList<MyOrderItem> myOrderItems, Context context) {
        super(itemView);
        this.myOrderItems = myOrderItems;
        this.context = context;

        orderid = itemView.findViewById(R.id.textView_orderId);
        orderTime = itemView.findViewById(R.id.textview_date_of_order);
        orderAddress = itemView.findViewById(R.id.text_Address);
        orderStatus = itemView.findViewById(R.id.orderStatus);
        amountpaid = itemView.findViewById(R.id.txt_amountPaid_order);
        totalprice = itemView.findViewById(R.id.txt_totalPrice_all_item_order);
        itemcount = itemView.findViewById(R.id.txtViewItemsCount_order);
        deliverycharges = itemView.findViewById(R.id.txt_delivery_charges_order);
        cancleBtn = itemView.findViewById(R.id.cancel_btn);
        recyclerViewOrderItem = itemView.findViewById(R.id.recyclerView_myOrder_items);
        imageSwitcher = itemView.findViewById(R.id.expand_collapse_detail_order);
        moreLayout = itemView.findViewById(R.id.more_content_order);
    }
}
