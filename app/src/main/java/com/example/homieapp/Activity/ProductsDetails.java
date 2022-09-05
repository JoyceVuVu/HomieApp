package com.example.homieapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homieapp.R;
import com.example.homieapp.model.Cart;
import com.example.homieapp.model.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProductsDetails extends AppCompatActivity {
    ImageView back, cart, plus, minus;
    Button add_to_cart;
    ImageView product_img;
    TextView product_name, product_price, product_description, number, product_price_discount;
    DatabaseReference product_reference, discount_reference, user_reference, cart_reference;
    StorageReference product_img_reference;
    String prod_id, discount_id,percent;
    int product_quantity;
    int total_number = 1;
    String fromPage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products_details_activity);

        prod_id = getIntent().getStringExtra("ID");
        fromPage = getIntent().getStringExtra("fromPage");
        Log.d("fromPage", fromPage);
        back = findViewById(R.id.product_detail_back);
        cart = findViewById(R.id.product_detail_cart);
        plus = findViewById(R.id.plus_btn);
        minus = findViewById(R.id.minus_btn);
        add_to_cart = findViewById(R.id.add_to_cart);
        number = findViewById(R.id.add_number);
        number.setText("1");
        product_price_discount = findViewById(R.id.product_detail_price_discount);

        product_img = findViewById(R.id.product_detail_img);
        product_name= findViewById(R.id.product_detail_name);
        product_price= findViewById(R.id.product_detail_price);
        product_description= findViewById(R.id.product_detail_description_area);

        product_reference = FirebaseDatabase.getInstance().getReference("products");
        product_img_reference = FirebaseStorage.getInstance().getReference("product images");
        discount_reference = FirebaseDatabase.getInstance().getReference("discounts");
//        user_reference = FirebaseDatabase.getInstance().getReference("users");
        user_reference = FirebaseDatabase.getInstance().getReference("users");
        back.setOnClickListener(view -> Back());
        cart.setOnClickListener(view -> startActivity(new Intent(ProductsDetails.this,CartActivity.class)));
        showData();
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (total_number < product_quantity){
                    total_number++;
                    number.setText(String.valueOf(total_number));
                }else{
                    Toast.makeText(getApplicationContext(), "It is more than our warehouse", Toast.LENGTH_SHORT).show();
                }
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (total_number > 0){
                    total_number--;
                    number.setText(String.valueOf(total_number));
                }
            }
        });
        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });
        cart.setOnClickListener(view -> startActivity(new Intent(ProductsDetails.this, CartActivity.class)));
    }

    private void addToCart() {
//        String _product_id = prod_id;
        String numberInCart = number.getText().toString().trim();
        //getCurrentUser
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersDetails = sessionManager.getUserDetailFromSession();
        String user_id = usersDetails.get(SessionManager.KEY_SESSIONPHONENO);

        //Check whether product is exist or not
        Query query = user_reference.child(user_id).child("carts").orderByChild(prod_id).equalTo(prod_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String quantity_orderFromDB = snapshot.child(prod_id).child("numberInCart").getValue(String.class);
                    int total_quantity =Integer.parseInt(quantity_orderFromDB) + Integer.parseInt(numberInCart);
                    if (total_quantity > product_quantity){
                        Toast.makeText(getApplicationContext(), "The amount you choose has reached a maximum of this product\n", Toast.LENGTH_SHORT).show();
                    }else {
                        snapshot.child(prod_id).child("numberInCart").getRef().setValue(String.valueOf(total_quantity))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "Product is added in your cart", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                }else {
                    product_reference.child(prod_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = snapshot.child("name").getValue(String.class);
                            String quantity = snapshot.child("quantity").getValue(String.class);
                            String category = snapshot.child("category").getValue(String.class);
                            String price = snapshot.child("price").getValue(String.class);
                            String image = snapshot.child("image").getValue(String.class);
                            String description = snapshot.child("description").getValue(String.class);
                            String discount = snapshot.child("discount").getValue(String.class);

                            Products products = new Products(prod_id, name, quantity, category,price, image, description, discount);
                            Cart cart = new Cart(products, numberInCart);
                            user_reference.child(user_id).child("carts").child(prod_id).setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ProductsDetails.this, "Product is added to cart", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(ProductsDetails.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showData() {
        product_reference.child(prod_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                product_name.setText(snapshot.child("name").getValue(String.class));
                String price = snapshot.child("price").getValue(String.class);
                product_price.setText(price + " VND");
                discount_id = snapshot.child("discount").getValue(String.class);
                discount_reference.child(discount_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        percent = snapshot.child("percent").getValue(String.class);
                        product_price_discount.setText((Integer.parseInt(price) - Integer.parseInt(price) * Float.parseFloat(percent)) + "VND");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                product_description.setText(snapshot.child("description").getValue(String.class));
                String url = snapshot.child("image").getValue(String.class);
                product_quantity = Integer.parseInt(snapshot.child("quantity").getValue(String.class));
                Picasso.with(getApplicationContext()).load(url).fit().centerCrop().into(product_img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Back();
    }
    public void Back(){
        if (fromPage == "com.example.homieapp.Activity.MainActivity@cb21aa"){
            startActivity(new Intent(ProductsDetails.this, MainActivity.class));
        }else if (fromPage == "com.example.homieapp.Activity.DashboảdProduct@cb21aa"){
            startActivity((new Intent(ProductsDetails.this, DashboardProduct.class)));
        }else {
            finish();
        }
    }
}
