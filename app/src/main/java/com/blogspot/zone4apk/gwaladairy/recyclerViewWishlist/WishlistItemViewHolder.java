package com.blogspot.zone4apk.gwaladairy.recyclerViewWishlist;

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

public class WishlistItemViewHolder extends RecyclerView.ViewHolder {

    //Views
    TextView text_name;
    TextView text_description;
    TextView text_price;
    TextView text_quantity;
    ImageView image;

    //Buttons Setup
    ImageView removeItem;
    TextView moveingBtnToCrat;

    //DataBase setup
    FirebaseAuth mAuth;

    String itemId;
    String imageUrl;
    Long itemPrice;




    public WishlistItemViewHolder(final View itemView) {
        super(itemView);
        mAuth = FirebaseAuth.getInstance();

        text_name = (TextView) itemView.findViewById(R.id.product_name_wishlist);
        text_description = (TextView) itemView.findViewById(R.id.product_description_wishlist);
        text_price = (TextView) itemView.findViewById(R.id.product_price_wishlist);
        text_quantity = (TextView) itemView.findViewById(R.id.product_quantity_wishlist);
        image = (ImageView) itemView.findViewById(R.id.product_image_wishlist);



        moveingBtnToCrat = itemView.findViewById(R.id.button_move_to_cart);
        moveingBtnToCrat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //removing data from wishlist db
                DatabaseReference mWishListDataBase = FirebaseDatabase.getInstance().getReference().child("WishlistDatabase").child(mAuth.getCurrentUser().getUid());
                mWishListDataBase.child(itemId).removeValue();

                //adding data to cart db
                DatabaseReference mCartDatabase = FirebaseDatabase.getInstance().getReference().child("CartDatabase").child(mAuth.getCurrentUser().getUid()).child(itemId);

                Map addToCartProductDetails = new HashMap();
                addToCartProductDetails.put("name", text_name.getText());
                addToCartProductDetails.put("description", text_description.getText());
                addToCartProductDetails.put("quantity", text_quantity.getText());
                addToCartProductDetails.put("price", itemPrice);
                addToCartProductDetails.put("image_url", imageUrl);
                addToCartProductDetails.put("itemId", itemId);
                addToCartProductDetails.put("product_count", 1);

                mCartDatabase.updateChildren(addToCartProductDetails, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            //If database transaction was successful
                            Log.i("Status", "Item is add to Cart");
                        }
                    }
                });

            }
        });

        removeItem = itemView.findViewById(R.id.button_delete_wishlist);
        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mWishListDataBase = FirebaseDatabase.getInstance().getReference().child("WishlistDatabase").child(mAuth.getCurrentUser().getUid().toString());
                mWishListDataBase.child(itemId).removeValue();
            }
        });
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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
}
