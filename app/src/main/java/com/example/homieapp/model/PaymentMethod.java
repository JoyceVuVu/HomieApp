package com.example.homieapp.model;

public class PaymentMethod {
    String bank;
    String account_number;
    String cardholder_name;
    String ex_date;
    String CVV;
    boolean isChecked;

    public PaymentMethod() {
    }
    public PaymentMethod(String account_number){
        this.account_number = account_number;
    }
    public PaymentMethod(String bank, String account_number, String cardholder_name, String ex_date, String CVV) {
        this.bank = bank;
        this.account_number = account_number;
        this.cardholder_name = cardholder_name;
        this.ex_date = ex_date;
        this.CVV = CVV;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getCardholder_name() {
        return cardholder_name;
    }

    public void setCardholder_name(String cardholder_name) {
        this.cardholder_name = cardholder_name;
    }

    public String getEx_date() {
        return ex_date;
    }

    public void setEx_date(String ex_date) {
        this.ex_date = ex_date;
    }

    public String getCVV() {
        return CVV;
    }

    public void setCVV(String CVV) {
        this.CVV = CVV;
    }
}
