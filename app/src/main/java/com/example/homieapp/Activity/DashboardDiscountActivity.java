package com.example.homieapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Adapter.CategoryAdapter;
import com.example.homieapp.Adapter.DiscountAdapter;
import com.example.homieapp.R;
import com.example.homieapp.model.Discount;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Dictionary;

public class DashboardDiscountActivity extends AppCompatActivity {
    RecyclerView discount_recycler;
    TextView activity_title;
    ImageButton back;
    DatabaseReference discount_reference;
    DiscountAdapter discountAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        discount_recycler = findViewById(R.id.recycler_dashboard);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        discount_recycler.setLayoutManager(layoutManager);
        activity_title = findViewById(R.id.dashboard_title);
        activity_title.setText("Discount");
        back = findViewById(R.id.dashboard_back);
        back.setOnClickListener(view -> startActivity(new Intent(DashboardDiscountActivity.this, MainActivity.class)));
        discount_reference = FirebaseDatabase.getInstance().getReference("discounts");
        FirebaseRecyclerOptions<Discount> options =
                new FirebaseRecyclerOptions.Builder<Discount>()
                .setQuery(discount_reference, Discount.class)
                .build();
        discountAdapter = new DiscountAdapter(options);
        discount_recycler.setAdapter(discountAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        discountAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        discountAdapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DashboardDiscountActivity.this, MainActivity.class));
    }
}
