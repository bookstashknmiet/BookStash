package com.blogspot.zone4apk.gwaladairy.recyclerViewDashboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.zone4apk.gwaladairy.R;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    TextView text_name;
    TextView text_description;
    TextView text_price;
    TextView text_quantity;
    ImageView image;

    public ProductViewHolder(View itemView) {
        super(itemView);
        text_name = (TextView) itemView.findViewById(R.id.product_name);
        text_description = (TextView) itemView.findViewById(R.id.product_description);
        text_price = (TextView) itemView.findViewById(R.id.product_price);
        text_quantity = (TextView) itemView.findViewById(R.id.product_quantity);
        image = (ImageView) itemView.findViewById(R.id.product_image);
    }

    public void setText_name(String name) {
        text_name.setText(name);
    }

    public void setText_description(String description) {
        text_description.setText(description);
    }

    public void setText_price(String price) {
        text_price.setText(price);
    }

    public void setImage(String imageUrl, Context ctx) {
        Picasso.with(ctx)
                .load(imageUrl)
                .transform(new CropSquareTransformation())
                .into(image);

    }

    public void setText_quantity(String quantity) {
        text_quantity.setText(quantity);
    }

}
