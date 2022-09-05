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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Adapter.CartViewHolder;
import com.example.homieapp.R;
import com.example.homieapp.model.Cart;
import com.example.homieapp.model.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CartActivity extends AppCompatActivity {
    private ImageView back;
    private Button checkout;
    private RecyclerView _cart_item_recycler;
    private TextView total_product_price, delivery_service, discount, total_price, cart_toast;
    DatabaseReference product_reference, discount_reference, user_reference, bill_reference;
    String user_id;
    int delivery = 25000;
    public static List<Cart> cartList = new ArrayList<>();
    int overTotalPrice, total_discount;
    String  totalPrice;
    boolean isCheckBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);

        //hooks
        back = findViewById(R.id.cart_back);
        checkout = findViewById(R.id.btn_checkout);
        _cart_item_recycler = findViewById(R.id.cart_item_recycler);
        _cart_item_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        total_product_price = findViewById(R.id.cart_totalPrice);
        delivery_service = findViewById(R.id.cart_delivery);
        discount = findViewById(R.id.cart_discount);
        total_price = findViewById(R.id.cart_total);
        cart_toast = findViewById(R.id.cart_toast);

        SessionManager sessionManager = new SessionManager(getApplicationContext(), SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersDetails = sessionManager.getUserDetailFromSession();
        user_id = usersDetails.get(SessionManager.KEY_SESSIONPHONENO);
        Log.d("user_id", user_id);
        delivery_service.setText(delivery + " VND");

        product_reference = FirebaseDatabase.getInstance().getReference("products");
        discount_reference = FirebaseDatabase.getInstance().getReference("discounts");
        bill_reference = FirebaseDatabase.getInstance().getReference("bills");
        cartList.clear();

    }

    @Override
    protected void onStart() {
        super.onStart();
        total_product_price.setText("0 VND");
        discount.setText("0 VND");
        total_price.setText("0 VND");
        user_reference = FirebaseDatabase.getInstance().getReference("users");

        user_reference.child(user_id).child("carts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseRecyclerOptions<Cart> options =
                        new FirebaseRecyclerOptions.Builder<Cart>()
                                .setQuery(user_reference.child(user_id).child("carts"), Cart.class)
                                .build();
                FirebaseRecyclerAdapter<Cart, CartViewHolder> cartAdapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                        String id = model.getProducts().getId();
                        String name = model.getProducts().getName();
                        String price = model.getProducts().getPrice();
                        String discount_id = model.getProducts().getDiscount();
                        String image = model.getProducts().getImage();
                        String numberInCart = model.getNumberInCart();

                        holder.product_name.setText(name);
                        holder.product_price.setText(price + " VND");
                        Picasso.with(holder.itemView.getContext()).load(image).fit().centerCrop().into(holder.product_img);
                        holder.number_order.setText(numberInCart);

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
                                discount_reference.child(discount_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String percent = snapshot.child("percent").getValue(String.class);
                                        int total_discount_onepiece = (int) (Integer.parseInt(price) * Float.parseFloat(percent) * Integer.parseInt(numberInCart));
                                        int price_oncPiece = Integer.parseInt(price) * Integer.parseInt(numberInCart);
                                        //                                                        model.setSelected(true);
                                        //                                                    }else {
                                        //                                                        model.setSelected(false);
                                        isCheckBox = b;
                                        if (isCheckBox == true) {
                                            overTotalPrice = overTotalPrice + price_oncPiece;
                                            total_discount = total_discount + total_discount_onepiece;
                                            totalPrice = String.valueOf(overTotalPrice - total_discount + delivery);
                                            cartList.add(model);
                                        } else {
                                            overTotalPrice = overTotalPrice - price_oncPiece;
                                            total_discount = total_discount - total_discount_onepiece;
                                            if (overTotalPrice == 0) {
                                                totalPrice = String.valueOf(0);
                                            } else {
                                                totalPrice = String.valueOf(overTotalPrice - total_discount + delivery);
                                            }
                                            cartList.remove(model);
                                        }
                                        total_product_price.setText(overTotalPrice + " VND");
                                        discount.setText(total_discount + " VND");
                                        total_price.setText(totalPrice + " VND");

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        holder.plus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Integer.parseInt(numberInCart) < Integer.parseInt(model.getProducts().getQuantity())) {
                                    int total_number = Integer.parseInt(numberInCart) + 1;
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
                                if (Integer.parseInt(numberInCart) > 1) {
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
                                    alertDialogBuilder.setMessage("Are you sure,You wanted to Remove\n" + holder.product_name.getText());
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
                                } catch (Exception e) {
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row_item, parent, false);
                        return new CartViewHolder(view);
                    }

                };
                _cart_item_recycler.setAdapter(cartAdapter);
                cartAdapter.startListening();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH:mm/");
                String date = df.format(Calendar.getInstance().getTime());
                String randomID = UUID.randomUUID().toString().toUpperCase().substring(0,6);
                String order_id = date +  "HD" + user_id + randomID ;
                if (cartList.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please choose your products ...", Toast.LENGTH_SHORT).show();
                }else {
                    HashMap<String, Object> productMap = new HashMap<>();
                    productMap.put("productList", cartList);
                    productMap.put("order_id","HD" + user_id + randomID);
                    productMap.put("totalPrice", total_price.getText());
                    bill_reference.child(order_id).updateChildren(productMap);
                    user_reference.child(user_id).child("orders").child(order_id).updateChildren(productMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                                    intent.putExtra("order_id", order_id);
                                    intent.putExtra("order_time", date);
                                    intent.putExtra("randomID", "HD" + user_id + randomID);
                                    startActivity(intent);
                                }
                            });
                }
            }
        });

    }
}
