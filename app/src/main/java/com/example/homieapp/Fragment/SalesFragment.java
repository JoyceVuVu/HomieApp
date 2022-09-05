package com.example.homieapp.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.homieapp.R;
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
import java.util.Calendar;

import kotlin.collections.unsigned.UArraysKt;

public class SalesFragment extends Fragment {

    Spinner year, month, day;
    RecyclerView recyclerView;
    TextView total;
    Button filter;
    DatabaseReference bill_reference;
    Calendar cal;
    int dayNow, monthNow;
    OrderAdapter adapter;
    int totalSales;
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
        total = root.findViewById(R.id.sale_total_value);
        filter = root.findViewById(R.id.sales_fragment_filter);
        cal = Calendar.getInstance();
        bill_reference = FirebaseDatabase.getInstance().getReference("bills");
        FirebaseRecyclerOptions<Order_bill> options = new FirebaseRecyclerOptions.Builder<Order_bill>()
                .setQuery(bill_reference, Order_bill.class)
                .build();
        adapter = new OrderAdapter(options);
        recyclerView.setAdapter(adapter);
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

    private void filterSales() {
        String day_spinner = day.getSelectedItem().toString().trim();
        String month_spinner = month.getSelectedItem().toString().trim();
        String year_spinner = year.getSelectedItem().toString().trim();
        String paths = "";
        if (year_spinner != "Year" && month_spinner != "Month" && day_spinner != "Day") {
            paths = year_spinner + "/" + month_spinner + "/" + day_spinner + "/";
        } else if (year_spinner != "Year" && month_spinner != "Month"){
            paths = year_spinner + "/" + month_spinner + "/";
        }else if (year_spinner != "Year"){
            paths = year_spinner + "/";
        }
        bill_reference.child(paths).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseRecyclerOptions<Order_bill> options = new FirebaseRecyclerOptions.Builder<Order_bill>()
                .setQuery(bill_reference.child(paths), Order_bill.class)
                .build();
        adapter = new OrderAdapter(options);
        recyclerView.setAdapter(adapter);
    }
    private void dayPicker() {
        String[] mValue= {"Day", "01" ,"02", "03","04","05","06","07","08","09","11","12","13","14","15","16","17","18","19","20",
                "21", "22", "23", "24","25","26","27","28","29","30","31"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,mValue);
        day.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }


    private void yearPicker() {
        int maxYear = cal.get(Calendar.YEAR);//2016
        final int minYear = 1999;//1997;
        int arraySize = maxYear - minYear;

        String[] tempArray = new String[arraySize];
        tempArray[0] = "Year";
        int tempYear = minYear;

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
        String[] mValue= {"Month", "01" ,"02", "03","04","05","06","07","08","09","11","12"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,mValue);
        month.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }
    private class OrderAdapter extends FirebaseRecyclerAdapter<Order_bill, OrderAdapter.OrderBillViewHolder> {
        /**
         * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
         * {@link FirebaseRecyclerOptions} for configuration options.
         *
         * @param options
         */
        public OrderAdapter(@NonNull FirebaseRecyclerOptions<Order_bill> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull OrderBillViewHolder holder, int position, @NonNull Order_bill model) {
            holder.bill_date.setText(model.getDate());
            holder.bill_id.setText(model.getId());
            holder.bill_totalPrice.setText(model.getTotal_price());
            totalSales = totalSales + Integer.parseInt(model.getTotal_price());
            total.setText(String.valueOf(totalSales));
        }

        @NonNull
        @Override
        public OrderBillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OrderBillViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_row, parent, false));
        }

        private class OrderBillViewHolder extends RecyclerView.ViewHolder {
            TextView bill_date, bill_id, bill_totalPrice;

            public OrderBillViewHolder(@NonNull View itemView) {
                super(itemView);
                bill_date = itemView.findViewById(R.id.bill_date);
                bill_id = itemView.findViewById(R.id.bill_id);
                bill_totalPrice = itemView.findViewById(R.id.bill_totalPrice);
            }
        }
    }

}