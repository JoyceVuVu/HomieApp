package com.example.homieapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Adapter.ProductAdapter;
import com.example.homieapp.R;
import com.example.homieapp.model.Products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CategoryDetail extends AppCompatActivity {
    private ImageButton back;
    private TextView activity_title;
    private RecyclerView recyclerView;
    DatabaseReference category_reference, product_reference;
    String category_id;
    ProductAdapter productAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        //hooks
        back = findViewById(R.id.dashboard_back);
        activity_title = findViewById(R.id.dashboard_title);
        recyclerView = findViewById(R.id.recycler_dashboard);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        category_id = getIntent().getStringExtra("ID");

        category_reference = FirebaseDatabase.getInstance().getReference("categories");
        product_reference = FirebaseDatabase.getInstance().getReference("products");

        category_reference.child(category_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String category_name = snapshot.child("name").getValue(String.class);
                activity_title.setText(category_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        back.setOnClickListener(view -> startActivity(new Intent(CategoryDetail.this, MainActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query=product_reference.orderByChild("category").equalTo(category_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseRecyclerOptions<Products> options =
                        new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(query, Products.class)
                        .build();
                productAdapter = new ProductAdapter(options, getApplicationContext());
                recyclerView.setAdapter(productAdapter);
                productAdapter.startListening();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        productAdapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CategoryDetail.this, MainActivity.class));
    }
}
