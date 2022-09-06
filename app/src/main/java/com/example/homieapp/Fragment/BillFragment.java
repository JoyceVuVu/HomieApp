package com.example.homieapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.homieapp.Adapter.OrderBillAdapter;
import com.example.homieapp.R;
import com.example.homieapp.model.Order_bill;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class BillFragment extends Fragment {
    RadioButton confirmed, shipping, done,all;
    RecyclerView recyclerView;
    TextView total;
    DatabaseReference bill_reference;
    List<Order_bill> order_billList = new ArrayList<>();
    OrderBillAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_bill, container, false);
        confirmed = root.findViewById(R.id.bill_confirm);
        shipping = root.findViewById(R.id.bill_shipping);
        done = root.findViewById(R.id.bill_done);
        all= root.findViewById(R.id.bill_all);
        recyclerView = root.findViewById(R.id.bill_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        total = root.findViewById(R.id.bill_total_value);
        bill_reference = FirebaseDatabase.getInstance().getReference("bills");
        bill_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                order_billList.clear();
                for (DataSnapshot ds :snapshot.getChildren() ){
                    Order_bill order_bill = ds.getValue(Order_bill.class);
                    order_billList.add(order_bill);
                }
                adapter = new OrderBillAdapter(order_billList, getContext());
                recyclerView.setAdapter(adapter);
                total.setText(String.valueOf(order_billList.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bill_reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        order_billList.clear();
                        for (DataSnapshot ds :snapshot.getChildren() ){
                            Order_bill order_bill = ds.getValue(Order_bill.class);
                            order_billList.add(order_bill);
                        }
                        adapter = new OrderBillAdapter(order_billList, getContext());
                        recyclerView.setAdapter(adapter);
                        total.setText(String.valueOf(order_billList.size()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        confirmed.setOnClickListener(view -> showData("Confirmed"));
        shipping.setOnClickListener(view -> showData("Shipping"));
        done.setOnClickListener(view -> showData("Done"));
        return root;
    }
    private void showData(String s){
        bill_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                order_billList.clear();
                for (DataSnapshot ds :snapshot.getChildren() ){
                    Order_bill order_bill = ds.getValue(Order_bill.class);
                    String status = ds.child("status").getValue(String.class);
                    if (status.equals(s)){
                        order_billList.add(order_bill);
                    }
                }
                adapter = new OrderBillAdapter(order_billList, getContext());
                recyclerView.setAdapter(adapter);
                total.setText(String.valueOf(order_billList.size()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}