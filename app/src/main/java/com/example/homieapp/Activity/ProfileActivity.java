package com.example.homieapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homieapp.R;

public class ProfileActivity extends AppCompatActivity {
    TextView full_name, username, email, address, phone_no;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        //Hooks
        full_name = (TextView) findViewById(R.id.profile_full_name);
        username = (TextView) findViewById(R.id.profile_username);
        email = (TextView) findViewById(R.id.profile_email);
        address = (TextView) findViewById(R.id.profile_address);
        phone_no = (TextView) findViewById(R.id.profile_phone_n0);

        showAllUserData();

    }

    private void showAllUserData() {
    }
}
