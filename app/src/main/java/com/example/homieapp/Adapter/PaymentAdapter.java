package com.example.homieapp.Adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Activity.EditPaymentMethod;
import com.example.homieapp.Activity.EditProduct;
import com.example.homieapp.Activity.ProductsDetails;
import com.example.homieapp.R;
import com.example.homieapp.model.PaymentMethod;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class PaymentAdapter extends FirebaseRecyclerAdapter<PaymentMethod, PaymentAdapter.PaymentViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private final int lastSelectedPosition = -1;
    private final int isChecked = 0;
    public PaymentAdapter(@NonNull FirebaseRecyclerOptions<PaymentMethod> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PaymentViewHolder holder, int position, @NonNull PaymentMethod model) {
       holder.name.setText(model.getBank());
       holder.account.setText(model.getAccount_number());
       holder.edit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (model.getAccount_number() != "Cash") {
                   Intent intent = new Intent(holder.itemView.getContext(), EditPaymentMethod.class);
                   intent.putExtra("account_number", model.getAccount_number());
                   holder.itemView.getContext().startActivity(intent);
               }
           }
       });
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
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
           }
       });
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PaymentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_row_item, parent, false));
    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView name, account;
        ImageView edit;
        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            account = itemView.findViewById(R.id.payment_row_item_account);
            name = itemView.findViewById(R.id.payment_row_item_name);
            edit = itemView.findViewById(R.id.payment_row_item_edit);

        }
    }
}
