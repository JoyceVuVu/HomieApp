package com.example.homieapp.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homieapp.Activity.BillDetail;
import com.example.homieapp.Activity.EditPaymentMethod;
import com.example.homieapp.Activity.HistoryActivity;
import com.example.homieapp.Activity.ProductsDetails;
import com.example.homieapp.Activity.UpdateBill;
import com.example.homieapp.R;
import com.example.homieapp.model.Cart;
import com.example.homieapp.model.Order_bill;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
public class OrderBillAdapter extends RecyclerView.Adapter<OrderBillAdapter.OrderBillViewHolder> {
    List<Order_bill> order_billList;
    Context context;
    public OrderBillAdapter(List<Order_bill> order_billList, Context context) {
        this.order_billList = order_billList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderBillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderBillViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderBillViewHolder holder, int position) {
        holder.order_id.setText(order_billList.get(position).getOrder_id());
        holder.order_date.setText(order_billList.get(position).getDate());
        holder.order_totalPrice.setText(order_billList.get(position).getTotalPrice() + " VND");
        holder.status.setText(" - " + order_billList.get(position).getStatus());
        String order_id = order_billList.get(position).getOrder_id().trim();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), BillDetail.class);
                intent.putExtra("order_id", order_id);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(holder.itemView.getContext());
                    alertDialogBuilder.setMessage("Do you want to update status");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(holder.itemView.getContext(), UpdateBill.class);
                            intent.putExtra("order_id", order_id);
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });
                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }catch (Exception e){
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return order_billList.size();
    }

    public static class OrderBillViewHolder extends RecyclerView.ViewHolder {
        public TextView order_id, order_date, order_totalPrice, status;
        public OrderBillViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.bill_status);
            order_id = itemView.findViewById(R.id.bill_id);
            order_date = itemView.findViewById(R.id.bill_date);
            order_totalPrice = itemView.findViewById(R.id.bill_totalPrice);
        }
    }
}

