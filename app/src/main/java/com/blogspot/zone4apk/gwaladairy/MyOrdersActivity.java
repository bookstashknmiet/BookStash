package com.blogspot.zone4apk.gwaladairy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.FrameLayout;

import com.blogspot.zone4apk.gwaladairy.recyclerViewMyOrderProducts.MyOrderProduct;
import com.blogspot.zone4apk.gwaladairy.recyclerViewMyOrderProducts.MyOrderProductViewHolder;
import com.blogspot.zone4apk.gwaladairy.recyclerViewMyOrders.MyOrderItem;
import com.blogspot.zone4apk.gwaladairy.recyclerViewMyOrders.MyOrderItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MyOrdersActivity extends AppCompatActivity {


    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    RecyclerView recyclerViewOrder;
    ArrayList<MyOrderItem> myOrderItems;
    RecyclerAdapter adapter;

    //Framelayouts
    FrameLayout frameLayoutContent;
    FrameLayout frameLayoutNoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        mAuth = FirebaseAuth.getInstance();

        myOrderItems = new ArrayList<>();
        frameLayoutContent = findViewById(R.id.framelayout_content_order);
        frameLayoutNoContent = findViewById(R.id.frmamelayout_nocontent_order);
        recyclerViewOrder = findViewById(R.id.recyclerView_Order);
        recyclerViewOrder.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrder.setHasFixedSize(true);
        adapter = new RecyclerAdapter(myOrderItems, getApplicationContext());
        //Database stuff
        databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderDatabase")
                .child(mAuth.getCurrentUser().getUid());
        Query query = databaseReference.orderByKey().limitToLast(25);       //Sorting orders and limiting to last 25
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //----------------We need to show no content page if there is no order
                long orderCount = dataSnapshot.getChildrenCount();
                if (orderCount == 0) {//hiding views when there is no item in cart.
                    frameLayoutNoContent.setVisibility(View.VISIBLE);
                    frameLayoutContent.setVisibility(View.GONE);

                } else {//showing view when there is any item in cart.
                    frameLayoutNoContent.setVisibility(View.GONE);
                    frameLayoutContent.setVisibility(View.VISIBLE);

                }


                myOrderItems.clear();
                Iterable<DataSnapshot> orderList = dataSnapshot.getChildren();
                for (DataSnapshot data : orderList) {
                    MyOrderItem details = new MyOrderItem();
                    Log.i("Check", String.valueOf(data));

                    //For taking orderNo------------------
                    String orderNo = data.getKey();

                    //For taking Addresss------------------
                    DataSnapshot deliveryAdd = data.child("deliveryAddress");
                    String address = deliveryAdd.child("name").getValue() +
                            "\n" +
                            deliveryAdd.child("addressLine1").getValue() +
                            ", " +
                            deliveryAdd.child("addressLine2").getValue() +
                            "\n" +
                            deliveryAdd.child("city").getValue() +
                            ", " +
                            deliveryAdd.child("state").getValue() +
                            "-" +
                            deliveryAdd.child("pincode").getValue() +
                            "\n" +
                            "Mob: " +
                            deliveryAdd.child("mobile").getValue() + "\n";

                    //For taking Status-----------------------
                    String orderStatus = "Status : " + data.child("paymentDetail").child("status").getValue();

                    //For taking order time------------
                    Date date = null;
                    try {
                        date = new SimpleDateFormat("yyyyMMdd-hhmmss").parse(data.getKey());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //For taking payment details
                    String amountPaid = "" + data.child("paymentDetail").child("amountPaid").getValue();
                    String totalPrice = "" + data.child("paymentDetail").child("totalPrice").getValue();
                    String deliveryCharges = "" + data.child("paymentDetail").child("deliveryCharges").getValue();
                    String itemsCount = "" + data.child("paymentDetail").child("itemCount").getValue();

                    //Putting values into the object
                    details.setOrderNo(orderNo);
                    details.setAddress(address);
                    details.setOrderstatus(orderStatus);
                    details.setTime(new SimpleDateFormat("EEEE, MMMM d, yyyy hh:mm:ss a").format(date));
                    details.setAmountPaid(amountPaid);
                    details.setTotalPrice(totalPrice);
                    details.setDeliveryCharges(deliveryCharges);
                    details.setItemsCount(itemsCount);
                    myOrderItems.add(details);
                }
                Collections.reverse(myOrderItems);      //reversing list so that last order appear at top
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerViewOrder.setAdapter(adapter);

    }

    private class RecyclerAdapter extends RecyclerView.Adapter<MyOrderItemViewHolder> {
        ArrayList<MyOrderItem> myOrderItems;
        Context context;

        public RecyclerAdapter(ArrayList<MyOrderItem> myOrderItems, Context context) {
            this.myOrderItems = myOrderItems;
            this.context = context;
        }

        @NonNull
        @Override
        public MyOrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_order, parent, false);
            return new MyOrderItemViewHolder(view, myOrderItems, context);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyOrderItemViewHolder holder, final int position) {
            final MyOrderItem myOrderItem = myOrderItems.get(position);
            //--------------
            holder.orderid.setText(myOrderItem.getOrderNo());
            holder.orderAddress.setText(myOrderItem.getAddress());
            holder.orderTime.setText(myOrderItem.getTime());
            holder.orderStatus.setText(myOrderItem.getOrderstatus());
            holder.amountpaid.setText("\u20B9 " + myOrderItem.getAmountPaid());
            holder.totalprice.setText("\u20B9 " + myOrderItem.getTotalPrice());
            holder.deliverycharges.setText("\u20B9 " + myOrderItem.getDeliveryCharges());
            switch (myOrderItem.getItemsCount()) {
                case "1":
                    holder.itemcount.setText("Price (1 item)");
                    break;
                default:
                    holder.itemcount.setText("Price (" + myOrderItem.getItemsCount() + " items)");
                    break;
            }

            //------------
            switch (myOrderItem.getOrderstatus()) {
                case "Status : Order Placed":
                    holder.orderStatus.setTextColor(Color.YELLOW);
                    holder.cancleBtn.setText("CANCEL ORDER");
                    holder.cancleBtn.setEnabled(true);
                    holder.cancleBtn.setVisibility(View.VISIBLE);

                    break;
                case "Status : Order Cancelled":
                    holder.orderStatus.setTextColor(Color.RED);
                    holder.cancleBtn.setText("ORDER CANCELLED");
                    holder.cancleBtn.setEnabled(false);
                    holder.cancleBtn.setVisibility(View.INVISIBLE);

                    break;
                case "Status : Order Delivered":
                    holder.orderStatus.setTextColor(Color.CYAN);
                    holder.cancleBtn.setText("ORDER DELIVERED");
                    holder.cancleBtn.setEnabled(false);
                    holder.cancleBtn.setVisibility(View.INVISIBLE);

                    break;
            }

            holder.cancleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence options[] = new CharSequence[]{"Cancel Order", "Dismiss"};
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MyOrdersActivity.this);
                    builder.setTitle("Are you sure?");
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setItems(options, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                //Cancel order
                                FirebaseDatabase.getInstance().getReference()
                                        .child("OrderDatabase")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(myOrderItem.getOrderNo())
                                        .child("paymentDetail")
                                        .child("status")
                                        .setValue("Order Cancelled");
                                dialog.dismiss();
                            }
                            if (which == 1) {
                                //Dismiss
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                }
            });

            //Setting expand collapse
            holder.imageSwitcher.setContentDescription("collapsed");
            if (holder.imageSwitcher.getContentDescription().toString().equals("expanded")) {
                holder.imageSwitcher.setImageResource(R.drawable.ic_expand_less_black_24dp);
                holder.imageSwitcher.setContentDescription("expanded");
                holder.moreLayout.setVisibility(View.VISIBLE);
            } else {
                holder.imageSwitcher.setImageResource(R.drawable.ic_expand_more_black_24dp);
                holder.imageSwitcher.setContentDescription("collapsed");
                holder.moreLayout.setVisibility(View.GONE);
            }
            //making it switchable
            holder.imageSwitcher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.imageSwitcher.getContentDescription().toString().equals("collapsed")) {
                        holder.imageSwitcher.setImageResource(R.drawable.ic_expand_less_black_24dp);
                        holder.imageSwitcher.setContentDescription("expanded");
                        holder.moreLayout.setVisibility(View.VISIBLE);
                    } else {
                        holder.imageSwitcher.setImageResource(R.drawable.ic_expand_more_black_24dp);
                        holder.imageSwitcher.setContentDescription("collapsed");
                        holder.moreLayout.setVisibility(View.GONE);
                    }
                }
            });

            //Setting recycler view for ordered items----------------------------------------------------------
            holder.recyclerViewOrderItem.setLayoutManager(new LinearLayoutManager(
                    context, LinearLayoutManager.HORIZONTAL, false));

            databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderDatabase")
                    .child(mAuth.getCurrentUser().getUid())
                    .child(myOrderItem.getOrderNo())
                    .child("productsOrdered");

            databaseReference.keepSynced(true);
            Query query = databaseReference.limitToLast(50);
            FirebaseRecyclerOptions<MyOrderProduct> options =
                    new FirebaseRecyclerOptions.Builder<MyOrderProduct>().setQuery(query, MyOrderProduct.class).build();

            holder.recyclerViewOrderItem.setItemAnimator(new DefaultItemAnimator());
            FirebaseRecyclerAdapter adapter =
                    new FirebaseRecyclerAdapter<MyOrderProduct, MyOrderProductViewHolder>(options) {
                        @NonNull
                        @Override
                        public MyOrderProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_order_product, parent, false);
                            return new MyOrderProductViewHolder(view, context);
                        }

                        @Override
                        protected void onBindViewHolder(@NonNull final MyOrderProductViewHolder holder, int position, @NonNull final MyOrderProduct model) {
                            holder.setText_name(model.getName());
                            holder.setText_description(model.getDescription());
                            holder.setText_price("\u20B9 " + String.valueOf(model.getPrice()));
                            holder.setImage(model.getImage_url(), context);
                            holder.setText_quantity(model.getQuantity());
                            holder.setItemId(model.getItemId());
                            holder.setPrice(model.getPrice());
                            holder.setProductCount(model.getProduct_count());
                        }
                    };
            holder.recyclerViewOrderItem.setAdapter(adapter);
            adapter.startListening();


        }

        @Override
        public int getItemCount() {
            return myOrderItems.size();
        }
    }
}
