package com.example.homieapp.model;

import java.io.Serializable;

public class User {
    String full_name;
    String username;
    String email;
    String phone_no;
    String pass;
    String address;
    String ava;
    String admin;

    public User() {
    }

    public User(String full_name, String username, String email, String phone_no, String password, String address){
        this.full_name = full_name;
        this.username = username;
        this.email = email;
        this.phone_no = phone_no;
        this.pass = password;
        this.address = address;
    }
    public User(String full_name, String username, String email, String phone_no, String pass, String address, String isAdmin) {
        this.full_name = full_name;
        this.username = username;
        this.email = email;
        this.phone_no = phone_no;
        this.pass = pass;
        this.address = address;
        this.admin = isAdmin;
    }

    public User(String full_name, String username, String email, String phone_no, String pass, String address, String ava, String isAdmin) {
        this.full_name = full_name;
        this.username = username;
        this.email = email;
        this.phone_no = phone_no;
        this.pass = pass;
        this.address = address;
        this.ava = ava;
        this.admin = isAdmin;
    }

//    public User(String full_name, String username, String email, String address, String ava, String isAdmin) {
//        this.full_name = full_name;
//        this.username = username;
//        this.email = email;
//        this.address = address;
//        this.ava = ava;
//        this.admin = isAdmin;
//    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAva() {
        return ava;
    }

    public void setAva(String ava) {
        this.ava = ava;
    }
}
