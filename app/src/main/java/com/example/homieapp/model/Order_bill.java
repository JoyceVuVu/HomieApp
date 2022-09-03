package com.example.homieapp.model;

import java.util.ArrayList;
import java.util.List;

public class Order_bill {
    String id;
    String user_id;
    String username;
    String date;
    String phoneNumber;
    String address;
    String status;
    String payment_method;
    String total_price;
    ArrayList<Products> productsList;

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
        this.id = id;
        this.user_id = user_id;
        this.username = username;
        this.date = date;
        this.phoneNumber = phone_no;
        this.address = address;
        this.status = status;
        this.payment_method = payment_method;
        this.total_price = total_price;
        this.productsList = productsList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPhone_no() {
        return phoneNumber;
    }

    public void setPhone_no(String phone_no) {
        this.phoneNumber = phone_no;
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

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public ArrayList<Products> getProductsList() {
        return productsList;
    }

    public void setProductsList(ArrayList<Products> productsList) {
        this.productsList = productsList;
    }
}
