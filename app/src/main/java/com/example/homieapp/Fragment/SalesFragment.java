package com.example.homieapp.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.homieapp.Activity.HistoryActivity;
import com.example.homieapp.Activity.MonthYearPickerDialog;
import com.example.homieapp.Adapter.OrderAdapter;
import com.example.homieapp.Adapter.OrderBillAdapter;
import com.example.homieapp.R;
import com.example.homieapp.model.Cart;
import com.example.homieapp.model.Order_bill;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.internal.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kotlin.collections.unsigned.UArraysKt;

public class SalesFragment extends Fragment {

    Spinner year, month, day;
    RecyclerView recyclerView;
    TextView total;
    Button filter;
    DatabaseReference bill_reference;
    Calendar cal;
    int dayNow, monthNow;
    OrderBillAdapter adapter;
    List<Order_bill> order_billList = new ArrayList<>();
    String order_date, paths;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sales, container, false);
        year = root.findViewById(R.id.year);
        month = root.findViewById(R.id.month);
        day = root.findViewById(R.id.day);
        recyclerView = root.findViewById(R.id.sales_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        total = root.findViewById(R.id.sale_total_value);
        filter = root.findViewById(R.id.sales_fragment_filter);
        cal = Calendar.getInstance();
        bill_reference = FirebaseDatabase.getInstance().getReference("bills");
        showAll();
        dayPicker();
        monthPicker();
        yearPicker();
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterSales();
            }
        });

        return root;

    }
    private void showAll(){
        bill_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String price = "";
                int totalSales = 0;
                String date;
                order_billList.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Order_bill order_bill = ds.getValue(Order_bill.class);
                    Log.d("orderbill", order_bill.getOrder_id());
                    order_billList.add(order_bill);
                    price = ds.child("totalPrice").getValue(String.class).trim();
                    totalSales = totalSales + Integer.parseInt(price);
//                    adapter = new OrderBillAdapter(order_billList, getContext());
//                    recyclerView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
                }
                adapter = new OrderBillAdapter(order_billList, getContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                total.setText(totalSales + " VND");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterSales() {
        String day_spinner = day.getSelectedItem().toString().trim();
        String month_spinner = month.getSelectedItem().toString().trim();
        String year_spinner = year.getSelectedItem().toString().trim();
        if (year_spinner != "Year" && month_spinner != "Month" && day_spinner != "Day") {
            paths = year_spinner + "/" + month_spinner + "/" + day_spinner + "/";
        } else if (year_spinner != "Year" && month_spinner != "Month"){
            paths = year_spinner + "/" + month_spinner + "/";
        }else if (year_spinner != "Year") {
            paths = year_spinner + "/";
        }else {
            showAll();
        }

        bill_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String price = "";
                int totalSales = 0;
                String date;
                order_billList.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                     date = ds.child("date").getValue(String.class).trim();
                    price = ds.child("totalPrice").getValue(String.class).trim();
                    Order_bill order_bill = ds.getValue(Order_bill.class);
                    if (date.contains(paths)) {
                        totalSales = totalSales + Integer.parseInt(price);
                        order_billList.add(order_bill);
                    }

                }
                total.setText(totalSales + " VND");
                adapter = new OrderBillAdapter(order_billList, getContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void dayPicker() {
        dayNow = cal.get(Calendar.DAY_OF_MONTH);
        Log.d("dayNow", String.valueOf(dayNow));
        List<String> list  = new ArrayList<>();
        list.add(0, "Day");
        for (int i = 1; i <32 ; i++) {
            String s;
            if (i < 10){
                s = "0" + i;
                list.add(i, s);
            }else list.add(i,String.valueOf(i));
        }
        setSpinnerDataFromDB(list, getContext(),day, String.valueOf(monthNow));
    }


    private void yearPicker() {
        int maxYear = cal.get(Calendar.YEAR);//2016
        final int minYear = 1999;//1997;
        int arraySize = maxYear - minYear;

        String[] tempArray = new String[arraySize];
        tempArray[0] = "Year";
        int tempYear = minYear + 1;

        for(int i=0 ; i < arraySize; i++){
            if(i != 0){
                tempArray[i] = " " + tempYear + "";
            }
            tempYear++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,tempArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(adapter);
    }
    private void monthPicker(){
        monthNow = cal.get(Calendar.MONTH);
        Log.d("monthNow", String.valueOf(monthNow));
        List<String> list  = new ArrayList<>();
        list.add(0, "Month");
        for (int i = 1; i <13 ; i++) {
            String s;
            if (i < 10){
                s = "0" + i;
                list.add(i, s);
            }else list.add(i,String.valueOf(i));
        }
        setSpinnerDataFromDB(list, getContext(),month, String.valueOf(monthNow));

    }
    public void setSpinnerDataFromDB(List<String> list, final Context context, final Spinner spinner, String s) {

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(s)){
                        spinner.setSelection(i);
                        Log.d("position", String.valueOf(i));
                    }
                }

    }

}