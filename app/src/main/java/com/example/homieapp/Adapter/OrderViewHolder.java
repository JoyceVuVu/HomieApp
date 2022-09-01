package com.example.homieapp.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {
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
