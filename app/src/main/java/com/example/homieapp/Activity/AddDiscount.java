package com.example.homieapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.homieapp.R;
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

public class AddDiscount extends AppCompatActivity {
    private TextInputLayout in_discount_id, in_discount_name, in_discount_percent, in_discount_description;
    private ImageView in_discount_img;
    private Button in_discount_btn;
    private ProgressBar progressBar;
    private Uri uri;
    private DatabaseReference discount_reference;
    private StorageReference discount_img_reference;
    private static final int GalleryPick = 1;
    private String downloadImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_discount);

        //hooks
        in_discount_id = (TextInputLayout) findViewById(R.id.add_discount_id);
        in_discount_name = (TextInputLayout) findViewById(R.id.add_discount_name);
        in_discount_percent = (TextInputLayout) findViewById(R.id.add_discount_percent);
        in_discount_description = (TextInputLayout) findViewById(R.id.add_discount_description);
        in_discount_img = (ImageView) findViewById(R.id.add_discount_img);
        in_discount_btn = (Button) findViewById(R.id.add_discount_btn);
        progressBar = (ProgressBar) findViewById(R.id.add_discount_progressbar);

        discount_reference = FirebaseDatabase.getInstance().getReference("discounts");
        discount_img_reference = FirebaseStorage.getInstance().getReference("discount images");

        in_discount_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });
        in_discount_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDiscount();
            }
        });

    }

    private void addDiscount() {
        if (!validateID() | !validateName() | !validatePercent() | !validateDescription() | !validateImage()){
            return;
        }else{
            addItem();
        }
    }

    private void addItem() {
        progressBar.setVisibility(View.VISIBLE);
        String name = in_discount_name.getEditText().getText().toString().trim();
        String id = in_discount_id.getEditText().getText().toString().trim();
        String percent = in_discount_percent.getEditText().getText().toString().trim();
        String description = in_discount_description.getEditText().getText().toString().trim();

        final StorageReference filePath = discount_img_reference.child(uri.getLastPathSegment() + id + ".jpg");
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
                                        Toast.makeText(getApplicationContext(),"got the discount image url successfully ...", Toast.LENGTH_SHORT).show();

                                        HashMap<String, Object> discountMap = new HashMap<>();
                                        discountMap.put("ID", id);
                                        discountMap.put("Name", name);
                                        discountMap.put("Image", downloadImageUrl);
                                        discountMap.put("Percent", percent);
                                        discountMap.put("Description", description);

                                        discount_reference.child(id).updateChildren(discountMap).
                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Intent intent = new Intent(AddDiscount.this, MainActivity.class);
                                                            startActivity(intent);
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(AddDiscount.this, "Discount is added successfully ...", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(AddDiscount.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "Discount image is mandatory ...", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateDescription() {
        String description = in_discount_description.getEditText().getText().toString().trim();
        if (description.isEmpty()){
            in_discount_description.setError("ID is required");
            in_discount_description.requestFocus();
            return  false;
        }else {
            in_discount_description.setError(null);
            in_discount_description.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePercent() {
        String percent = in_discount_percent.getEditText().getText().toString().trim();
        if (percent.isEmpty()){
            in_discount_percent.setError("ID is required");
            in_discount_percent.requestFocus();
            return  false;
        }else {
            in_discount_percent.setError(null);
            in_discount_percent.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateName() {
        String name = in_discount_name.getEditText().getText().toString().trim();
        if (name.isEmpty()){
            in_discount_name.setError("ID is required");
            in_discount_name.requestFocus();
            return  false;
        }else {
            in_discount_name.setError(null);
            in_discount_name.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateID() {
        String id = in_discount_id.getEditText().getText().toString().trim();
        if (id.isEmpty()){
            in_discount_id.setError("ID is required");
            in_discount_id.requestFocus();
            return  false;
        }else {
            in_discount_id.setError(null);
            in_discount_id.setErrorEnabled(false);
            return true;
        }
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
            in_discount_img.setImageURI(uri);
        }
    }
}