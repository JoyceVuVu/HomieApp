package com.example.homieapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.homieapp.R;
import com.example.homieapp.model.PaymentMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditPaymentMethod extends AppCompatActivity {
    private TextInputLayout name, number, UserName, date, CVV;
    private Button save_btn;
    private ProgressBar progressBar;
    private RadioButton cash_rad_btn, baking_rad_btn;
    DatabaseReference payment_reference;
    String account;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);
        name = findViewById(R.id.payment_bank_name);
        number = findViewById(R.id.payment_bank_number);
        UserName = findViewById(R.id.payment_cardholder_name);
        date = findViewById(R.id.payment_expire_date);
        CVV = findViewById(R.id.payment_CVV);
        save_btn = findViewById(R.id.payment_save);
        progressBar = findViewById(R.id.payment_progress_bar);
        cash_rad_btn = findViewById(R.id.cash_radio_btn);
        baking_rad_btn = findViewById(R.id.banking_radio_btn);

        account = getIntent().getStringExtra("account_number");
//        account = "19035061048013";

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        user_id = userDetails.get(SessionManager.KEY_SESSIONPHONENO);

        payment_reference = FirebaseDatabase.getInstance().getReference("users").child(user_id).child("payment_method");
        showData();
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

    }
    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.cash_radio_btn:
                if (checked) {
                    name.setEnabled(false);
                    number.setEnabled(false);
                    UserName.setEnabled(false);
                    date.setEnabled(false);
                    CVV.setEnabled(false);
                }
                break;
            case R.id.banking_radio_btn:
                if (checked){
                    name.setEnabled(true);
                    number.setEnabled(true);
                    UserName.setEnabled(true);
                    date.setEnabled(true);
                    CVV.setEnabled(true);
                }
                break;
        }
    }

    private void update() {
        if (cash_rad_btn.isChecked()){
            String account_number = "Cash";
            PaymentMethod paymentMethod = new PaymentMethod(account_number);
            payment_reference.child(account_number).setValue(paymentMethod).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "A new method is added successfully...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        }else if(baking_rad_btn.isChecked()) {
            progressBar.setVisibility(View.VISIBLE);
            String bank = name.getEditText().getText().toString().trim();
            String account = number.getEditText().getText().toString().trim();
            String cardholder_name = UserName.getEditText().getText().toString().trim();
            String exp_date = date.getEditText().getText().toString().trim();
            String cvv = CVV.getEditText().getText().toString().trim();

            if (!validateBank(bank) | !validateAccount(account) | !validateName(cardholder_name) | !validateDate(exp_date) | !validateCVV(cvv)) {
                return;
            }
            HashMap<String, Object> payMap = new HashMap<>();
            payMap.put("bank", bank);
            payMap.put("account_number", account);
            payMap.put("cardholder_name", cardholder_name);
            payMap.put("ex_date", exp_date);
            payMap.put("cvv", cvv);
            payment_reference.child(account).updateChildren(payMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "A new method is updated successfully...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private void showData() {
        payment_reference.orderByChild("account_number").equalTo(account).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.getEditText().setText(snapshot.child(account).child("bank").getValue(String.class));
                number.getEditText().setText(snapshot.child(account).child("account_number").getValue(String.class));
                UserName.getEditText().setText(snapshot.child(account).child("cardholder_name").getValue(String.class));
                CVV.getEditText().setText(snapshot.child(account).child("cvv").getValue(String.class));
                date.getEditText().setText(snapshot.child(account).child("ex_date").getValue(String.class));
                String s = snapshot.child(account).child("account_number").getValue(String.class);

                if (s != "Cash"){
                    baking_rad_btn.setSelected(true);
                }else cash_rad_btn.setSelected(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private boolean validateCVV(String s) {
        if (s.isEmpty()){
            CVV.setError("Phone no is required!");
            CVV.requestFocus();
            return false;
        } else {
            CVV.setError(null);
            CVV.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateDate(String s) {
        if (s.isEmpty()){
            date.setError("Phone no is required!");
            date.requestFocus();
            return false;
        } else {
            date.setError(null);
            date.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateName(String s) {
        if (s.isEmpty()){
            UserName.setError("Phone no is required!");
            UserName.requestFocus();
            return false;
        } else {
            UserName.setError(null);
            UserName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateAccount(String s) {
        if (s.isEmpty()) {
            number.setError("Phone no is required!");
            number.requestFocus();
            return false;
        } else {
            number.setError(null);
            number.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateBank(String s) {
        if (s.isEmpty()){
            name.setError("Phone no is required!");
            name.requestFocus();
            return false;
        } else {
            name.setError(null);
            name.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}