package com.example.homieapp.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Activity.EditPaymentMethod;
import com.example.homieapp.R;
import com.example.homieapp.model.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

public class PayAdapter extends RecyclerView.Adapter <PayAdapter.PayViewHolder> {
    private int lastSelectedPosition = -1;
    private List<PaymentMethod> list = new ArrayList<PaymentMethod>();
    Context context;
    public PayAdapter(List<PaymentMethod> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public PayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_row_item, parent, false);
        return new PayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PayViewHolder holder, int position) {
        holder.payment_radio_btn.setText(list.get(position).getAccount_number());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(holder.itemView.getContext());
                    alertDialogBuilder.setMessage("Do you want to edit this method");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), EditPaymentMethod.class));
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
        holder.payment_radio_btn.setChecked(lastSelectedPosition == position);
    }
    public int getLastSelectedPosition() {
        return lastSelectedPosition;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PayViewHolder extends RecyclerView.ViewHolder {
        public RadioButton payment_radio_btn;
        public PayViewHolder(@NonNull View itemView) {
            super(itemView);
            payment_radio_btn = itemView.findViewById(R.id.payment_method_item_radio_btn);
            payment_radio_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }
}
