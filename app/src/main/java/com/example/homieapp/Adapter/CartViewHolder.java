package com.example.homieapp.Adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.R;

public class CartViewHolder extends RecyclerView.ViewHolder {
    public ImageView product_img, bin, plus, minus;
    public CheckBox checkBox;
    public TextView product_name;
    public TextView product_price;
    public TextView product_price_discount;
    public TextView number_order;
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
