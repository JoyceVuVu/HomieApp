package com.example.homieapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import java.util.HashMap;

public class DashboardFavoriteProduct extends AppCompatActivity {
    private ImageButton back;
    private TextView activity_title;
    private RecyclerView recyclerView;
    DatabaseReference favourite_product_reference;
    ProductAdapter productAdapter;
    String user_íd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        back = findViewById(R.id.dashboard_back);
        activity_title = findViewById(R.id.dashboard_title);
        recyclerView = findViewById(R.id.recycler_dashboard);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL,false));
        back.setOnClickListener(view -> startActivity(new Intent(DashboardFavoriteProduct.this, MainActivity.class)));
        activity_title.setText("Favourite");

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        user_íd = userDetails.get(SessionManager.KEY_SESSIONPHONENO);
        favourite_product_reference = FirebaseDatabase.getInstance().getReference("users").child(user_íd).child("favourite");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(favourite_product_reference, Products.class)
                        .build();
        productAdapter = new ProductAdapter(options, getApplicationContext());
        recyclerView.setAdapter(productAdapter);
        productAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        productAdapter.stopListening();
    }
}
