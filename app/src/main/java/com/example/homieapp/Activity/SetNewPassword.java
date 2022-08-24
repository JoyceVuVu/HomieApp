package com.example.homieapp.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.homieapp.R;
import com.example.homieapp.model.CheckInternet;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetNewPassword extends AppCompatActivity {
    ImageView logo;
    TextInputLayout in_password, in_confirm_password;
    Button confirm_btn;
    ProgressBar progressBar;
    Animation animation;
    String Password, Confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_set_new_password);

        //Hooks
        logo = findViewById(R.id.set_new_pass_logo);
        animation = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        logo.setAnimation(animation);

        in_password= findViewById(R.id.set_new_pass);
        in_confirm_password= findViewById(R.id.set_confirm_new_pass);
        confirm_btn= findViewById(R.id.confirm_btn);
        progressBar= findViewById(R.id.set_new_pass_progressbar);

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewPasswordBtn();
            }
        });
    }
    private void setNewPasswordBtn() {
        //Check internet connection
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(this)){
            showCustomDialog();
        }

        Password = in_password.getEditText().getText().toString().trim();
        Confirm_password = in_confirm_password.getEditText().getText().toString().trim();

        //check validate Password
        String passwordVal = "^"+
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";
        if (Password.isEmpty()){
            in_password.setError("Password is required!");
            in_password.requestFocus();
            return;
        }else if(!Password.matches(passwordVal)){
            in_password.setError("Password is too weak");
            in_password.requestFocus();
            return;
        }else {
            in_password.setError(null);
            in_password.setErrorEnabled(false);
        }

        if (Confirm_password.isEmpty()){
            in_confirm_password.setError("Confirm password is required!");
            in_confirm_password.requestFocus();
            return;
        }else if (!Confirm_password.equals(Password)){
            in_confirm_password.setError("Incorrect");
            in_confirm_password.requestFocus();
            return;
        }else {
            in_confirm_password.setError(null);
            in_confirm_password.setErrorEnabled(false);
        }
        progressBar.setVisibility(View.VISIBLE);

        String PhoneNo = getIntent().getStringExtra("phone_no");

        //Update Data in Firebase and in Sessions
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(PhoneNo).child("password").setValue(in_password);
        startActivity(new Intent(getApplicationContext(), ForgetPasswordSuccessMessage.class));
        finish();

    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetNewPassword.this);
        builder.setMessage("Please connect to the internet to proceed further")
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