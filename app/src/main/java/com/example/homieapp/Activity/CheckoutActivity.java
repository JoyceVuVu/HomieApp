package com.example.homieapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Adapter.PaymentAdapter;
import com.example.homieapp.R;
import com.example.homieapp.model.PaymentMethod;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CheckoutActivity extends AppCompatActivity {
    private TextView activity_title, order_id, total_price, order_date, shipping_to, add_method, payment_method_error_toast;
    private ImageView back;
    private Button payment_btn;
    private EditText current_username, current_user_phone, current_user_address;
    private RecyclerView order_recycler, payment_recycler;
    DatabaseReference order_reference, payment_reference;
    String product_name, product_id, product_image, product_price, product_discount, totalPrice, numberInCart, product_category, status_order, product_quantity, currentDate;
    String username, phone_no, address;
    PaymentAdapter  paymentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);

        product_id = getIntent().getStringExtra("id");
        product_name = getIntent().getStringExtra("name");
        product_quantity = getIntent().getStringExtra("quantity");
        product_image = getIntent().getStringExtra("image");
        product_category = getIntent().getStringExtra("category");
        product_price = getIntent().getStringExtra("price");
        product_discount = getIntent().getStringExtra("discount");
        numberInCart = getIntent().getStringExtra("numberInCart");
        totalPrice = getIntent().getStringExtra("totalPrice");


        back =findViewById(R.id.checkout_back);
        activity_title = findViewById(R.id.activity_checkout_title);
        shipping_to = findViewById(R.id.checkout_shipping);
        current_username = findViewById(R.id.checkout_name_user);
        current_user_phone = findViewById(R.id.checkout_phone_no_user);
        current_user_address = findViewById(R.id.checkout_address_user);
        order_id = findViewById(R.id.checkout_order_id);
        order_recycler = findViewById(R.id.checkout_product_order_recycler);
        total_price = findViewById(R.id.checkout_total);
        payment_recycler = findViewById(R.id.checkout_payment_method_recycler);
        order_date = findViewById(R.id.checkout_order_time);
        payment_btn = findViewById(R.id.checkout_btn);
        add_method = findViewById(R.id.checkout_payment_method);
        payment_method_error_toast = findViewById(R.id.checkout_payment_toast);

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        phone_no = userDetails.get(SessionManager.KEY_SESSIONPHONENO);
        username = userDetails.get(SessionManager.KEY_USERNAME);
        address = userDetails.get(SessionManager.KEY_ADDRESS);

        current_username.setText(username);
        current_user_phone.setText(phone_no);
        current_user_address.setText(address);

        current_username.setEnabled(false);
        current_user_phone.setEnabled(false);
        current_user_address.setEnabled(false);

        order_reference = FirebaseDatabase.getInstance().getReference("users").child(phone_no);
        payment_reference = FirebaseDatabase.getInstance().getReference("users").child(phone_no);

        add_method.setOnClickListener(view -> startActivity(new Intent(CheckoutActivity.this, AddPaymentMethod.class)));
        order_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        payment_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        shipping_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_username.setEnabled(true);
                current_user_phone.setEnabled(true);
                current_user_address.setEnabled(true);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        payment_reference.child("payment_method").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    FirebaseRecyclerOptions<PaymentMethod> options =
                            new FirebaseRecyclerOptions.Builder<PaymentMethod>()
                                    .setQuery(payment_reference.child("payment_method"), PaymentMethod.class)
                                    .build();
                    paymentAdapter = new PaymentAdapter(options);
                    payment_recycler.setAdapter(paymentAdapter);
                    paymentAdapter.startListening();
                }else {
                    payment_method_error_toast.setText("There is no method");
                    payment_method_error_toast.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}
