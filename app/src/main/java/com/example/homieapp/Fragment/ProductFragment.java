package com.example.homieapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.homieapp.Activity.AddProductsActivity;
import com.example.homieapp.Activity.EditProduct;
import com.example.homieapp.Activity.ProductsDetails;
import com.example.homieapp.Adapter.OrderAdapter;
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
        registerForContextMenu(recyclerView);
        actionButton = root.findViewById(R.id.product_fragment_fab);
        product_reference = FirebaseDatabase.getInstance().getReference("products");
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(product_reference, Products.class)
                .build();
        FirebaseRecyclerAdapter<Products, OrderAdapter.OrderViewHolder> adapter = new FirebaseRecyclerAdapter<Products, OrderAdapter.OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position, @NonNull Products model) {
                holder.product_name.setText(model.getName());
                holder.product_price.setText(model.getPrice());
                holder.product_numberInCart.setText(model.getQuantity());
                Picasso.with(holder.itemView.getContext()).load(model.getImage()).fit().centerCrop().into(holder.product_img);
                id = model.getId();
                holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                        getActivity().getMenuInflater().inflate(R.menu.context_menu, contextMenu);
                    }
                });
            }

            @NonNull
            @Override
            public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new OrderAdapter.OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row_item, parent,false));
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        return root;
    }
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_view:
                Intent intent = new Intent(getContext(), ProductsDetails.class);
                intent.putExtra("ID", id);
                startActivity(intent);
                return true;
            case R.id.menu_add:
                Intent intent1 = new Intent(getContext(), AddProductsActivity.class);
                intent1.putExtra("ID", id);
                startActivity(intent1);
                return true;
            case R.id.menu_update:
                Intent intent3 = new Intent(getContext(), EditProduct.class);
                intent3.putExtra("ID", id);
                startActivity(intent3);
                return true;
            case R.id.menu_delete:
                product_reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
                        Toast.makeText(getContext(), "This item is deleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            default:
                return super.onContextItemSelected(item);
        }
    }

}