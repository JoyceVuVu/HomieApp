package com.example.homieapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.homieapp.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private TextView full_name, username, email, address, phone_no, order_status, payment;
    private Button edit;
    private ImageView back;
    private ShapeableImageView user_image;
    private DatabaseReference user_reference;
    private StorageReference user_img_reference;
    String user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        user_id = userDetails.get(SessionManager.KEY_SESSIONPHONENO);

        //Hooks
        full_name = findViewById(R.id.profile_full_name);
        username = findViewById(R.id.profile_username);
        email = findViewById(R.id.profile_email);
        address = findViewById(R.id.profile_address);
        phone_no = findViewById(R.id.profile_phone_n0);
        edit = findViewById(R.id.profile_edit_btn);
        user_image = findViewById(R.id.ava);
        back = findViewById(R.id.profile_back);
        order_status = findViewById(R.id.order_status);
        payment = findViewById(R.id.profile_payment);
        back.setOnClickListener(view -> startActivity(new Intent(ProfileActivity.this, MainActivity.class)));

        user_reference = FirebaseDatabase.getInstance().getReference("users");
        user_img_reference = FirebaseStorage.getInstance().getReference("user images");

        showAllUserData();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfile.class);
                intent.putExtra("id",user_id);
                startActivity(intent);
            }
        });
        payment.setOnClickListener(view -> startActivity(new Intent(ProfileActivity.this, PaymentActivity.class)));
        order_status.setOnClickListener(view -> startActivity(new Intent(ProfileActivity.this, HistoryActivity.class)));

    }

    private void showAllUserData() {
        user_reference.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                full_name.setText(snapshot.child("full_name").getValue(String.class));
                username.setText(snapshot.child("username").getValue(String.class));
                email.setText(snapshot.child("email").getValue(String.class));
                phone_no.setText("0"+ snapshot.child("phone_no").getValue(String.class));
                address.setText(snapshot.child("address").getValue(String.class));
                String image_url = snapshot.child("image").getValue(String.class);
                Glide.with(getApplicationContext()).load(image_url).into(user_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
    }
}
