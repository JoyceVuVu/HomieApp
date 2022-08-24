package com.example.homieapp.model;

import java.io.Serializable;

public class User {
    String full_name;
    String username;
    String email;
    String phone_no;
    String pass;
    String address;
    String gender;
    String ava;

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

    public User( String full_name, String username, String email, String phone_no, String pass, String address, String gender, String ava) {
        this.full_name = full_name;
        this.username = username;
        this.email = email;
        this.phone_no = phone_no;
        this.pass = pass;
        this.address = address;
        this.gender = gender;
        this.ava = ava;
    }


    public User(String username,String email) {
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAva() {
        return ava;
    }

    public void setAva(String ava) {
        this.ava = ava;
    }
}
