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
import android.widget.TextView;
import android.widget.Toast;

import com.example.homieapp.Activity.AddCategoryActivity;
import com.example.homieapp.Activity.AddDiscount;
import com.example.homieapp.Activity.CategoryDetail;
import com.example.homieapp.Activity.DiscountDetail;
import com.example.homieapp.Activity.EditCategory;
import com.example.homieapp.Activity.EditDiscount;
import com.example.homieapp.R;
import com.example.homieapp.model.Discount;
import com.example.homieapp.model.ProductCategory;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private FloatingActionButton actionButton;
    DatabaseReference category_reference;
    String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = root.findViewById(R.id.category_fragment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        registerForContextMenu(recyclerView);

        actionButton = root.findViewById(R.id.category_fragment_fab);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddDiscount.class));
            }
        });
        category_reference = FirebaseDatabase.getInstance().getReference("categories");
        FirebaseRecyclerOptions<ProductCategory> options = new FirebaseRecyclerOptions.Builder<ProductCategory>()
                .setQuery(category_reference, ProductCategory.class)
                .build();
        FirebaseRecyclerAdapter<ProductCategory, CategoryFragment.CategoryFragmentViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductCategory, CategoryFragment.CategoryFragmentViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CategoryFragment.CategoryFragmentViewHolder holder, int position, @NonNull ProductCategory model) {
                        holder.id.setText(model.getName());
                        holder.percent.setText(model.getId());
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
                    public CategoryFragment.CategoryFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return new CategoryFragment.CategoryFragmentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false));
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        return root;
    }

    private class CategoryFragmentViewHolder extends RecyclerView.ViewHolder {
        TextView id, percent;
        public CategoryFragmentViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.fragment_discount_id);
            percent = itemView.findViewById(R.id.fragment_discount_percent);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_view:
                Intent intent = new Intent(getContext(), CategoryDetail.class);
                intent.putExtra("ID", id);
                startActivity(intent);
                return true;
            case R.id.menu_add:
                Intent intent1 = new Intent(getContext(), AddCategoryActivity.class);
                intent1.putExtra("ID", id);
                startActivity(intent1);
                return true;
            case R.id.menu_update:
                Intent intent3 = new Intent(getContext(), EditCategory.class);
                intent3.putExtra("ID", id);
                startActivity(intent3);
                return true;
            case R.id.menu_delete:
                category_reference.addValueEventListener(new ValueEventListener() {
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