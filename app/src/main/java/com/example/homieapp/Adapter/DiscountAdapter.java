package com.example.homieapp.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homieapp.Activity.DashboardDiscountActivity;
import com.example.homieapp.Activity.DiscountDetail;
import com.example.homieapp.R;
import com.example.homieapp.model.Discount;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class DiscountAdapter extends FirebaseRecyclerAdapter<Discount, DiscountAdapter.DiscountViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public DiscountAdapter(@NonNull FirebaseRecyclerOptions<Discount> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DiscountViewHolder holder, int position, @NonNull Discount model) {
//        Glide.with(holder.discount_img.getContext()).load(model.getImage()).into(holder.discount_img);
        Picasso.with(holder.discount_img.getContext()).load(model.getImage()).fit().centerCrop().into(holder.discount_img);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), DiscountDetail.class);
                intent.putExtra("ID", model.getId());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public DiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_row_item,parent,false);
        return new DiscountViewHolder(view);
    }

    public class DiscountViewHolder extends RecyclerView.ViewHolder {
        ImageView discount_img;
        public DiscountViewHolder(@NonNull View itemView) {
            super(itemView);
            discount_img = itemView.findViewById(R.id.discount_pic);
        }
    }
}
