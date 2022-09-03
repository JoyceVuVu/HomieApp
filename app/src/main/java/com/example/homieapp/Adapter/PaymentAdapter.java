package com.example.homieapp.Adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Activity.EditPaymentMethod;
import com.example.homieapp.R;
import com.example.homieapp.model.PaymentMethod;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PaymentAdapter extends FirebaseRecyclerAdapter<PaymentMethod, PaymentAdapter.PaymentViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private  int lastSelectedPosition = -1;
    private int isChecked = 0;
    public PaymentAdapter(@NonNull FirebaseRecyclerOptions<PaymentMethod> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PaymentViewHolder holder, int position, @NonNull PaymentMethod model) {
        holder.radioButton.setText(model.getAccount_number());
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
        holder.radioButton.setChecked(lastSelectedPosition == position);
        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
        if (lastSelectedPosition == position){
            isChecked ++;
            model.setChecked(true);
        }
        model.setChecked(isChecked % 2 == 1);
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PaymentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_row_item, parent, false));
    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;
        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.payment_method_item_radio_btn);

        }
    }
}
