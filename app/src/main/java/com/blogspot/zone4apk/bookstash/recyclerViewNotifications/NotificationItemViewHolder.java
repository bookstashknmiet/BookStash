package com.blogspot.zone4apk.bookstash.recyclerViewNotifications;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.zone4apk.bookstash.R;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;

/**
 * Created by AMIT on 8/9/2018.
 */


public class NotificationItemViewHolder extends RecyclerView.ViewHolder {

    TextView description, notificationdate;
    ImageView notificationImage;

    public NotificationItemViewHolder(View itemView) {
        super(itemView);
        description = itemView.findViewById(R.id.notificationMessage);
        notificationdate = itemView.findViewById(R.id.notificationDate);
        notificationImage = itemView.findViewById(R.id.notificationImage);
    }

    public void setNotificationMessage(String name) {
        description.setText(name);
    }

    public void setDate(String date) {
        notificationdate.setText(date);
    }

    public void setImage(String imageUrl, Context ctx) {
        Picasso.with(ctx)
                .load(imageUrl)
                .transform(new CropSquareTransformation())
                .into(notificationImage);
    }

}
