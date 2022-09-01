package com.example.homieapp.Adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Activity.EditPaymentMethod;
import com.example.homieapp.Activity.SessionManager;
import com.example.homieapp.R;
import com.example.homieapp.model.PaymentMethod;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PaymentAdapter extends FirebaseRecyclerAdapter<PaymentMethod, PaymentAdapter.PaymentViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PaymentAdapter(@NonNull FirebaseRecyclerOptions<PaymentMethod> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PaymentViewHolder holder, int position, @NonNull PaymentMethod model) {
        holder.payment_radio_btn.setText(model.getAccount_number() + "-" +model.getBank());
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
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_row_item, parent, false);
        return new PaymentViewHolder(view);
    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder {
        public RadioButton payment_radio_btn;
        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            payment_radio_btn = itemView.findViewById(R.id.payment_method_item_radio_btn);
        }
    }
}
