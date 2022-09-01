package com.example.homieapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.homieapp.R;

public class AdminManagement extends AppCompatActivity {
    Button back, add_product, edit_product, add_category, edit_category, add_discount, edit_discount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_management);

        back = findViewById(R.id.admin_management_back);
        back.setOnClickListener(view -> startActivity(new Intent(AdminManagement.this, MainActivity.class)));
        add_product = findViewById(R.id.admin_add_product);
        add_product.setOnClickListener(view -> startActivity(new Intent(AdminManagement.this, AddProductsActivity.class)));
        edit_product= findViewById(R.id.admin_edit_product);
        edit_product.setOnClickListener(view -> startActivity(new Intent(AdminManagement.this, EditProduct.class)));
        add_category = findViewById(R.id.admin_add_category);
        add_category.setOnClickListener(view -> startActivity(new Intent(AdminManagement.this, AddCategoryActivity.class)));
        edit_category = findViewById(R.id.admin_edit_category);
        edit_category.setOnClickListener(view -> startActivity(new Intent(AdminManagement.this, EditCategory.class)));
        add_discount = findViewById(R.id.admin_add_discount);
        add_discount.setOnClickListener(view -> startActivity(new Intent(AdminManagement.this, AddDiscount.class)));
        edit_discount = findViewById(R.id.admin_edit_discount);
        edit_discount.setOnClickListener(view -> startActivity(new Intent(AdminManagement.this, EditDiscount.class)));
    }
}