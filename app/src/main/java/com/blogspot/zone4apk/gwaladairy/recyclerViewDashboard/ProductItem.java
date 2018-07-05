package com.blogspot.zone4apk.gwaladairy.recyclerViewDashboard;

public class ProductItem {
    public ProductItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    private String name;
    private Long price;
    private String description;
    private String imageurl;
    //name is our key to fetch data

}
