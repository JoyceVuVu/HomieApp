package com.example.homieapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.ULocale;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homieapp.R;
import com.example.homieapp.model.ProductCategory;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Locale;

public class AddCategoryActivity extends AppCompatActivity {
   private TextInputLayout in_cate_id, in_cate_name;
   private ImageView in_cate_img;
   private Button finish;
   private ProgressBar progressBar;
   private Uri uri;
   private DatabaseReference cate_reference;
   private StorageReference cate_img_reference;
   private static final int GalleryPick = 1;
   private String downloadImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        //hooks
        in_cate_id = (TextInputLayout) findViewById(R.id.add_cate_id);
        in_cate_name = (TextInputLayout) findViewById(R.id.add_cate_name);
        in_cate_img = (ImageView) findViewById(R.id.add_cate_img);
        finish = (Button) findViewById(R.id.add_cate_btn);
        progressBar = (ProgressBar) findViewById(R.id.add_cate_progressbar);

        cate_img_reference = FirebaseStorage.getInstance().getReference("category images");
        cate_reference = FirebaseDatabase.getInstance().getReference("categories");


        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory();
            }
        });
        in_cate_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }




    private void addCategory() {
        if (!validateID() | !validateName() |!validateImage()){
            return;
        }else {
            addItem();
        }
    }


    private void addItem() {
        progressBar.setVisibility(View.VISIBLE);
        String name = in_cate_name.getEditText().getText().toString().trim();
        String id = in_cate_id.getEditText().getText().toString().trim();

        final StorageReference filePath = cate_img_reference.child(uri.getLastPathSegment() + id + ".jpg");
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
                        Toast.makeText(getApplicationContext(), "Category Image uploaded successfully...", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(getApplicationContext(),"got the category image url successfully ...", Toast.LENGTH_SHORT).show();

                                        HashMap<String, Object> categoryMap = new HashMap<>();
                                        categoryMap.put("ID", id);
                                        categoryMap.put("Name", name);
                                        categoryMap.put("Image", downloadImageUrl);
                                        cate_reference.child(id).updateChildren(categoryMap).
                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Intent intent = new Intent(AddCategoryActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(AddCategoryActivity.this, "Category is added successfully ...", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(AddCategoryActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                    }
                                });
                    }
                });
    }
    private boolean validateImage() {
        if(uri == null){
            Toast.makeText(getApplicationContext(), "Category image is mandatory ...", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateName() {
        String name = in_cate_name.getEditText().getText().toString().trim();
        if (name.isEmpty()){
            in_cate_name.setError("Name is required!");
            in_cate_name.requestFocus();
            return false;
        }else {
            in_cate_name.setError(null);
            in_cate_name.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateID() {
        String id = in_cate_id.getEditText().getText().toString().trim();
        if (id.isEmpty()){
            in_cate_id.setError("ID is required!");
            in_cate_id.requestFocus();
            return false;
        }else {
            in_cate_id.setError(null);
            in_cate_id.setErrorEnabled(false);
            return true;
        }
    }
}