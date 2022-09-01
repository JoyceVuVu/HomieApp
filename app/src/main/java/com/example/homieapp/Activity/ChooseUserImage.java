package com.example.homieapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.homieapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChooseUserImage extends AppCompatActivity {
    private CircleImageView user_image;
    private Button save;
    private DatabaseReference user_reference;
    private StorageReference user_img_reference;
    private String user_id, full_name, username, email, phone_no, password, address,WhatToDo;
    private Uri uri;
    private static final int GalleryPick = 1;
    private String downloadImageUrl;
    private ProgressBar progressBar;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user_image);

        phone_no = getIntent().getStringExtra("phone_no");
//        phone_no = "965645160";
        user_image= findViewById(R.id.user_image);
        save = findViewById(R.id.user_image_save);
        progressBar = findViewById(R.id.add_user_img_progressbar);

        user_reference = FirebaseDatabase.getInstance().getReference("users");
        user_img_reference = FirebaseStorage.getInstance().getReference("user images");

        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserImage();
            }
        });
    }

    private void addUserImage() {
        progressBar.setVisibility(View.VISIBLE);
        if (!validateImage()){
            return;
        }else{
            uploadImage();
        }
    }
    private boolean validateImage() {
        if(uri == null){
            Toast.makeText(getApplicationContext(), "User image is mandatory ...", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void uploadImage() {
        progressBar.setVisibility(View.VISIBLE);

        final StorageReference filePath = user_img_reference.child(uri.getLastPathSegment() + phone_no + ".jpg");
        final UploadTask uploadTask = filePath.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "User Image uploaded successfully...", Toast.LENGTH_SHORT).show();
                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()){
                                    throw  task.getException();
                                }
                                downloadImageUrl = filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        })
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        downloadImageUrl = task.getResult().toString();
                                        Toast.makeText(getApplicationContext(),"got the user image url successfully ...", Toast.LENGTH_SHORT).show();

                                        HashMap<String, Object> userMap = new HashMap<>();
                                        userMap.put("phone_no", phone_no);
                                        userMap.put("image", downloadImageUrl);

                                        user_reference.child(phone_no).updateChildren(userMap).
                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Intent intent = new Intent(ChooseUserImage.this, LoginActivity.class);
                                                            startActivity(intent);
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(ChooseUserImage.this, "User is added successfully ...", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(ChooseUserImage.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null){
            uri = data.getData();
            user_image.setImageURI(uri);
        }
    }
}