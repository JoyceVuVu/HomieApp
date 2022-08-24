package com.example.homieapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homieapp.R;
import com.example.homieapp.model.ProductCategory;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class CategoryAdapter extends FirebaseRecyclerAdapter<ProductCategory, CategoryAdapter.ViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Context context;
    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<ProductCategory> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ProductCategory model) {
        holder.cate_name.setText(model.getProcateName());
        Glide.with(context).load(model.getProcateUrl()).into(holder.cate_pic);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_row_item,parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cate_name;
        ImageView cate_pic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cate_name = itemView.findViewById(R.id.cate_name);
            cate_pic = itemView.findViewById(R.id.cate_pic);
        }
    }
}
