package com.blogspot.zone4apk.gwaladairy.recyclerViewCart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.zone4apk.gwaladairy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;

/**
 * Created by AMIT on 7/9/2018.
 */

public class CartItemViewHolder extends RecyclerView.ViewHolder {

    TextView text_name;
    TextView text_description;
    TextView text_price;
    TextView text_quantity;
    ImageView image;

    //Buttons Setup
    TextView removeItem;
    TextView moveingBtnToWishlist;

    //DataBase setup
    FirebaseAuth mAuth;

    String itemId;
    String imageUrl;
    Long itemPrice;

    public CartItemViewHolder(View itemView) {
        super(itemView);
        mAuth = FirebaseAuth.getInstance();

        text_name = (TextView) itemView.findViewById(R.id.product_name_cart);
        text_description = (TextView) itemView.findViewById(R.id.product_description_cart);
        text_price = (TextView) itemView.findViewById(R.id.product_price_cart);
        text_quantity = (TextView) itemView.findViewById(R.id.product_quantity_cart);
        image = (ImageView) itemView.findViewById(R.id.product_image_cart);

        moveingBtnToWishlist = itemView.findViewById(R.id.button_move_to_wishlist);
        moveingBtnToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mWishListDataBase = FirebaseDatabase.getInstance().getReference().child("CartDatabase").child(mAuth.getCurrentUser().getUid().toString());
                mWishListDataBase.child(itemId).removeValue();

                DatabaseReference cartDatabase = FirebaseDatabase.getInstance().getReference().child("WishlistDatabase").child(mAuth.getCurrentUser().getUid().toString()).push();

                String pushId = cartDatabase.getKey();

                Map addToCartProductDetails = new HashMap();
                addToCartProductDetails.put("name", text_name.getText());
                addToCartProductDetails.put("description", text_description.getText());
                addToCartProductDetails.put("quantity", text_quantity.getText());
                addToCartProductDetails.put("price", itemPrice);
                addToCartProductDetails.put("image_url", imageUrl);
                addToCartProductDetails.put("itemId", pushId);

                cartDatabase.updateChildren(addToCartProductDetails, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                        if (databaseError == null) {
                            Log.i("Status", "Item is add to Wishlist");
                        }
                    }
                });
            }
        });

        removeItem = itemView.findViewById(R.id.button_remove_from_cart);
        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OnClick Handler for Delete button
                DatabaseReference mWishListDataBase = FirebaseDatabase.getInstance().getReference().child("CartDatabase").child(mAuth.getCurrentUser().getUid().toString());
                mWishListDataBase.child(itemId).removeValue();
            }
        });


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

    public void setPrice(Long itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setImage(String imageUrl, Context ctx) {
        Picasso.with(ctx)
                .load(imageUrl)
                .transform(new CropSquareTransformation())
                .into(image);

        this.imageUrl = imageUrl;
    }

    public void setText_quantity(String quantity) {
        text_quantity.setText(quantity);
    }

}

