package com.example.homieapp.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homieapp.R;
import com.example.homieapp.model.CheckInternet;
import com.example.homieapp.model.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    TextView back_txt;
    private ImageView logo;
    private Button signup_btn;
    private TextInputLayout in_full_name, in_user_name, in_email, in_password, in_phone_no, in_confirm_password, in_address;
    private ProgressBar progressBar;

//    FirebaseDatabase rootNode;
//    DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.signup_activity);

        //Hooks
        signup_btn = (Button) findViewById(R.id.signup_btn);

        in_full_name = findViewById(R.id.full_name);
        in_user_name = findViewById(R.id.user_name);
        in_email = findViewById(R.id.email);
        in_phone_no = findViewById(R.id.phone_no);
        in_password = findViewById(R.id.password);
        in_confirm_password = findViewById(R.id.confirm_password);
        in_address = findViewById(R.id.address);
        back_txt = findViewById(R.id.back_txt);
        logo = findViewById(R.id.login_logo);

        progressBar = findViewById(R.id.register_progress_bar);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckInternet checkInternet = new CheckInternet();
                if (!checkInternet.isConnected(RegisterActivity.this)){
                    showCustomDialog();
                }else {
                    RegisterUser();
                }
            }
        });
        back_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back();
            }
        });
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setMessage("Please connect to the internet to proceed furthe")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), IntroActivity.class));
                        finish();
                    }
                });
    }

    private void RegisterUser() {
        String full_name = in_full_name.getEditText().getText().toString().trim();
        String username = in_user_name.getEditText().getText().toString().trim();
        String email = in_email.getEditText().getText().toString().trim();
        String phone_no = in_phone_no.getEditText().getText().toString().trim();
        String password = in_password.getEditText().getText().toString().trim();
        String confirm_password = in_confirm_password.getEditText().getText().toString().trim();
        String address = in_address.getEditText().getText().toString().trim();

        if (!validateFullName(full_name) | !validateUserName(username) | !validateEmail(email) | !validatePhoneNo(phone_no) | !validatePassWord(password) | !validateConfirmPass(confirm_password, password) | !validateAddress(address)){
            return;
        }

        Intent intent = new Intent(getApplicationContext(), VerifyPhoneNo.class);
        intent.putExtra("phone_no",phone_no);
        intent.putExtra("full_name", full_name);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("address", address);
        intent.putExtra("WhatToDo", "registerUser");

        startActivity(intent);


    }

    private void Back() {
        Intent iBack = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(iBack);
    }


    private boolean validateAddress(String address) {
        if (address.isEmpty()) {
            in_address.setError("Address is required!");
            in_address.requestFocus();
            return false;
        } else {
            in_address.setError(null);
            in_address.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassWord(String password) {
        String passwordVal = "^" +
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=*])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";
        if (password.isEmpty()) {
            in_password.setError("Password is required!");
            in_password.requestFocus();
            return false;
        } else if (!password.matches(passwordVal)) {
            in_password.setError("Password is too weak");
            in_password.requestFocus();
            return false;
        } else {
            in_password.setError(null);
            in_password.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validateConfirmPass(String confirm_password, String password){
        if (confirm_password.isEmpty()) {
            in_confirm_password.setError("Confirm password is required!");
            in_confirm_password.requestFocus();
            return false;
        } else if (!confirm_password.equals(password)) {
            in_confirm_password.setError("Incorrect");
            in_confirm_password.requestFocus();
            return false;
        } else {
            in_confirm_password.setError(null);
            in_confirm_password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePhoneNo(String phone_no) {
        if (phone_no.isEmpty()) {
            in_phone_no.setError("Phone no is required!");
            in_phone_no.requestFocus();
            return false;
        } else {
            in_phone_no.setError(null);
            in_phone_no.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            in_email.setError("Email is required!");
            in_email.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            in_email.setError("Please provide valid email");
            in_email.requestFocus();
            return false;
        } else {
            in_email.setError(null);
            in_email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateUserName(String username) {
        String noWhiteSpace = "Aw{1,20}z";
        if (username.isEmpty()) {
            in_user_name.setError("User name is required!");
            in_user_name.requestFocus();
            return false;
        } else if (username.length() > 15) {
            in_user_name.setError("Username too long");
            return false;
        } else if (username.matches(noWhiteSpace)) {
            in_user_name.setError("White Spaces are not allowed");
            return false;
        } else {
            in_user_name.setError(null);
            in_user_name.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateFullName(String full_name) {
        //check validate full_name
        if (full_name.isEmpty()) {
            in_full_name.setError("Full name is required!");
            in_full_name.requestFocus();
            return false;
        } else {
            in_full_name.setError(null);
            in_full_name.setErrorEnabled(false);
            return true;
        }
    }

}

