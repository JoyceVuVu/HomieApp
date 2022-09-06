package com.example.homieapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telecom.Call;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.homieapp.Activity.AddProductsActivity;
import com.example.homieapp.Activity.EditProduct;
import com.example.homieapp.Activity.ProductsDetails;
import com.example.homieapp.Adapter.OrderAdapter;
import com.example.homieapp.Adapter.OrderBillAdapter;
import com.example.homieapp.R;
import com.example.homieapp.model.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment {
    RecyclerView recyclerView;
    FloatingActionButton actionButton;
    DatabaseReference product_reference;
    String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_add_product, container, false);
        recyclerView = root.findViewById(R.id.product_fragment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        actionButton = root.findViewById(R.id.product_fragment_fab);
        product_reference = FirebaseDatabase.getInstance().getReference("products");
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(product_reference, Products.class)
                .build();
        FirebaseRecyclerAdapter<Products, OrderAdapter.OrderViewHolder> adapter = new FirebaseRecyclerAdapter<Products, OrderAdapter.OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position, @NonNull Products model) {
                holder.product_name.setText(model.getName());
                holder.product_numberInCart.setText(model.getQuantity());
                holder.product_price.setText(model.getPrice());
                Glide.with(holder.itemView.getContext()).load(model.getImage()).into(holder.product_img);
                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu menu = new PopupMenu(getContext(), holder.more);
                        menu.inflate(R.menu.context_menu);
                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch ((menuItem.getItemId())) {
                                    case R.id.menu_view:
                                        Intent intent = new Intent(getContext(), ProductsDetails.class);
                                        intent.putExtra("ID", model.getId());
                                        startActivity(intent);
                                        return true;
                                    case R.id.menu_update:
                                        Intent intent1 = new Intent(getContext(), EditProduct.class);
                                        intent1.putExtra("ID", model.getId());
                                        startActivity(intent1);
                                        return true;
                                    case R.id.menu_delete:
                                        product_reference.child(model.getId()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                snapshot.getRef().removeValue();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        menu.show();
                    }
                });
            }


            @NonNull
            @Override
            public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new OrderAdapter.OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row_item, parent, false));
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        actionButton.setOnClickListener(view -> startActivity(new Intent(getContext(), AddProductsActivity.class)));
        return root;
    }


}