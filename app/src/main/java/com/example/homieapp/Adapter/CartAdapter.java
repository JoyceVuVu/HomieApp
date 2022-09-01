package com.example.homieapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Activity.ProductsDetails;
import com.example.homieapp.Activity.SessionManager;
import com.example.homieapp.R;
import com.example.homieapp.model.Cart;
import com.example.homieapp.model.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CartAdapter extends FirebaseRecyclerAdapter<Cart, CartAdapter.CartViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    DatabaseReference product_reference, user_reference, discount_reference;
    Context context;
    String discount_id, percent;
    public CartAdapter(@NonNull FirebaseRecyclerOptions<Cart> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position, @NonNull Cart model) {
        String product_id = model.getProduct_id();
        int number_order = Integer.parseInt(model.getQuantity_order());
        product_reference = FirebaseDatabase.getInstance().getReference("products");
        user_reference = FirebaseDatabase.getInstance().getReference("users");
        discount_reference = FirebaseDatabase.getInstance().getReference("discounts");
        //get Current user
        SessionManager sessionManager = new SessionManager(holder.itemView.getContext(),SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersDetails = sessionManager.getUserDetailFromSession();
        String user_id  = usersDetails.get(SessionManager.KEY_SESSIONPHONENO);

        product_reference.child(product_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String quantity =snapshot.child("quantity").getValue(String.class);
                String price = snapshot.child("price").getValue(String.class);
                discount_id = snapshot.child("discount").getValue(String.class);
                String image = snapshot.child("image").getValue(String.class);
                holder.product_name.setText(name);
                holder.product_price.setText(price + " VND");

                Picasso.with(holder.itemView.getContext()).load(image).fit().centerCrop().into(holder.product_img);
                discount_reference.child(discount_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        percent = snapshot.child("percent").getValue(String.class);
                        holder.product_price_discount.setText((Integer.parseInt(price) - Integer.parseInt(price) * Float.parseFloat(percent)) + "VND");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (number_order < Integer.parseInt(quantity)){
                            int total_number  = number_order + 1;
                            HashMap<String, Object> cartMap = new HashMap<>();
                            cartMap.put("numberInCart", total_number);
                            user_reference.child(user_id).child("carts").child(product_id).updateChildren(cartMap);
                            holder.number_order.setText(String.valueOf(total_number));
                        }
                    }
                });
                holder.minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (number_order > 1){
                            int total_number = number_order - 1;
                            HashMap<String, Object> cartMap = new HashMap<>();
                            cartMap.put("numberInCart", total_number);
                            user_reference.child(user_id).child("carts").child(product_id).updateChildren(cartMap);
                            holder.number_order.setText(String.valueOf(total_number));
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row_item,parent, false);
        return new CartAdapter.CartViewHolder(view);
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView product_img, bin, plus, minus;
        CheckBox checkBox;
        TextView product_name, product_price, product_price_discount, number_order;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            product_img = itemView.findViewById(R.id.cart_product_img);
            bin = itemView.findViewById(R.id.remove_item);
            plus = itemView.findViewById(R.id.cart_plus_btn);
            minus = itemView.findViewById(R.id.cart_minus_btn);
            checkBox = itemView.findViewById(R.id.cart_item_checkbox);
            product_name = itemView.findViewById(R.id.giohang_sp_name);
            product_price = itemView.findViewById(R.id.cart_product_price);
            product_price_discount = itemView.findViewById(R.id.prod_total);
            number_order = itemView.findViewById(R.id.cart_number_order);
        }
    }
}
