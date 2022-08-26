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

public class ProfileActivity extends AppCompatActivity {
    private TextView full_name, username, email, address, phone_no;
    private Button edit;
    private ShapeableImageView user_image;
    private DatabaseReference user_reference;
    private StorageReference user_img_reference;
    String id;
    String parentDB;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        //Hooks
        full_name = (TextView) findViewById(R.id.profile_full_name);
        username = (TextView) findViewById(R.id.profile_username);
        email = (TextView) findViewById(R.id.profile_email);
        address = (TextView) findViewById(R.id.profile_address);
        phone_no = (TextView) findViewById(R.id.profile_phone_n0);
        edit = findViewById(R.id.profile_edit_btn);
        id = "965645160";
        parentDB = "admins";
        user_reference = FirebaseDatabase.getInstance().getReference("users");
        user_img_reference = FirebaseStorage.getInstance().getReference("user images");

        showAllUserData();

        user_image = findViewById(R.id.ava);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfile.class);
                intent.putExtra("id",id);
                intent.putExtra("parentDB",parentDB);
                startActivity(intent);
            }
        });

    }

    private void showAllUserData() {
        user_reference.child(parentDB).child(id).addValueEventListener(new ValueEventListener() {
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
}
