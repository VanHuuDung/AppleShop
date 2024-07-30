package com.example.onlineshopapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.onlineshopapp.Adapter.Products_Adapter;
import com.example.onlineshopapp.Model.Products;
import com.example.onlineshopapp.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ProductTypeActivity extends AppCompatActivity {
    ArrayList<Products> productsArrayList = new ArrayList<>();
    Products_Adapter productsAdapter;
    RecyclerView recyclerView_products;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_type);
        addControls();
        Intent intent = getIntent();
        String categories = intent.getStringExtra("categories");
        String img = intent.getStringExtra("url_image");
        Glide.with(this).load(img).into(imageView);
        FirebaseApp.initializeApp(getApplicationContext());
        getDataFromFirebase_Products(categories);
        addEvents();
    }

    private void addControls() {
        recyclerView_products = findViewById(R.id.recyclerView_products_type);
        imageView = findViewById(R.id.imageView_products_type);
    }
    public void getDataFromFirebase_Products(String categories)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Products");

        myRef.orderByChild("category_id").equalTo(categories).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsArrayList.clear();
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    Products user = userSnapshot.getValue(Products.class);
                    productsArrayList.add(user);
                }
                Collections.reverse(productsArrayList);

                recyclerView_products.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));
                productsAdapter = new Products_Adapter(productsArrayList, getApplicationContext());
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
                recyclerView_products.setLayoutManager(layoutManager);
                recyclerView_products.setItemAnimator(new DefaultItemAnimator());
                recyclerView_products.setAdapter(productsAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void addEvents() {

    }
}