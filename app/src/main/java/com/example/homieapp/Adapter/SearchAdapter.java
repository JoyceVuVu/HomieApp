package com.example.homieapp.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homieapp.Activity.ProductsDetails;
import com.example.homieapp.R;
import com.example.homieapp.model.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class SearchAdapter extends FirebaseRecyclerAdapter<Products, SearchAdapter.ProductSearchViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public SearchAdapter(@NonNull FirebaseRecyclerOptions<Products> options) {
        super(options);
    }

    protected void onBindViewHolder(@NonNull ProductSearchViewHolder holder, int position, @NonNull Products model) {
        String id = model.getId();
        holder.product_name.setText(model.getName());
        holder.product_price.setText(model.getPrice());
        holder.product_numberInCart.setText(model.getQuantity());
        Glide.with(holder.itemView.getContext()).load(model.getImage()).into(holder.product_img);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ProductsDetails.class);
                intent.putExtra("ID", id);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ProductSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row_item, parent, false);
        return new ProductSearchViewHolder(view);
    }


    public class ProductSearchViewHolder extends RecyclerView.ViewHolder{
        public ImageView product_img;
        public TextView product_name, product_numberInCart, product_price;
        public ProductSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            product_img = itemView.findViewById(R.id.order_product_img);
            product_name = itemView.findViewById(R.id.order_product_name);
            product_numberInCart = itemView.findViewById(R.id.order_number);
            product_price = itemView.findViewById(R.id.order_product_price);
        }
    }
}

