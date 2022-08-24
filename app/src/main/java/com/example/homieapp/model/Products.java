package com.example.homieapp.model;

import java.io.Serializable;

public class Products implements Serializable {

      String productId;
      String productName;
      String productQty;
      String procateId;
      String productPrice;
      String imageUrl;
      String description;
      String feedback;
      String[] offers;
      String discount;
    boolean favourite;
    int numberInCart;

    public Products() {
    }

    public Products(String productId, String productName, String productQty, String procateId, String productPrice, String imageUrl, String description, String feedback, String[] offers, String discount, boolean favourite, int numberInCart) {
        this.productId = productId;
        this.productName = productName;
        this.productQty = productQty;
        this.procateId = procateId;
        this.productPrice = productPrice;
        this.imageUrl = imageUrl;
        this.description = description;
        this.feedback = feedback;
        this.offers = offers;
        this.discount = discount;
        this.favourite = favourite;
        this.numberInCart = numberInCart;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getProcateId() {
        return procateId;
    }

    public void setProcateId(String procateId) {
        this.procateId = procateId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String[] getOffers() {
        return offers;
    }

    public void setOffers(String[] offers) {
        this.offers = offers;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public String  getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
