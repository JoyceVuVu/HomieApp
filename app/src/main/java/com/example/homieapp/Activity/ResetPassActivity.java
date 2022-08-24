package com.example.homieapp.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homieapp.R;
import com.example.homieapp.model.CheckInternet;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ResetPassActivity extends AppCompatActivity {

    TextView login;
    ImageView logo;
    TextInputLayout phone_no;
    Button reset_btn;
    ProgressBar progressBar;
    Animation animation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.reset_password);

        //Hooks
        logo = findViewById(R.id.reset_logo);
        animation = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        logo.setAnimation(animation);

        phone_no = findViewById(R.id.reset_phone_no);
        reset_btn= findViewById(R.id.reset_btn);
        login =findViewById(R.id.reset_login);
        progressBar =findViewById(R.id.reset_progressbar);

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPhone();
            }
        });

    }
    public void verifyPhone(){
        //check Internet Connection
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(this)){
            showCustomDialog();
        }

        //Check validate phone_no
        String PhoneNo = phone_no.getEditText().getText().toString().trim();
        if (PhoneNo.isEmpty()){
            phone_no.setError("Please enter your phone number!");
            phone_no.requestFocus();
        }else {
            phone_no.setError(null);
            phone_no.setErrorEnabled(false);
        }

        progressBar.setVisibility(View.VISIBLE);

        //check user exists or not in database
        Query checkUser = FirebaseDatabase.getInstance().getReference("users").orderByChild("phone_no").equalTo(PhoneNo);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    phone_no.setError(null);
                    phone_no.setErrorEnabled(false);

                    Intent intent = new Intent(getApplicationContext(),VerifyPhoneNo.class);
                    intent.putExtra("phone_no", PhoneNo);
                    intent.putExtra("WhatToDo", "updatePass");
                    startActivity(intent);
                    finish();

                    progressBar.setVisibility(View.GONE);
                }else {
                    progressBar.setVisibility(View.GONE);
                    phone_no.setError("No such user exist");
                    phone_no.requestFocus();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassActivity.this);
        builder.setMessage("Please connect to the internet to process further")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        finish();
                    }
                });
    }

}
