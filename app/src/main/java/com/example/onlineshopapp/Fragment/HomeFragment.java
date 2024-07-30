package com.example.onlineshopapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.onlineshopapp.Adapter.Categories_Adapter;
import com.example.onlineshopapp.Adapter.Products_Adapter;
import com.example.onlineshopapp.Model.Categories;
import com.example.onlineshopapp.Model.Products;
import com.example.onlineshopapp.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    RecyclerView recyclerView_categories;
    RecyclerView recyclerView_products;
    ArrayList<Categories> categoriesArrayList = new ArrayList<>();
    Categories_Adapter categoriesArrayAdapter;
    ArrayList<Products> productsArrayList = new ArrayList<>();
    Products_Adapter productsAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        addControls(view);
        FirebaseApp.initializeApp(getActivity());
        getDataFromFirebase_Categories();


//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView_products.setLayoutManager(layoutManager);
//        recyclerView_products.setItemAnimator(new DefaultItemAnimator());
//        recyclerView_products.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.HORIZONTAL));
//        recyclerView_products.setAdapter(productsAdapter);
        getDataFromFirebase_Products();
        addEvents();
        return view;
    }
    private void addControls(View view) {
        recyclerView_categories = view.findViewById(R.id.recycler_categories);
        recyclerView_products = view.findViewById(R.id.recyclerView_products);
    }
    private void addEvents() {
    }

    public void getDataFromFirebase_Categories()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Categories");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriesArrayList.clear();
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    Categories user = userSnapshot.getValue(Categories.class);
                    user.setId(userSnapshot.getKey());
                    categoriesArrayList.add(user);
                }
                recyclerView_categories.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.HORIZONTAL));
                categoriesArrayAdapter = new Categories_Adapter(categoriesArrayList, getContext());
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView_categories.setLayoutManager(layoutManager);
                recyclerView_categories.setItemAnimator(new DefaultItemAnimator());
                recyclerView_categories.setAdapter(categoriesArrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void getDataFromFirebase_Products()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Products");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsArrayList.clear();
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    Products user = userSnapshot.getValue(Products.class);
                    user.setId(userSnapshot.getKey());
                    productsArrayList.add(user);
                }

                recyclerView_products.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.HORIZONTAL));
                productsAdapter = new Products_Adapter(productsArrayList, getContext());
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                recyclerView_products.setLayoutManager(layoutManager);
                recyclerView_products.setItemAnimator(new DefaultItemAnimator());
                recyclerView_products.setAdapter(productsAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}