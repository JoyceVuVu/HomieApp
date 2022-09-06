package com.example.homieapp.model;

import java.util.ArrayList;
import java.util.List;

public class Order_bill {
    String order_id;
    String user_id;
    String username;
    String date;
    String phoneNumber;
    String address;
    String status;
    String payment_method;
    String totalPrice;
    List<Products> productsList;

    public Order_bill() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Order_bill(String user_id, String username, String date, String phone_no, String address, String status, String payment_method) {
        this.user_id = user_id;
        this.username = username;
        this.date = date;
        this.phoneNumber = phone_no;
        this.address = address;
        this.status = status;
        this.payment_method = payment_method;
    }

    public Order_bill(String id, String user_id,String username, String date, String phone_no, String address, String status, String payment_method, String total_price, ArrayList<Products> productsList) {
        this.order_id = id;
        this.user_id = user_id;
        this.username = username;
        this.date = date;
        this.phoneNumber = phone_no;
        this.address = address;
        this.status = status;
        this.payment_method = payment_method;
        this.totalPrice = total_price;
        this.productsList = productsList;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }


    public List<Products> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<Products> productsList) {
        this.productsList = productsList;
    }
}
