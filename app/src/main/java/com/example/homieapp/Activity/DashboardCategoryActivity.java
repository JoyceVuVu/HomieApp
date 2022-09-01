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
import com.example.homieapp.R;
import com.example.homieapp.model.ProductCategory;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardCategoryActivity extends AppCompatActivity {
    RecyclerView cate_recycler;
    TextView activity_title;
    ImageButton back;
    DatabaseReference cate_reference;
    CategoryAdapter categoryAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        cate_recycler = findViewById(R.id.recycler_dashboard);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL,false);
        cate_recycler.setLayoutManager(layoutManager);
        activity_title = findViewById(R.id.dashboard_title);
        activity_title.setText("Category");
        back = findViewById(R.id.dashboard_back);
        back.setOnClickListener(view -> startActivity(new Intent(DashboardCategoryActivity.this, MainActivity.class)));

        cate_reference = FirebaseDatabase.getInstance().getReference("categories");
        FirebaseRecyclerOptions<ProductCategory> options =
                new FirebaseRecyclerOptions.Builder<ProductCategory>()
                .setQuery(cate_reference, ProductCategory.class)
                .build();
        categoryAdapter = new CategoryAdapter(options, this);
        cate_recycler.setAdapter(categoryAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        categoryAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        categoryAdapter.startListening();
    }
}
