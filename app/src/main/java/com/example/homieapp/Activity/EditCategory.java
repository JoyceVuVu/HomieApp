package com.example.homieapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.homieapp.R;
import com.example.homieapp.model.ProductCategory;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;

public class EditCategory extends AppCompatActivity {
    private ImageView edit_cate_img;
    private TextInputEditText edit_cate_id, edit_cate_name;
    private TextView activity_title;
    private Button finish;
    private ProgressBar progressBar;
    private DatabaseReference reference;
    private StorageReference cate_img_reference;
    private static final int GalleryPick = 1;
    private String downloadImageUrl;
    private Uri uri;
    private String defaultImageUrl;
    String id;
    boolean isCLicked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_category);

        id = "ban";

        //hooks
        edit_cate_id = findViewById(R.id.add_cate_id_edit);
        edit_cate_name = findViewById(R.id.add_cate_name_edit);
        finish = findViewById(R.id.add_cate_btn);
        progressBar = findViewById(R.id.add_cate_progressbar);
        edit_cate_img = findViewById(R.id.add_cate_img);
        activity_title = findViewById(R.id.activity_category_tittle);
        activity_title.setText("Edit Category");

        reference = FirebaseDatabase.getInstance().getReference("categories");
        cate_img_reference = FirebaseStorage.getInstance().getReference("category images");

        edit_cate_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCLicked = true;
                OpenGallery();
            }
        });
        showData();

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });
    }

    private void updateData() {
        progressBar.setVisibility(View.VISIBLE);

        String _name = edit_cate_name.getText().toString().trim();

        if (isCLicked){
            final StorageReference oldPath = FirebaseStorage.getInstance().getReferenceFromUrl(defaultImageUrl);
            oldPath.delete();
            final StorageReference newPath = cate_img_reference.child(uri.getLastPathSegment() + id + ".jpg");
            final UploadTask uploadTask = newPath.putFile(uri);
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
                            Toast.makeText(getApplicationContext(), "Category Image uploaded successfully...", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(EditCategory.this, "got the category image url successfully ...", Toast.LENGTH_SHORT).show();
                                            saveDate(id,_name, downloadImageUrl);
                                        }
                                    });
                        }
                    });
        }else {
            downloadImageUrl = defaultImageUrl;
            saveDate(id,_name, downloadImageUrl);
        }



    }
    private void saveDate(String id,String name, String url){
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("id", id);
        productMap.put("name", name);
        productMap.put("image", url);
        reference.child(id).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(EditCategory.this, DashboardProduct.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditCategory.this, "Category is edited successfully ...", Toast.LENGTH_SHORT).show();
                        }else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditCategory.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void showData() {
        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                edit_cate_id.setText(id);
                edit_cate_id.setEnabled(false);
                edit_cate_name.setText(snapshot.child("name").getValue(String.class));
                defaultImageUrl = snapshot.child("image").getValue(String.class);
                Glide.with(getApplicationContext()).load(defaultImageUrl).into(edit_cate_img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data !=null){
            uri = data.getData();
            edit_cate_img.setImageURI(uri);
        }
    }
}
