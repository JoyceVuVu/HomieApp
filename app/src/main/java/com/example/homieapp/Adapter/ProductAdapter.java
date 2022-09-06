package com.example.homieapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homieapp.Activity.ProductsDetails;
import com.example.homieapp.Activity.SessionManager;
import com.example.homieapp.R;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductAdapter extends FirebaseRecyclerAdapter<Products, ProductAdapter.ProductViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Context context;
    List<Products> productsList = new ArrayList<>();
    DatabaseReference product_reference, product_favourite_reference;
    boolean isFavourite = false;
    int isChecked = 0;
    public ProductAdapter(@NonNull FirebaseRecyclerOptions<Products> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
        product_reference = FirebaseDatabase.getInstance().getReference("products");
        product_favourite_reference = FirebaseDatabase.getInstance().getReference("users");
        String id = model.getId();
        holder.product_name.setText(model.getName());
        holder.product_price.setText(model.getPrice() + "VND");
        Picasso.with(holder.product_img.getContext()).load(model.getImage()).fit().centerCrop().into(holder.product_img);
        holder.product_price_discount.setText("Discount " + model.getDiscount());

        SessionManager sessionManager = new SessionManager(holder.itemView.getContext(),SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersDetails = sessionManager.getUserDetailFromSession();
        String user_id = usersDetails.get(SessionManager.KEY_SESSIONPHONENO);
        product_favourite_reference.child(user_id).child("favourite").orderByChild("id").equalTo(model.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isFavourite = snapshot.exists();
                if (isFavourite){

                    holder.heart.setButtonDrawable(R.drawable.ic_heart_filled);
                }else {
                    holder.heart.setButtonDrawable(R.drawable.ic_heart);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavourite) {
                    product_favourite_reference.child(user_id).child("favourite").child(model.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            holder.heart.setButtonDrawable(R.drawable.ic_heart);
                            Toast.makeText(context, "Product is removed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    product_favourite_reference.child(user_id).child("favourite").child(model.getId()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            holder.heart.setButtonDrawable(R.drawable.ic_heart_filled);
                            Toast.makeText(context, "Product is added", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ProductsDetails.class);
                intent.putExtra("ID", id);
                intent.putExtra("fromPage", holder.itemView.getContext().toString());
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_row_item,parent, false);
        return new ProductViewHolder(view);
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView product_name, product_price, product_price_discount;
        ImageView product_img;
        CheckBox heart;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            product_img = itemView.findViewById(R.id.product_img);
            product_price_discount = itemView.findViewById(R.id.product_price_discount);
            heart = itemView.findViewById(R.id.heart);
        }
    }
}
