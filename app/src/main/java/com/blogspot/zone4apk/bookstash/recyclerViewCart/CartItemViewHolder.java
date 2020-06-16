package com.blogspot.zone4apk.bookstash.recyclerViewCart;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.zone4apk.bookstash.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;

public class CartItemViewHolder extends RecyclerView.ViewHolder {

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
    ImageView incQuantity;
    ImageView decQuantity;
    //Buttons
    TextView removeItem;
    TextView moveingBtnToWishlist;

    //DataBase setup
    FirebaseAuth mAuth;
    Context context;


    public CartItemViewHolder(View itemView, final Context context) {
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

        //Quantity views
        incQuantity = (ImageView) itemView.findViewById(R.id.btnIncQty);
        decQuantity = (ImageView) itemView.findViewById(R.id.btnDecQty);
        incQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productCount < 10)
                    updateQty(productCount + 1);
            }
        });

        decQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productCount > 1)
                    updateQty(productCount - 1);
            }
        });


        //Moving to wishlist on button press
        moveingBtnToWishlist = itemView.findViewById(R.id.button_move_to_wishlist);
        moveingBtnToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //cart database
                DatabaseReference mCartDatabase = FirebaseDatabase.getInstance().getReference().child("CartDatabase")
                        .child(mAuth.getCurrentUser().getUid());
                mCartDatabase.child(itemId).removeValue();

                //wishlist database
                DatabaseReference mWishListDataBase = FirebaseDatabase.getInstance().getReference().child("WishlistDatabase").child(mAuth.getCurrentUser().getUid().toString()).child(itemId);

                Map addToCartProductDetails = new HashMap();
                addToCartProductDetails.put("name", text_name.getText());
                addToCartProductDetails.put("description", text_description.getText());
                addToCartProductDetails.put("quantity", text_quantity.getText());
                addToCartProductDetails.put("price", itemPrice);
                addToCartProductDetails.put("image_url", imageUrl);
                addToCartProductDetails.put("itemId", itemId);

                mWishListDataBase.updateChildren(addToCartProductDetails, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            //If database transaction was successful
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

    public void updateQty(long qty) {
        DatabaseReference mCartDb = FirebaseDatabase.getInstance().getReference().child("CartDatabase")
                .child(mAuth.getCurrentUser().getUid()).child(itemId);
        Map updateQuantityCount = new HashMap();
        updateQuantityCount.put("name", text_name.getText());
        updateQuantityCount.put("description", text_description.getText());
        updateQuantityCount.put("quantity", text_quantity.getText());
        updateQuantityCount.put("price", itemPrice);
        updateQuantityCount.put("image_url", imageUrl);
        updateQuantityCount.put("itemId", itemId);
        updateQuantityCount.put("product_count", qty);
        mCartDb.updateChildren(updateQuantityCount);
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

