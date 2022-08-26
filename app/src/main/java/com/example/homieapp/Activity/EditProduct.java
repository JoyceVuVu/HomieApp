package com.example.homieapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditProduct extends AppCompatActivity {
    private TextInputLayout edit_product_id, edit_product_name, edit_product_quantity, edit_product_price, edit_product_description;
    private TextView activity_title;
    private Button finish;
    private Spinner edit_spinner_category, edit_discount_spinner;
    private ImageView edit_product_img;
    private ProgressBar progressBar;
    private Uri uri;
    private DatabaseReference reference,cate_reference,discount_reference;
    private StorageReference product_img_reference;
    private static final int GalleryPick = 1;
    private String downloadImageUrl;
    private String defaultImageUrl;
    String id;
    boolean isCLicked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_products);

        id = "bansofa";

        //hooks
        edit_product_id = findViewById(R.id.add_product_id);
        edit_product_name = findViewById(R.id.add_product_name);
        edit_product_price = findViewById(R.id.add_product_price);
        edit_product_quantity = findViewById(R.id.add_product_qty);
        edit_product_description = findViewById(R.id.add_product_description);
        edit_spinner_category = findViewById(R.id.add_product_category);
        edit_discount_spinner = findViewById(R.id.add_product_discount);
        finish = findViewById(R.id.add_product_btn);
        progressBar = findViewById(R.id.add_product_progressbar);
        edit_product_img = findViewById(R.id.add_product_img);
        activity_title = findViewById(R.id.activity_tittle);
        activity_title.setText("Edit Product");

        reference = FirebaseDatabase.getInstance().getReference("products");
        product_img_reference = FirebaseStorage.getInstance().getReference("product images");

         cate_reference = FirebaseDatabase.getInstance().getReference("categories");
         discount_reference = FirebaseDatabase.getInstance().getReference("discounts");

        setSpinnerDataFromDB(cate_reference, this, edit_spinner_category);
        setSpinnerDataFromDB(discount_reference, this, edit_discount_spinner);

        edit_product_img.setOnClickListener(new View.OnClickListener() {
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

        String _name = edit_product_name.getEditText().getText().toString().trim();
        String _quantity = edit_product_quantity.getEditText().getText().toString().trim();
        String _price = edit_product_price.getEditText().getText().toString().trim();
        String _description = edit_product_description.getEditText().getText().toString().trim();
        String _category = edit_spinner_category.getSelectedItem().toString().trim();
        String _discount = edit_discount_spinner.getSelectedItem().toString().trim();

        if (isCLicked){
            final StorageReference oldPath = FirebaseStorage.getInstance().getReferenceFromUrl(defaultImageUrl);
            oldPath.delete();
            final StorageReference newPath = product_img_reference.child(uri.getLastPathSegment() + id + ".jpg");
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
                                            Toast.makeText(EditProduct.this, "got the category image url successfully ...", Toast.LENGTH_SHORT).show();
                                            saveDate(id,_name, _quantity, _price, _description,_category, _discount, downloadImageUrl);
                                        }
                                    });
                        }
                    });
        }else {
            downloadImageUrl = defaultImageUrl;
            saveDate(id,_name, _quantity, _price, _description,_category, _discount, downloadImageUrl);
        }



    }
    private void saveDate(String id,String name, String quantity,String price,String description, String category,String discount, String url){
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("ID", id);
        productMap.put("Name", name);
        productMap.put("Quantity", quantity);
        productMap.put("Price", price);
        productMap.put("Description", description);
        productMap.put("Category", category);
        productMap.put("Discount", discount);
        productMap.put("Image", url);
        reference.child(id).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(EditProduct.this, DashboardProduct.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditProduct.this, "Product is edited successfully ...", Toast.LENGTH_SHORT).show();
                        }else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditProduct.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void showData() {
        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                edit_product_id.getEditText().setText(id);
                edit_product_id.setEnabled(false);
                edit_product_name.getEditText().setText(snapshot.child("Name").getValue(String.class));
                edit_product_quantity.getEditText().setText(snapshot.child("Quantity").getValue(String.class));
                edit_product_price.getEditText().setText(snapshot.child("Price").getValue(String.class));
                edit_product_description.getEditText().setText(snapshot.child("Description").getValue(String.class));
                defaultImageUrl = snapshot.child("Image").getValue(String.class);
                Glide.with(getApplicationContext()).load(defaultImageUrl).into(edit_product_img);
                String product_cate_value = snapshot.child("Category").getValue(String.class);
                List<String> list = new ArrayList<String>();
                list.add(product_cate_value);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,list);
                edit_spinner_category.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setSpinnerDataFromDB(DatabaseReference mRef, final Context context, final Spinner spinner) {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> listId = new ArrayList<String>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    String categoryId = snap.child("ID").getValue(String.class);
                    listId.add(categoryId);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listId);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
            edit_product_img.setImageURI(uri);
        }
    }
}
