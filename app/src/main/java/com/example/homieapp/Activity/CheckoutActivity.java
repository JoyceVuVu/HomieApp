package com.example.homieapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Adapter.OrderAdapter;
import com.example.homieapp.Adapter.PaymentAdapter;
import com.example.homieapp.Interface.ItemClickListener;
import com.example.homieapp.R;
import com.example.homieapp.model.Cart;
import com.example.homieapp.model.PaymentMethod;
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

public class CheckoutActivity extends AppCompatActivity {
    private TextView activity_title, order_id, total_price, order_date, shipping_to, add_method, payment_method_error_toast;
    private ImageView back;
    private Button payment_btn;
    private EditText current_username, current_user_phone, current_user_address;
    private RecyclerView order_recycler;
    private Spinner payment_spinner;
    DatabaseReference order_reference, payment_reference, discount_reference, cart_reference, product_reference, bill_reference;
    String _order_id,randomID;
    String username, phone_no, address;
//    PaymentAdapter paymentAdapter;
    OrderAdapter orderAdapter;
    public static List<Cart> cartListItem = new ArrayList<>();
    String order_time, payment;
    List<Cart> list = new ArrayList<>();
    List<Products> productsList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);

        back =findViewById(R.id.checkout_back);
        activity_title = findViewById(R.id.activity_checkout_title);
        shipping_to = findViewById(R.id.checkout_shipping);
        current_username = findViewById(R.id.checkout_name_user);
        current_user_phone = findViewById(R.id.checkout_phone_no_user);
        current_user_address = findViewById(R.id.checkout_address_user);
        order_id = findViewById(R.id.checkout_order_id);
        order_recycler = findViewById(R.id.checkout_product_order_recycler);
        total_price = findViewById(R.id.checkout_total);
        payment_spinner = findViewById(R.id.checkout_payment_method_spinner);
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
        current_user_phone.setText("0"+phone_no);
        current_user_address.setText(address);

        current_username.setEnabled(false);
        current_user_phone.setEnabled(false);
        current_user_address.setEnabled(false);

        _order_id = getIntent().getStringExtra("order_id");
        randomID = getIntent().getStringExtra("randomID");
        order_time = getIntent().getStringExtra("order_time");
//         _order_id = "2022/09/05/08:03/HD96564516060C03D";
//        String randomID = "HD96564516060C03D";
//        order_time = "2022/09/05/08:03";

        order_reference = FirebaseDatabase.getInstance().getReference("users").child(phone_no).child("orders").child(_order_id);
        discount_reference = FirebaseDatabase.getInstance().getReference("discounts");
        cart_reference = FirebaseDatabase.getInstance().getReference("users").child(phone_no).child("carts");
        product_reference = FirebaseDatabase.getInstance().getReference("products");
        bill_reference = FirebaseDatabase.getInstance().getReference("bills");

        add_method.setOnClickListener(view -> startActivity(new Intent(CheckoutActivity.this, AddPaymentMethod.class)));
        back.setOnClickListener(view -> cancel());
        order_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        shipping_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_username.setEnabled(true);
                current_user_phone.setEnabled(true);
                current_user_address.setEnabled(true);
            }
        });
        payment_reference = FirebaseDatabase.getInstance().getReference("users").child(phone_no).child("payment_method");
        setSpinnerDataFromDB(payment_reference, this, payment_spinner);
        order_id.setText(randomID);
        order_date.setText(order_time);
        order_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String totalPrice = snapshot.child("totalPrice").getValue(String.class);
                total_price.setText(totalPrice);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void setSpinnerDataFromDB(DatabaseReference mRef, final Context context, final Spinner spinner) {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    payment_method_error_toast.setVisibility(View.GONE);
                    final List<String> listId = new ArrayList<String>();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        String categoryId = snap.child("account_number").getValue(String.class);
                        listId.add(categoryId);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listId);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner.setSelection(0);
                }else {
                    payment_method_error_toast.setVisibility(View.VISIBLE);
                    payment_method_error_toast.setText("There is no method");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void payment() {

        if (!validateName() | !validatePhone() | !validateAddress()) {
            return;
        }else {
            addBill();
        }
    }

    private void addBill() {
        String username = current_username.getText().toString().trim();
        String userphone = current_user_phone.getText().toString().trim();
        String address = current_user_address.getText().toString().trim();
        String payment = payment_spinner.getSelectedItem().toString().trim();

        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("user_id", phone_no);
        orderMap.put("username", username);
        orderMap.put("date", order_time);
        orderMap.put("phoneNumber", userphone);
        orderMap.put("address", address);
        orderMap.put("Confirmed", address);
        orderMap.put("payment_method", payment);
        bill_reference.child(_order_id).updateChildren(orderMap);
        order_reference.updateChildren(orderMap);
        order_reference.child("productList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    Cart cart = ds.getValue(Cart.class);
                    list.add(cart);
                }
                Log.d("cart", String.valueOf(list.size()));
                for (int i = 0; i < list.size(); i++) {
                    String numberInCart = list.get(i).getNumberInCart();
                    String quantity = list.get(i).getProducts().getQuantity();
                    String updateQuantity= String.valueOf(Integer.parseInt(quantity) - Integer.parseInt(numberInCart));
                    HashMap<String , Object> updateProduct = new HashMap<>();
                    updateProduct.put("quantity", updateQuantity);
                    product_reference.child(list.get(i).getProducts().getId()).updateChildren(updateProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(CheckoutActivity.this, "Product is updated in product_reference", Toast.LENGTH_SHORT).show();
                        }
                    });
                    DatabaseReference favorite_reference = FirebaseDatabase.getInstance().getReference("users").child(phone_no).child("favourite");
                    favorite_reference.child(list.get(i).getProducts().getId()).updateChildren(updateProduct);
                    cart_reference.child(list.get(i).getProducts().getId()).orderByChild("products/id").equalTo(list.get(i).getProducts().getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(CheckoutActivity.this, "Cart is updated", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    startActivity(new Intent(CheckoutActivity.this, SuccessOrder.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        order_reference.child("productList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartListItem.clear();
                for (DataSnapshot productSnap:snapshot.getChildren()) {
                    Cart cart = productSnap.getValue(Cart.class);
                    cartListItem.add(cart);
                    orderAdapter = new OrderAdapter(cartListItem, getApplicationContext());
                    order_recycler.setAdapter(orderAdapter);
                    orderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        payment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment();
                Toast.makeText(getApplicationContext(), "Btn is clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancel();
    }

    public void cancel() {
        order_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
                startActivity(new Intent(CheckoutActivity.this, CartActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        finish();
    }

    private boolean validateAddress() {
        String address = current_user_address.getText().toString().trim();

        if (address.isEmpty()){
            current_user_address.setError("Phone no is required!");
            current_user_address.requestFocus();
            return false;
        } else {
            current_user_address.setError(null);
            current_user_address.setEnabled(true);
            return true;
        }
    }

    private boolean validatePhone() {
        String userphone = current_user_phone.getText().toString().trim();
        if (userphone.isEmpty()){
            current_user_phone.setError("Phone no is required!");
            current_user_phone.requestFocus();
            return false;
        } else {
            current_user_phone.setError(null);
            current_user_phone.setEnabled(false);
            return true;
        }
    }

    private boolean validateName() {
        String username = current_username.getText().toString().trim();
        if (username.isEmpty()){
            current_username.setError("Phone no is required!");
            current_username.requestFocus();
            return false;
        } else {
            current_username.setError(null);
            current_username.setEnabled(true);
            return true;
        }
    }
}
