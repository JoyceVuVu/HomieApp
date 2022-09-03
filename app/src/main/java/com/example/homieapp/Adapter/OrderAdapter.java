package com.example.homieapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homieapp.Activity.CheckoutActivity;
import com.example.homieapp.R;
import com.example.homieapp.model.Cart;
import com.example.homieapp.model.Products;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    List<Cart> cartListItem;
    Context context;
    DatabaseReference discount_reference = FirebaseDatabase.getInstance().getReference("discounts");
    public OrderAdapter(List<Cart> cartListItem, Context context) {
        this.cartListItem = cartListItem;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.product_name.setText(cartListItem.get(position).getProducts().getName());
        holder.product_numberInCart.setText("x" + cartListItem.get(position).getNumberInCart());
        String discount = cartListItem.get(position).getProducts().getDiscount();
        String price = cartListItem.get(position).getProducts().getPrice();
        discount_reference.child(discount).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String percent = snapshot.child("percent").getValue(String.class);
                String discount_price = Integer.parseInt(price) - Integer.parseInt(price) * Float.parseFloat(percent) + " VND";
                holder.product_price.setText(discount_price);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Glide.with(holder.itemView.getContext()).load(cartListItem.get(position).getProducts().getImage()).into(holder.product_img);
    }

    @Override
    public int getItemCount() {
        return cartListItem.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public ImageView product_img;
        public TextView product_name, product_numberInCart, product_price;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            product_img = itemView.findViewById(R.id.order_product_img);
            product_name = itemView.findViewById(R.id.order_product_name);
            product_numberInCart = itemView.findViewById(R.id.order_number);
            product_price = itemView.findViewById(R.id.order_product_price);
        }
    }
}
