package com.example.homieapp.model;

public class Cart {
   private Products products;
   private String numberInCart;

    public Cart() {
    }

    public Cart(Products products, String numberInCart) {
        this.products = products;
        this.numberInCart = numberInCart;
    }

    public Products getProducts() {
        return products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    public String getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(String numberInCart) {
        this.numberInCart = numberInCart;
    }
}
