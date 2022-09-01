package com.example.homieapp.Activity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homieapp.R;
import com.example.homieapp.model.CheckInternet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    ImageView logo;
    TextInputLayout in_phone_no, in_password;
    Button signin_btn;
    TextView sign_up, forget_password;
    CheckBox remember_me;
    TextInputEditText phone_no_editText, password_editText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_activity);

        //Hooks
        logo = (ImageView) findViewById(R.id.login_logo);
        in_phone_no = (TextInputLayout) findViewById(R.id.login_phone_no);
        in_password = (TextInputLayout) findViewById(R.id.login_password);
        signin_btn = (Button) findViewById(R.id.btnSignin);
        sign_up = (TextView) findViewById(R.id.txtSignup);
        forget_password = (TextView) findViewById(R.id.resetpass);
        progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
        remember_me = (CheckBox) findViewById(R.id.login_remember_me);
        phone_no_editText = findViewById(R.id.login_phone_no_editText);
        password_editText = findViewById(R.id.login_password_editText);
        //check whether phone number and password is already saved in Shared preferences or not
        SessionManager sessionManager= new SessionManager(LoginActivity.this, SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()){
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
            phone_no_editText.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPHONENO));
            password_editText.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPASSWORD));
        }

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSignup = new Intent(LoginActivity.this, RegisterActivity.class);

                Pair[] pairs = new Pair[6];

                pairs[0] = new Pair<View,String>(logo,"logo_image");
                pairs[1] = new Pair<View,String>(in_phone_no,"username_tran");
                pairs[2] = new Pair<View,String>(in_password,"password_tran");
                pairs[3] = new Pair<View,String>(signin_btn,"login_btn_tran");
                pairs[4] = new Pair<View,String>(sign_up,"tv_signup_tran");
                pairs[5] = new Pair<View,String>(forget_password,"tv_reset_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(iSignup, options.toBundle());
            }
        });
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iSignup = new Intent(LoginActivity.this, ResetPassActivity.class);

                Pair[] pairs = new Pair[6];

                pairs[0] = new Pair<View,String>(logo,"logo_image");
                pairs[1] = new Pair<View,String>(in_phone_no,"username_tran");
                pairs[2] = new Pair<View,String>(in_password,"password_tran");
                pairs[3] = new Pair<View,String>(signin_btn,"login_btn_tran");
                pairs[4] = new Pair<View,String>(sign_up,"tv_signup_tran");
                pairs[5] = new Pair<View,String>(forget_password,"tv_reset_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(iSignup, options.toBundle());
            }
        });

        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckInternet checkInternet = new CheckInternet();
                if (!checkInternet.isConnected(LoginActivity.this)){
                    showCustomDialog();
                }else {
                    Login();
                }
            }
        });

    }


    private void Login() {
        
        if (!validatePhone() | !validatePass()){
            return;
        }else {
            isUser();
        }
    }

    private void isUser() {
        progressBar.setVisibility(View.VISIBLE);
        String phone_no = in_phone_no.getEditText().getText().toString().trim();
        if (phone_no.charAt(0) == '0'){
            phone_no = phone_no.substring(1);
        }
        String password = in_password.getEditText().getText().toString().trim();

        if(remember_me.isChecked()){
            SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_REMEMBERME);
            sessionManager.createRememberMeSession(phone_no, password);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser = reference.orderByChild("phone_no").equalTo(phone_no);
        String finalPhone_no = phone_no;
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    in_phone_no.setError(null);
                    in_phone_no.setErrorEnabled(false);
                    String passwordFromDB = dataSnapshot.child(finalPhone_no).child("pass").getValue(String.class);
                    if (passwordFromDB.equals(password)) {
                        in_phone_no.setError(null);
                        in_phone_no.setErrorEnabled(false);

                        String full_nameFromDB = dataSnapshot.child(finalPhone_no).child("full_name").getValue(String.class);
                        String usernameFromDB = dataSnapshot.child(finalPhone_no).child("username").getValue(String.class);
                        String phoneNoFromDB = dataSnapshot.child(finalPhone_no).child("phone_no").getValue(String.class);
                        String emailFromDB = dataSnapshot.child(finalPhone_no).child("email").getValue(String.class);
                        String addressFromDB = dataSnapshot.child(finalPhone_no).child("address").getValue(String.class);
                        String imageFromDB = dataSnapshot.child(finalPhone_no).child("image").getValue(String.class);
                        String adminFromDB = String.valueOf(dataSnapshot.child(finalPhone_no).child("admin").getValue(Boolean.class));
                        Log.d("phone_no: ", phoneNoFromDB );
                        SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_USERSESSION);
                        sessionManager.createLoginSession(full_nameFromDB, usernameFromDB, emailFromDB, phoneNoFromDB, passwordFromDB, addressFromDB, imageFromDB, adminFromDB);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    } else {
                        progressBar.setVisibility(View.GONE);
                        in_password.setError("Wrong Password");
                        in_password.requestFocus();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    in_phone_no.setError("No such User exist");
                    in_phone_no.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

            private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Please connect to the internet to proceed further")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), IntroActivity.class));
                        finish();
                    }
                });
    }

    private boolean validatePass() {
        String password = in_password.getEditText().getText().toString().trim();
        String passwordVal = "^"+
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=*])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";
        if (password.isEmpty()){
            in_password.setError("Password is required!");
            in_password.requestFocus();
            return false;
        }else if(!password.matches(passwordVal)){
            in_password.setError("Password is too weak");
            in_password.requestFocus();
            return false;
        }else {
            in_password.setError(null);
            in_password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePhone() {
        String phone_no = in_phone_no.getEditText().getText().toString().trim();
        if (phone_no.isEmpty()){
            in_phone_no.setError("Username is required!");
            in_phone_no.requestFocus();
            return false;
        }else {
            in_phone_no.setError(null);
            in_phone_no.setErrorEnabled(false);
            return true;
        }
    }

}
