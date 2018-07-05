package com.blogspot.zone4apk.gwaladairy.recyclerViewDashboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.zone4apk.gwaladairy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private ArrayList<ProductItem> arrayList;
    Context ctx;

    public RecyclerAdapter(ArrayList<ProductItem> arrayList, Context ctx) {
        this.arrayList = arrayList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public RecyclerAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new RecyclerViewHolder(view, arrayList, ctx);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.RecyclerViewHolder holder, int position) {
        ProductItem productItem = arrayList.get(position);
        holder.text_name.setText(productItem.getName());
        holder.text_description.setText(productItem.getDescription());
        holder.text_price.setText("\u20B9 "+String.valueOf(productItem.getPrice()));
        Picasso.with(ctx)
                .load(productItem.getImageurl())
                .transform(new CropSquareTransformation())
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final Context ctx;
        private final ArrayList<ProductItem> arrayList;
        TextView text_name;
        TextView text_description;
        TextView text_price;
        ImageView image;


        public RecyclerViewHolder(View itemView, ArrayList<ProductItem> arrayList, Context ctx) {
            super(itemView);
            this.ctx = ctx;
            this.arrayList = arrayList;
            text_name = (TextView) itemView.findViewById(R.id.product_name);
            text_description = (TextView) itemView.findViewById(R.id.product_description);
            text_price = (TextView) itemView.findViewById(R.id.product_price);
            image = (ImageView) itemView.findViewById(R.id.product_image);
        }
    }
}
