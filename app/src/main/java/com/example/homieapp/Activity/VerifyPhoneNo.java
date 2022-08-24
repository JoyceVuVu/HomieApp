package com.example.homieapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homieapp.R;
import com.example.homieapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneNo extends AppCompatActivity {

    TextInputLayout enter_otp;
    Button verify_btn;
    ProgressBar progressBar;
    Animation animation;
    ImageView logo;
    FirebaseAuth mAuth;
    String codeBySystem,full_name, username, email, phone_no, password, address,WhatToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_phone_no);

        //Hooks
        logo = findViewById(R.id.verify_logo);
        animation = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        logo.setAnimation(animation);

        mAuth = FirebaseAuth.getInstance();

        verify_btn = findViewById(R.id.verify_btn);
        enter_otp = findViewById(R.id.enter_otp);
        progressBar = findViewById(R.id.progress_bar);

        //get all the data from Intent
        WhatToDo = getIntent().getStringExtra("WhatToDo");
        phone_no = getIntent().getStringExtra("phone_no");
        if (phone_no.charAt(0) == '0') {
            phone_no = phone_no.substring(1);
        }
        full_name = getIntent().getStringExtra("full_name");
        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        address = getIntent().getStringExtra("address");

        progressBar.setVisibility(View.GONE);
        sendVerificationCodeToUser(phone_no);

        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = enter_otp.getEditText().getText().toString();
                if (code.isEmpty() || code.length() < 6){
                    enter_otp.setError("Wrong OTP ...");
                    enter_otp.requestFocus();
                    return;
                }
                progressBar.setProgress(View.VISIBLE);
                verifyCode(code);
            }
        });
    }
    private void sendVerificationCodeToUser(String phone_no){

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+84" + phone_no)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            Toast.makeText(VerifyPhoneNo.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneNo.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            if (WhatToDo.equals("registerUser")){
                                storeNewUserData();
                                Toast.makeText(VerifyPhoneNo.this, "Your account has been created successfully!", Toast.LENGTH_SHORT).show();
                            }else if (WhatToDo.equals("updatePass")) {
                                updateOlderUsersData();
                                Toast.makeText(VerifyPhoneNo.this, "Your account has been updated successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(VerifyPhoneNo.this, "Verification Not Completed! Try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void updateOlderUsersData() {
        Intent intent = new Intent(getApplicationContext(),SetNewPassword.class);
        intent.putExtra("phone_no",phone_no);
        startActivity(intent);
        finish();
    }

    private void storeNewUserData() {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("users");

        phone_no = phone_no;
        User user = new User(full_name,username,email,phone_no,password,address);

        if(email.contains("@admin.com")){
            reference.child("admins").child(phone_no).setValue(user);
        }else {
            reference.child("customers").child(phone_no).setValue(user);
        }

//        reference.child(phone_no).setValue(user);

        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }
}