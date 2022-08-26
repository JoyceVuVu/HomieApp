package com.example.homieapp.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homieapp.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class EditProfile extends AppCompatActivity {
    private TextView full_name, username, email, address, phone_no;
    private Button edit;
    private ShapeableImageView user_image;
    private DatabaseReference user_reference;
    private StorageReference user_img_reference;
    private Uri uri;
    String id;
    String parentDB;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.editprofile_activity);
    }
}
