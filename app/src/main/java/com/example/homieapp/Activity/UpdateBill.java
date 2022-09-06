package com.example.homieapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homieapp.Adapter.OrderAdapter;
import com.example.homieapp.R;
import com.example.homieapp.model.Cart;
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

public class UpdateBill extends AppCompatActivity {
    private TextView order_bill_id, address, payment, totalPrice;
    String order_id,user_id;
    private RecyclerView recyclerView;
    private ImageView back;
    DatabaseReference order_reference,bill_references;
    public static List<Cart> cartListItem = new ArrayList<>();
    OrderAdapter orderAdapter;
    Button status_btn, update;
    String selectedStatus = "Confirmed";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bill);

        status_btn = findViewById(R.id.order_status);
        order_id = getIntent().getStringExtra("order_id");
        update = findViewById(R.id.update_bill);
        order_bill_id = findViewById(R.id.order_bill_id);
        address = findViewById(R.id.order_bill_address);
        payment = findViewById(R.id.order_bill_payment);
        totalPrice = findViewById(R.id.order_bill_totalPrice);
        recyclerView = findViewById(R.id.order_bill_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        back = findViewById(R.id.order_bill_back);
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        user_id = userDetails.get(SessionManager.KEY_SESSIONPHONENO);
        order_reference = FirebaseDatabase.getInstance().getReference("users").child(user_id).child("orders").child(order_id);
        bill_references = FirebaseDatabase.getInstance().getReference("bills").child(order_id);
        order_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                order_bill_id.setText(order_id);
                address.setText(snapshot.child("username").getValue(String.class) + "\n" + snapshot.child("phoneNumber").getValue(String.class) + "\n" + snapshot.child("address").getValue(String.class));
                payment.setText("Payment method: " + snapshot.child("payment_method").getValue(String.class));
                totalPrice.setText("Total: " + snapshot.child("totalPrice").getValue(String.class));
                status_btn.setText(snapshot.child("status").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        back.setOnClickListener(view -> finish());
        status_btn.setOnClickListener(view -> updateBill());
        update.setOnClickListener(view -> {
            String s = status_btn.getText().toString().trim();
            HashMap<String, Object> statusMap = new HashMap<>();
            statusMap.put("status", s);
            bill_references.updateChildren(statusMap);
            order_reference.updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(UpdateBill.this, "Update successfully", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateBill() {
        String[] status = {"Confirmed", "Shipping", "Done"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateBill.this);
        builder.setTitle("Choose status");
        builder.setSingleChoiceItems(status, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedStatus = status[i];
                Toast.makeText(UpdateBill.this, selectedStatus, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                status_btn.setText(selectedStatus);
                dialogInterface.dismiss();
            }
        });
        builder.show();

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