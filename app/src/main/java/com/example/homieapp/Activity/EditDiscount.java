package com.example.homieapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.homieapp.R;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditDiscount extends AppCompatActivity {
    private ImageView edit_discount_img;
    private TextInputLayout edit_discount_id, edit_discount_name, edit_discount_description, edit_discount_percent;
    private TextView activity_title;
    private Button finish;
    private ImageButton back;
    private ProgressBar progressBar;
    private DatabaseReference reference;
    private StorageReference discount_img_reference;
    private static final int GalleryPick = 1;
    private String downloadImageUrl;
    private Uri uri;
    private String defaultImageUrl;
    String id;
    boolean isCLicked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_discount);

        id = getIntent().getStringExtra("ID");

        //hooks
        back = findViewById(R.id.add_discount_back);
        back.setOnClickListener(view -> finish());
        edit_discount_id = findViewById(R.id.add_discount_id);
        edit_discount_name = findViewById(R.id.add_discount_name);
        edit_discount_percent = findViewById(R.id.add_discount_percent);
        edit_discount_description = findViewById(R.id.add_discount_description);
        finish = findViewById(R.id.add_discount_btn);
        progressBar = findViewById(R.id.add_discount_progressbar);
        edit_discount_img = findViewById(R.id.add_discount_img);
        activity_title = findViewById(R.id.add_discount_title);
        activity_title.setText("Edit Discount");

        reference = FirebaseDatabase.getInstance().getReference("discounts");
        discount_img_reference = FirebaseStorage.getInstance().getReference("discount images");

        edit_discount_img.setOnClickListener(new View.OnClickListener() {
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

        String _name = edit_discount_name.getEditText().getText().toString().trim();
        String _percent = edit_discount_percent.getEditText().getText().toString().trim();
        String _description = edit_discount_description.getEditText().getText().toString().trim();

        if (isCLicked){
            final StorageReference oldPath = FirebaseStorage.getInstance().getReferenceFromUrl(defaultImageUrl);
            oldPath.delete();
            final StorageReference newPath = discount_img_reference.child(uri.getLastPathSegment() + id + ".jpg");
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
                                            Toast.makeText(EditDiscount.this, "got the category image url successfully ...", Toast.LENGTH_SHORT).show();
                                            saveDate(id,_name,_percent, _description,  downloadImageUrl);
                                        }
                                    });
                        }
                    });
        }else {
            downloadImageUrl = defaultImageUrl;
            saveDate(id,_name,_percent, _description, downloadImageUrl);
        }



    }
    private void saveDate(String id,String name, String percent,String description, String url){
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("id", id);
        productMap.put("name", name);
        productMap.put("percent", percent);
        productMap.put("description", description);
        productMap.put("image", url);
        reference.child(id).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(EditDiscount.this, DashboardProduct.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditDiscount.this, "Discount is edited successfully ...", Toast.LENGTH_SHORT).show();
                        }else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditDiscount.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void showData() {
        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                edit_discount_id.getEditText().setText(id);
                edit_discount_id.setEnabled(false);
                edit_discount_name.getEditText().setText(snapshot.child("name").getValue(String.class));
                edit_discount_percent.getEditText().setText(snapshot.child("percent").getValue(String.class));
                edit_discount_description.getEditText().setText(snapshot.child("description").getValue(String.class));
                defaultImageUrl = snapshot.child("image").getValue(String.class);
                Glide.with(getApplicationContext()).load(defaultImageUrl).into(edit_discount_img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void OpenGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null){
            uri = data.getData();
            edit_discount_img.setImageURI(uri);
        }
    }
}
