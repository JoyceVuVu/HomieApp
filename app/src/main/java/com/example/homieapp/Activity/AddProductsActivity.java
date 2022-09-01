package com.example.homieapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homieapp.R;
import com.example.homieapp.model.Discount;
import com.example.homieapp.model.ProductCategory;
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
import java.util.Locale;

public class AddProductsActivity extends AppCompatActivity {
    private TextInputLayout in_product_id, in_product_name, in_product_qty, in_product_price, in_product_description;
    private Spinner in_product_category, in_product_discount;
    private ImageView in_product_img;
    private Button finish;
    private ProgressBar progressBar;
    private Uri uri;
    private DatabaseReference product_reference,category_reference, discount_reference;
    private StorageReference product_img_reference;
    private static final int GalleryPick = 1;
    private String downloadImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        //hooks
        in_product_id = (TextInputLayout) findViewById(R.id.add_product_id);
        in_product_name = (TextInputLayout) findViewById(R.id.add_product_name);
        in_product_qty = (TextInputLayout) findViewById(R.id.add_product_qty);
        in_product_price = (TextInputLayout) findViewById(R.id.add_product_price);
        in_product_description = (TextInputLayout) findViewById(R.id.add_product_description);
        in_product_category =(Spinner) findViewById(R.id.add_product_category);
        in_product_discount =(Spinner) findViewById(R.id.add_product_discount);
        in_product_img = (ImageView) findViewById(R.id.add_product_img);
        finish = (Button) findViewById(R.id.add_product_btn);
        progressBar = (ProgressBar) findViewById(R.id.add_product_progressbar);
        
        product_reference = FirebaseDatabase.getInstance().getReference("products");
        product_img_reference = FirebaseStorage.getInstance().getReference("product images");

        in_product_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        category_reference = FirebaseDatabase.getInstance().getReference("categories");
        setSpinnerDataFromDB(category_reference, this, in_product_category);
        discount_reference = FirebaseDatabase.getInstance().getReference("discounts");
        setSpinnerDataFromDB(discount_reference,this,in_product_discount);

    }

    public void setSpinnerDataFromDB(DatabaseReference mRef, final Context context, final Spinner spinner) {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> listId = new ArrayList<String>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    String categoryId = snap.child("id").getValue(String.class);
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


    private void addProduct() {
        if (!validateID() | !validateName() | !validateQuantity() | !validatePrice() | !validateDescription() | !validateImage()){
            return;
        }else{
            addItem();
        }
    }

    private void addItem() {
        progressBar.setVisibility(View.VISIBLE);
        String id = in_product_id.getEditText().getText().toString().trim();
        String name = in_product_name.getEditText().getText().toString().trim();
        String quantity =in_product_qty.getEditText().getText().toString().trim();
        String price = in_product_price.getEditText().getText().toString().trim();
        String description = in_product_description.getEditText().getText().toString().trim();
        String category = in_product_category.getSelectedItem().toString().trim();
        String discount = in_product_discount.getSelectedItem().toString().trim();

        final StorageReference filePath = product_img_reference.child(uri.getLastPathSegment() + id + ".jpg");
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
                        Toast.makeText(getApplicationContext(), "Product Image uploaded successfully...", Toast.LENGTH_SHORT).show();
                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                downloadImageUrl = filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        })
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        downloadImageUrl = task.getResult().toString();
                                        Toast.makeText(AddProductsActivity.this, "got the product image url successfully ...", Toast.LENGTH_SHORT).show();

                                        HashMap<String, Object> productMap = new HashMap<>();
                                        productMap.put("id", id);
                                        productMap.put("name", name);
                                        productMap.put("quantity", quantity);
                                        productMap.put("price", price);
                                        productMap.put("description", description);
                                        productMap.put("category", category);
                                        productMap.put("discount", discount);
                                        productMap.put("image", downloadImageUrl);
                                        product_reference.child(id).updateChildren(productMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Intent intent = new Intent(AddProductsActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(AddProductsActivity.this, "Product is added successfully ...", Toast.LENGTH_SHORT).show();
                                                        }else {
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(AddProductsActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "Product image is mandatory ...", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateDescription() {
        String description = in_product_description.getEditText().getText().toString().trim();
        if (description.isEmpty()){
            in_product_description.setError("Description is required");
            in_product_description.requestFocus();
            return false;
        }else {
            in_product_description.setError(null);
            in_product_description.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePrice() {
        String price = in_product_price.getEditText().getText().toString().trim();
        if (price.isEmpty()){
            in_product_price.setError("Price is required");
            in_product_price.requestFocus();
            return false;
        }else {
            in_product_price.setError(null);
            in_product_price.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateQuantity() {
        String quantity = in_product_qty.getEditText().getText().toString().trim();
        if (quantity.isEmpty()){
            in_product_qty.setError("Price is required");
            in_product_qty.requestFocus();
            return false;
        }else {
            in_product_qty.setError(null);
            in_product_qty.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateName() {
        String name = in_product_name.getEditText().getText().toString().trim();
        if (name.isEmpty()){
            in_product_name.setError("Name is required");
            in_product_name.requestFocus();
            return false;
        }else {
            in_product_name.setError(null);
            in_product_name.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateID() {
        String id = in_product_id.getEditText().getText().toString().trim();
        if (id.isEmpty()){
            in_product_id.setError("ID is required");
            in_product_id.requestFocus();
            return false;
        }else {
            in_product_id.setError(null);
            in_product_id.setErrorEnabled(false);
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
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data !=null){
            uri = data.getData();
            in_product_img.setImageURI(uri);
        }
    }
}