package com.example.onlineshopapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlineshopapp.Activity.LoginActivity;
import com.example.onlineshopapp.Adapter.Products_Adapter;
import com.example.onlineshopapp.MainActivity;
import com.example.onlineshopapp.Model.Products;
import com.example.onlineshopapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private TextView textView;
    private Button btn;
    private RecyclerView recyclerView_products;
    private ArrayList<Products> productsArrayList = new ArrayList<>();
    private Products_Adapter productsAdapter;
    private String userId;
    private FirebaseUser user;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        addControls(view);

        checkLoginStatus();
        if (!userId.isEmpty()) {
            getDataFromFirebase_Wishlist(userId);
        }
        addEvents();
        return view;
    }

    private void addControls(View view) {
        recyclerView_products = view.findViewById(R.id.recyclerView_Wishlist);
        textView = view.findViewById(R.id.txt_favorite);
        btn = view.findViewById(R.id.btn_favorite);
    }

    private void addEvents() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userId.isEmpty()) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void checkLoginStatus() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            userId = "";
        }
        updateUI();
    }

    private void getDataFromFirebase_Wishlist(String userId) {
        if (userId.isEmpty())
            return;

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Wishlist").child(userId);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> favoriteProductIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String productId = snapshot.child("product_id").getValue(String.class);
                    if (productId != null) {
                        favoriteProductIds.add(productId.trim());
                    }
                }

                if (!favoriteProductIds.isEmpty()) {
                    getDataFromFirebase_ProductsWishlist(favoriteProductIds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
    }

    private void getDataFromFirebase_ProductsWishlist(List<String> productIds) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Products");
        productsArrayList.clear();

        for (String productId : productIds) {
            myRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Products product = dataSnapshot.getValue(Products.class);
                    if (product != null) {
                        product.setId(dataSnapshot.getKey());
                        productsArrayList.add(product);
                    }

                    if (productsArrayList.size() == productIds.size()) {
                        updateUI();
                        setupRecyclerView();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void setupRecyclerView() {
        productsAdapter = new Products_Adapter(productsArrayList, getContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView_products.setLayoutManager(layoutManager);
        recyclerView_products.setItemAnimator(new DefaultItemAnimator());
        recyclerView_products.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.HORIZONTAL));
        recyclerView_products.setAdapter(productsAdapter);
    }

    private void updateUI() {
        if (userId.isEmpty()) {
            textView.setText("Bạn chưa đăng nhập. Chuyển đến trang đăng nhập");
            btn.setText("Đăng Nhập");
            btn.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            recyclerView_products.setVisibility(View.GONE);
        } else {
            if (productsArrayList.isEmpty()) {
                textView.setText("Bạn chưa chọn thích sản phẩm nào");
                btn.setText("Mua Ngay!");
                textView.setVisibility(View.VISIBLE);
                btn.setVisibility(View.VISIBLE);
                recyclerView_products.setVisibility(View.GONE);
            } else {
                textView.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);
                recyclerView_products.setVisibility(View.VISIBLE);
            }
        }
    }
}
