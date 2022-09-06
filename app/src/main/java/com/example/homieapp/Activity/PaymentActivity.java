package com.example.homieapp.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Adapter.PaymentAdapter;
import com.example.homieapp.R;
import com.example.homieapp.model.PaymentMethod;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PaymentActivity extends AppCompatActivity {
    ImageView back;
    TextView activity_title;
    RecyclerView recyclerView;
    PaymentAdapter adapter;
    DatabaseReference payment_reference;
    String user_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        back = findViewById(R.id.dashboard_back);
        activity_title =findViewById(R.id.dashboard_title);
        activity_title.setText("Payment Method");
        recyclerView = findViewById(R.id.recycler_dashboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        user_id = userDetails.get(SessionManager.KEY_SESSIONPHONENO);
        payment_reference = FirebaseDatabase.getInstance().getReference("users").child(user_id).child("payment_method");
        FirebaseRecyclerOptions<PaymentMethod> options = new FirebaseRecyclerOptions.Builder<PaymentMethod>()
                .setQuery(payment_reference, PaymentMethod.class)
                .build();
        adapter = new PaymentAdapter(options);
        recyclerView.setAdapter(adapter);
        registerForContextMenu(recyclerView);
        back.setOnClickListener(view -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
