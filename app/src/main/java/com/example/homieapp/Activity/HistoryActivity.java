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

import com.example.homieapp.R;
import com.example.homieapp.model.Order_bill;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity {

    ImageView back;
    RecyclerView recyclerView;
    DatabaseReference bill_reference;
    String user_id;
    FirebaseRecyclerAdapter<Order_bill, OrderBillViewHolder> adapter;
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
        FirebaseRecyclerOptions<Order_bill> options = new FirebaseRecyclerOptions.Builder<Order_bill>()
                .setQuery(bill_reference, Order_bill.class)
                .build();
         adapter = new FirebaseRecyclerAdapter<Order_bill, OrderBillViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderBillViewHolder holder, int position, @NonNull Order_bill model) {
                holder.bill_date.setText(model.getDate());
                holder.bill_id.setText(model.getId());
                holder.bill_totalPrice.setText(model.getTotal_price());
            }

            @NonNull
            @Override
            public OrderBillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new OrderBillViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_row, parent, false));
            }
        };
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private class OrderBillViewHolder extends RecyclerView.ViewHolder {
        TextView bill_date, bill_id, bill_totalPrice;
        public OrderBillViewHolder(@NonNull View itemView) {
            super(itemView);
            bill_date = itemView.findViewById(R.id.bill_date);
            bill_id = itemView.findViewById(R.id.bill_id);
            bill_totalPrice = itemView.findViewById(R.id.bill_totalPrice);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(HistoryActivity.this, MainActivity.class));
    }
}