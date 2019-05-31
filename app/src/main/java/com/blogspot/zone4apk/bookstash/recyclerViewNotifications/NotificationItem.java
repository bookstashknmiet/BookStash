package com.blogspot.zone4apk.bookstash.recyclerViewNotifications;

/**
 * Created by AMIT on 8/9/2018.
 */

public class NotificationItem {

    public NotificationItem() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String date;
    public String description;
    public String imageUrl;

}
