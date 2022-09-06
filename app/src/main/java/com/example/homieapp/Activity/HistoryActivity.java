package com.example.homieapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homieapp.Adapter.OrderBillAdapter;
import com.example.homieapp.R;
import com.example.homieapp.model.Order_bill;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    ImageView back;
    RecyclerView recyclerView;
    DatabaseReference bill_reference;
    String user_id;
    OrderBillAdapter adapter;
    List<Order_bill> order_billList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        back = findViewById(R.id.history_back);
        recyclerView = findViewById(R.id.history_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        user_id = userDetails.get(SessionManager.KEY_SESSIONPHONENO);

        bill_reference = FirebaseDatabase.getInstance().getReference("users").child(user_id).child("orders");
        bill_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                order_billList.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Order_bill order_bill = ds.getValue(Order_bill.class);
                    order_billList.add(order_bill);
                    adapter = new OrderBillAdapter(order_billList, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        back.setOnClickListener(view -> startActivity(new Intent(HistoryActivity.this, MainActivity.class)));

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(HistoryActivity.this, MainActivity.class));
    }
}