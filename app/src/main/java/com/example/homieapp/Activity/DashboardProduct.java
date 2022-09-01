package com.example.homieapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Adapter.CategoryAdapter;
import com.example.homieapp.Adapter.DiscountAdapter;
import com.example.homieapp.Adapter.ProductAdapter;
import com.example.homieapp.R;
import com.example.homieapp.model.Discount;
import com.example.homieapp.model.Products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardProduct extends AppCompatActivity {
    RecyclerView product_recycler;
    TextView activity_title;
    ImageButton back;
    DatabaseReference product_reference;
    ProductAdapter productAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        product_recycler = findViewById(R.id.recycler_dashboard);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        product_recycler.setLayoutManager(layoutManager);
        activity_title = findViewById(R.id.dashboard_title);
        activity_title.setText("Product");
        back = findViewById(R.id.dashboard_back);
        back.setOnClickListener(view -> startActivity(new Intent(DashboardProduct.this, MainActivity.class)));
        product_reference = FirebaseDatabase.getInstance().getReference("products");
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(product_reference, Products.class)
                        .build();
        productAdapter = new ProductAdapter(options, this);
        product_recycler.setAdapter(productAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        productAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        productAdapter.stopListening();
    }
}
