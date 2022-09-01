package com.example.homieapp.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Adapter.CartAdapter;
import com.example.homieapp.Adapter.CartViewHolder;
import com.example.homieapp.Adapter.DiscountAdapter;
import com.example.homieapp.Adapter.ProductAdapter;
import com.example.homieapp.R;
import com.example.homieapp.model.Cart;
import com.example.homieapp.model.Discount;
import com.example.homieapp.model.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CartActivity extends AppCompatActivity {
    private ImageView back;
    private Button checkout;
    private RecyclerView _cart_item_recycler;
    private TextView total_product_price, delivery_service, discount, total_price;
    DatabaseReference product_reference, discount_reference, user_reference;
    CartAdapter cartAdapter;
    String user_id;
    int overTotalPrice, delivery = 25000, total_discount;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);

        //hooks
        back = findViewById(R.id.cart_back);
        checkout = findViewById(R.id.btn_checkout);
        _cart_item_recycler = findViewById(R.id.cart_item_recycler);
        _cart_item_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        total_product_price = findViewById(R.id.cart_totalPrice);
        delivery_service = findViewById(R.id.cart_delivery);
        discount = findViewById(R.id.cart_discount);
        total_price = findViewById(R.id.cart_total);
        SessionManager sessionManager = new SessionManager(getApplicationContext(), SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersDetails = sessionManager.getUserDetailFromSession();
        user_id  = usersDetails.get(SessionManager.KEY_SESSIONPHONENO);
        Log.d("user_id", user_id);
        delivery_service.setText(delivery + " VND");

        product_reference = FirebaseDatabase.getInstance().getReference("products");
        discount_reference = FirebaseDatabase.getInstance().getReference("discounts");

    }

    @Override
    protected void onStart() {
        super.onStart();
        user_reference = FirebaseDatabase.getInstance().getReference("users");
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(user_reference.child(user_id).child("carts"),Products.class )
                        .build();

        FirebaseRecyclerAdapter<Products, CartViewHolder> cartAdapter =
        new FirebaseRecyclerAdapter<Products, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Products model) {
                String id = model.getId();
                String name = model.getName();
                String price = model.getPrice();
                String quantity = model.getQuantity();
                Log.d("quantity", String.valueOf(quantity));
                String image = model.getImage();
                String numberInCart = model.getNumberInCart();
                Log.d("numberInCart", numberInCart);
                holder.product_name.setText(name);
                holder.product_price.setText(price + " VND");
                Picasso.with(holder.itemView.getContext()).load(image).fit().centerCrop().into(holder.product_img);
                holder.number_order.setText(numberInCart);
                String discount_id = model.getDiscount();

                discount_reference.orderByChild("id").equalTo(discount_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String percent = snapshot.child(discount_id).child("percent").getValue(String.class);
                        int discount_price_onePiece = (int) (Integer.parseInt(price) - Integer.parseInt(price) * Float.parseFloat(percent));
                        holder.product_price_discount.setText(discount_price_onePiece + " VND");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            overTotalPrice = overTotalPrice + Integer.parseInt(price) * Integer.parseInt(numberInCart);
                            total_product_price.setText(overTotalPrice + " VND");
                            discount_reference.child(discount_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String percent = snapshot.child("percent").getValue(String.class);
                                    total_discount = total_discount + (int) (Integer.parseInt(price) * Float.parseFloat(percent) * Integer.parseInt(numberInCart));
                                    discount.setText(total_discount + " VND");

                                    Log.d("total_discount", String.valueOf(total_discount));
                                    Log.d("overTotalPrice", String.valueOf(overTotalPrice));
                                    Log.d("delivery", String.valueOf(delivery));
                                    int totalPrice = overTotalPrice + delivery - total_discount;
                                    total_price.setText(totalPrice + " VND");

                                    checkout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                                            intent.putExtra("totalPrice", totalPrice);
                                            intent.putExtra("image", image);
                                            intent.putExtra("id", id);
                                            intent.putExtra("name", name);
                                            intent.putExtra("quantity", quantity);
                                            intent.putExtra("price", price);
                                            intent.putExtra("discount", discount_id);
                                            intent.putExtra("category", model.getCategory());
                                            intent.putExtra("numberInCart", numberInCart);
                                            intent.putExtra("total_discount",total_discount);
                                            //intent.putExtra("productTotalPrice", overTotalPrice);
                                            Log.d("intent", String.valueOf(intent));
                                            startActivity(intent);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else {
                            overTotalPrice = overTotalPrice - Integer.parseInt(price) * Integer.parseInt(numberInCart);
                            total_product_price.setText(overTotalPrice + " VND");
                            discount_reference.child(discount_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String percent = snapshot.child("percent").getValue(String.class);
                                    total_discount = total_discount - (int) (Integer.parseInt(price) * Float.parseFloat(percent) * Integer.parseInt(numberInCart));
                                    discount.setText(total_discount + " VND");

                                    Log.d("total_discount", String.valueOf(total_discount));
                                    Log.d("overTotalPrice", String.valueOf(overTotalPrice));
                                    Log.d("delivery", String.valueOf(delivery));
                                    int totalPrice = overTotalPrice - delivery - total_discount;
                                    total_price.setText(totalPrice + " VND");

                                    checkout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                                            intent.putExtra("totalPrice", String.valueOf(totalPrice));
                                            intent.putExtra("image", image);
                                            intent.putExtra("id", id);
                                            intent.putExtra("name", name);
                                            intent.putExtra("quantity", quantity);
                                            intent.putExtra("price", price);
                                            intent.putExtra("discount", discount_id);
                                            intent.putExtra("category", model.getCategory());
                                            intent.putExtra("numberInCart", numberInCart);
                                            intent.putExtra("total_discount",total_discount);
                                            //intent.putExtra("productTotalPrice", overTotalPrice);
                                            Log.d("intent", String.valueOf(intent));
                                            startActivity(intent);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                });

                holder.plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Integer.parseInt(numberInCart) < Integer.parseInt(quantity)){
                            int total_number  = Integer.parseInt(numberInCart) + 1;
                            HashMap<String, Object> cartMap = new HashMap<>();
                            cartMap.put("numberInCart", String.valueOf(total_number));
                            user_reference.child(user_id).child("carts").child(id).updateChildren(cartMap);
                            holder.number_order.setText(String.valueOf(total_number));
                        }
                    }
                });
                holder.minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Integer.parseInt(numberInCart) > 1){
                            int total_number = Integer.parseInt(numberInCart) - 1;
                            HashMap<String, Object> cartMap = new HashMap<>();
                            cartMap.put("numberInCart", String.valueOf(total_number));
                            user_reference.child(user_id).child("carts").child(id).updateChildren(cartMap);
                            holder.number_order.setText(String.valueOf(total_number));
                        }
                    }
                });
                holder.bin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(holder.itemView.getContext());
                            alertDialogBuilder.setMessage("Are you sure,You wanted to Remove\n"+holder.product_name.getText());
                            alertDialogBuilder.setCancelable(false);
                            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    user_reference.child(user_id).child("carts").child(id).removeValue();
                                    notifyItemRemoved(holder.getAdapterPosition());
                                }
                            });
                            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }catch (Exception e){
                        }
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row_item,parent, false);
                return new CartViewHolder(view);
            }
        };
        _cart_item_recycler.setAdapter(cartAdapter);
        cartAdapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
