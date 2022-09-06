package com.example.homieapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homieapp.Adapter.OrderAdapter;
import com.example.homieapp.R;
import com.example.homieapp.model.Cart;
import com.example.homieapp.model.Order_bill;
import com.example.homieapp.model.Products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BillDetail extends AppCompatActivity {
    private TextView order_bill_id, address, payment, totalPrice;
    String order_id,user_id;
    private RecyclerView recyclerView;
    private ImageView back;
    private Button status;
    DatabaseReference bill_references;
    public static List<Cart> cartListItem = new ArrayList<>();
    OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        order_id = getIntent().getStringExtra("order_id");
        order_bill_id = findViewById(R.id.order_bill_id);
        address = findViewById(R.id.order_bill_address);
        payment = findViewById(R.id.order_bill_payment);
        totalPrice = findViewById(R.id.order_bill_totalPrice);
        recyclerView = findViewById(R.id.order_bill_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        back = findViewById(R.id.order_bill_back);
        status = findViewById(R.id.order_status);

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        user_id = userDetails.get(SessionManager.KEY_SESSIONPHONENO);
        bill_references = FirebaseDatabase.getInstance().getReference("users").child(user_id).child("orders").child(order_id);
        bill_references.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                order_bill_id.setText(order_id);
                address.setText(snapshot.child("username").getValue(String.class) + "\n" + snapshot.child("phoneNumber").getValue(String.class) + "\n" + snapshot.child("address").getValue(String.class));
                payment.setText("Payment method: " + snapshot.child("payment_method").getValue(String.class));
                totalPrice.setText("Total: " + snapshot.child("totalPrice").getValue(String.class));
                status.setText(snapshot.child("status").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        cartListItem.clear();
        bill_references.child("productList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()) {
                    Cart cart = ds.getValue(Cart.class);
                    cartListItem.add(cart);
                    orderAdapter = new OrderAdapter(cartListItem, getApplicationContext());
                    recyclerView.setAdapter(orderAdapter);
                    orderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}