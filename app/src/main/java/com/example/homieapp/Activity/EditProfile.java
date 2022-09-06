package com.example.homieapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.homieapp.R;
import com.example.homieapp.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {
    private TextInputLayout full_name, username, email, address, phone_no;
    private Button save;
    private ProgressBar progressBar;
    private TextView back;
    private ShapeableImageView user_image;
    private DatabaseReference user_reference;
    private StorageReference user_img_reference;
    private Uri uri;
    String user_id;
    private String downloadImageUrl;
    private static final int GalleryPick = 1;
    boolean isCLicked = false;
    private  String defaultImageUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.editprofile_activity);

        user_id = getIntent().getStringExtra("id");
//        user_id = "965645160";
        full_name = findViewById(R.id.edit_full_name);
        username = findViewById(R.id.edit_user_name);
        email = findViewById(R.id.edit_email);
        address = findViewById(R.id.edit_address);
        phone_no = findViewById(R.id.edit_phone_no);
        save = findViewById(R.id.edit_profile_btn);
        progressBar = findViewById(R.id.edit_progress);
        back = findViewById(R.id.edit_profile_back);
        user_image = findViewById(R.id.edit_ava);
        user_reference = FirebaseDatabase.getInstance().getReference("users");
        user_img_reference = FirebaseStorage.getInstance().getReference("user images");
        showAllUserData();
        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCLicked = true;
                OpenGallery();
            }
        });
        back.setOnClickListener(view -> finish());
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUserData();
            }
        });
    }
    private void showAllUserData() {
        user_reference.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                full_name.getEditText().setText(snapshot.child("full_name").getValue(String.class));
                username.getEditText().setText(snapshot.child("username").getValue(String.class));
                email.getEditText().setText(snapshot.child("email").getValue(String.class));
                phone_no.getEditText().setText("0"+ snapshot.child("phone_no").getValue(String.class));
                phone_no.setEnabled(false);
                address.getEditText().setText(snapshot.child("address").getValue(String.class));
                defaultImageUrl = snapshot.child("image").getValue(String.class);
                Glide.with(getApplicationContext()).load(defaultImageUrl).into(user_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void editUserData(){
        progressBar.setVisibility(View.VISIBLE);
        boolean isAdmin = false;
        String _username = username.getEditText().getText().toString().trim();
        String _full_name = full_name.getEditText().getText().toString().trim();
        String _email = email.getEditText().getText().toString().trim();
        String _address = address.getEditText().getText().toString().trim();
        if (_email.contains("@admin.com")){
            isAdmin = true;
        }
        if (isCLicked){
            final StorageReference oldPath = FirebaseStorage.getInstance().getReferenceFromUrl(defaultImageUrl);
            oldPath.delete();
            final StorageReference newPath = user_img_reference.child(uri.getLastPathSegment() + user_id + ".jpg");
            final UploadTask uploadTask = newPath.putFile(uri);

            boolean finalIsAdmin1 = isAdmin;
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
                            Toast.makeText(getApplicationContext(), "Your Image uploaded successfully...", Toast.LENGTH_SHORT).show();
                            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    downloadImageUrl = newPath.getDownloadUrl().toString();
                                    return newPath.getDownloadUrl();
                                }
                            })
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            downloadImageUrl = task.getResult().toString();
                                            Toast.makeText(EditProfile.this, "got your image url successfully ...", Toast.LENGTH_SHORT).show();
                                            saveDate(_full_name, _username, _email, _address, downloadImageUrl, finalIsAdmin1);
                                        }
                                    });
                        }
                    });
        }else {
            downloadImageUrl = defaultImageUrl;
            saveDate(_full_name, _username, _email, _address, downloadImageUrl, isAdmin);
        }
    }
    private void saveDate(String full_name, String username, String email, String address, String ava, boolean isAdmin){
        HashMap<String, Object> userMap = new HashMap<>();

        userMap.put("full_name", full_name);
        userMap.put("username", username);
        userMap.put("email", email);
        userMap.put("address", address);
        userMap.put("ava", ava);
        userMap.put("isAdmin", isAdmin);
        user_reference.child(user_id).updateChildren(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(EditProfile.this, ProfileActivity.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditProfile.this, "Your profile is edited successfully ...", Toast.LENGTH_SHORT).show();
                        }else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditProfile.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
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
