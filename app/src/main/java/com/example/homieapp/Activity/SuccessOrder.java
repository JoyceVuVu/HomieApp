package com.example.homieapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homieapp.R;

public class SuccessOrder extends AppCompatActivity {

    private Button home, history;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_success);

        home = findViewById(R.id.order_successful_home);
        history = findViewById(R.id.order_successful_bill);

        home.setOnClickListener(view -> startActivity(new Intent(SuccessOrder.this,MainActivity.class)));
        history.setOnClickListener(view -> startActivity(new Intent(SuccessOrder.this, HistoryActivity.class)));

    }
}
