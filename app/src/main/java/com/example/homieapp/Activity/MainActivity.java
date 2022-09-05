package com.example.homieapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homieapp.Adapter.CategoryAdapter;
import com.example.homieapp.Adapter.DiscountAdapter;
import com.example.homieapp.Adapter.ProductAdapter;
import com.example.homieapp.R;
import com.example.homieapp.model.Discount;
import com.example.homieapp.model.ProductCategory;
import com.example.homieapp.model.Products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView productCatRecycler, proItemRecycler, discountRecycler;
    ImageView toolbar_ava;
    TextView cate_view_all, discount_view_all, product_view_all;
    SearchView enter_search;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    //    Menu menu;
    FirebaseAuth auth;
    CategoryAdapter categoryAdapter;
    ProductAdapter productAdapter;
    DiscountAdapter discountAdapter;
    FloatingActionButton cart_fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Drawer
        drawerLayout = findViewById(R.id._container);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);
        navigationView.bringToFront();
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_view);
        toolbar_ava = findViewById(R.id.toolbar_ava);

        View headerView = navigationView.getHeaderView(0);
        TextView headerUserName = headerView.findViewById(R.id.side_menu_username);
        TextView headerUserEmail = headerView.findViewById(R.id.side_menu_email);
        ShapeableImageView headerAva = headerView.findViewById(R.id.side_menu_ava);

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersDetails = sessionManager.getUserDetailFromSession();
        String user_name  = usersDetails.get(SessionManager.KEY_USERNAME);
        String email = usersDetails.get(SessionManager.KEY_EMAIL);
        String image_url = usersDetails.get(SessionManager.KEY_IMAGE);
        headerUserName.setText(user_name);
        headerUserEmail.setText(email);
        Picasso.with(this).load(image_url).into(toolbar_ava);
        Picasso.with(this).load(image_url).into(headerAva);

        //content main
        //Search();
        enter_search = findViewById(R.id.search_bar);
        enter_search.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
        cate_view_all = findViewById(R.id.home_view_all_category);
        discount_view_all = findViewById(R.id.home_view_all_discount);
        product_view_all = findViewById(R.id.home_view_all_product);
        cate_view_all.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DashboardCategoryActivity.class)));
        discount_view_all.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DashboardDiscountActivity.class)));
        product_view_all.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DashboardProduct.class)));

        //Category
        productCatRecycler = findViewById(R.id.home_cate_recycler);
        productCatRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        FirebaseRecyclerOptions<ProductCategory> options =
                new FirebaseRecyclerOptions.Builder<ProductCategory>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("categories"), ProductCategory.class)
                        .build();
        productCatRecycler.getRecycledViewPool().clear();
        categoryAdapter = new CategoryAdapter(options,this);
        productCatRecycler.setAdapter(categoryAdapter);

        //products
        proItemRecycler = findViewById(R.id.home_product_recycler);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL,false);
        proItemRecycler.setLayoutManager(layoutManager);
        FirebaseRecyclerOptions<Products> options1 =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("products"),Products.class )
                .build();
        proItemRecycler.getRecycledViewPool().clear();
        productAdapter = new ProductAdapter(options1, this);
        proItemRecycler.setAdapter(productAdapter);

        //discount
        discountRecycler = findViewById(R.id.home_discount_recycler);
        discountRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        FirebaseRecyclerOptions<Discount> options2 =
                new FirebaseRecyclerOptions.Builder<Discount>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("discounts"),Discount.class )
                .build();
        discountRecycler.getRecycledViewPool().clear();
        discountAdapter = new DiscountAdapter(options2);
        discountRecycler.setAdapter(discountAdapter);

        cart_fab = findViewById(R.id.home_fab);
        cart_fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        categoryAdapter.startListening();
        productAdapter.startListening();
        discountAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        categoryAdapter.stopListening();
        productAdapter.stopListening();
        discountAdapter.stopListening();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case  R.id.nav_cart:
                startActivity(new Intent(this, CartActivity.class));
                break;
            case R.id.nav_chat:
                startActivity(new Intent(this, AdminManagement.class));
                break;
            case R.id.nav_heart:
                startActivity( new Intent(MainActivity.this, DashboardFavoriteProduct.class));
                break;
            case R.id.nav_help:
                break;
            case R.id.nav_logout:
                auth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.nav_promotion:
                startActivity(new Intent(MainActivity.this, DiscountDetail.class));
                break;
            case R.id.nav_purchase:
                break;
            case R.id.nav_setting:
                break;
            case R.id.nav_statics:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {           //=>Tránh đóng ứng dụng khi ấn nút back

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
}