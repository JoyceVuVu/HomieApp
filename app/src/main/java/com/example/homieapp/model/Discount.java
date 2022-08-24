package com.example.homieapp.model;

public class Discount {
    private String discount_id;
    private String discount_name;
    private String discount_description;
    private String discount_percent;
    private String discount_imageUrl;

    public Discount() {
    }

    public Discount(String discount_id, String discount_name, String discount_description, String discount_percent, String discount_imageUrl) {
        this.discount_id = discount_id;
        this.discount_name = discount_name;
        this.discount_description = discount_description;
        this.discount_percent = discount_percent;
        this.discount_imageUrl = discount_imageUrl;
    }

    public String getDiscount_id() {
        return discount_id;
    }

    public void setDiscount_id(String discount_id) {
        this.discount_id = discount_id;
    }

    public String getDiscount_name() {
        return discount_name;
    }

    public void setDiscount_name(String discount_name) {
        this.discount_name = discount_name;
    }

    public String getDiscount_description() {
        return discount_description;
    }

    public void setDiscount_description(String discount_description) {
        this.discount_description = discount_description;
    }

    public String getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(String discount_percent) {
        this.discount_percent = discount_percent;
    }

    public String getDiscount_imageUrl() {
        return discount_imageUrl;
    }

    public void setDiscount_imageUrl(String discount_imageUrl) {
        this.discount_imageUrl = discount_imageUrl;
    }
}
