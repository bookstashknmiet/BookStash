package com.blogspot.zone4apk.bookstash.recyclerViewMyOrderProducts;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.zone4apk.bookstash.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;

public class MyOrderProductViewHolder extends RecyclerView.ViewHolder {

    //Obtained from database
    TextView text_name;
    TextView text_description;
    TextView text_price;
    TextView text_quantity;
    String itemId;
    String imageUrl;
    long itemPrice;
    long productCount;

    //Imageview
    ImageView image;

    //Qty objects
    TextView tvQuantity;

    //DataBase setup
    FirebaseAuth mAuth;
    Context context;


    public MyOrderProductViewHolder(View itemView, final Context context) {
        super(itemView);
        this.context = context;
        mAuth = FirebaseAuth.getInstance();

        //Initializing views
        text_name = (TextView) itemView.findViewById(R.id.product_name_cart);
        text_description = (TextView) itemView.findViewById(R.id.product_description_cart);
        text_price = (TextView) itemView.findViewById(R.id.product_price_cart);
        text_quantity = (TextView) itemView.findViewById(R.id.product_quantity_cart);
        image = (ImageView) itemView.findViewById(R.id.product_image_cart);
        tvQuantity = (TextView) itemView.findViewById(R.id.tvQty);

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

    public void setPrice(long itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setImage(String imageUrl, Context ctx) {
        this.imageUrl = imageUrl;
        Picasso.with(ctx)
                .load(imageUrl)
                .transform(new CropSquareTransformation())
                .into(image);
    }

    public void setText_quantity(String quantity) {
        text_quantity.setText(quantity);
    }

    public void setProductCount(long productCount) {
        this.productCount = productCount;
        tvQuantity.setText(String.valueOf(productCount));
    }
}
