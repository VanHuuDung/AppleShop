package com.example.onlineshopapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlineshopapp.Adapter.CartAdapter;
import com.example.onlineshopapp.Model.Cart;
import com.example.onlineshopapp.Model.CartItem;
import com.example.onlineshopapp.Model.Products;
import com.example.onlineshopapp.R;
import com.example.onlineshopapp.Utils.CartUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemChangeListener {
    private RecyclerView recCart;
    private TextView tvTongTien;
    private CartAdapter adapter;
    private List<String> productIds;
    private Map<String, CartItem> cartItems;
    private DatabaseReference cartRef;
    private String userId;
    FirebaseUser user;

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            cartRef = FirebaseDatabase.getInstance().getReference("Carts").child(userId);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recCart = view.findViewById(R.id.recCart);
        tvTongTien = view.findViewById(R.id.tvTongTien);
        productIds = new ArrayList<>();
        cartItems = new HashMap<>();

        adapter = new CartAdapter(cartItems, productIds, getContext(), this);
        recCart.setLayoutManager(new LinearLayoutManager(getContext()));
        recCart.setAdapter(adapter);

        if (user != null) {
            loadCartItems();
        }


        return view;
    }

    private void loadCartItems() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                productIds.clear();
                DatabaseReference proRef = FirebaseDatabase.getInstance().getReference("Products");
                List<Task<DataSnapshot>> tasks = new ArrayList<>();

                for (DataSnapshot itemSnapshot : snapshot.child("products").getChildren()) {
                    String productId = itemSnapshot.getKey();
                    CartItem item = itemSnapshot.getValue(CartItem.class);
                    cartItems.put(productId, item);
                    productIds.add(productId);

                    Task<DataSnapshot> productTask = proRef.child(productId).get();
                    tasks.add(productTask);
                }

                Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
                    int totalPrice = 0;
                    for (Task<DataSnapshot> productTask : tasks) {
                        if (productTask.isSuccessful()) {
                            DataSnapshot productSnapshot = productTask.getResult();
                            Integer price = productSnapshot.child("price").getValue(Integer.class);
                            String productId = productSnapshot.getKey();
                            if (price != null && cartItems.containsKey(productId)) {
                                CartItem item = cartItems.get(productId);
                                totalPrice += price * item.getQuantity();
                            }
                        } else {
                            // Handle error if needed
                        }
                    }

                    tvTongTien.setText(convertToVND(String.valueOf(totalPrice)));
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
    }


    @Override
    public void onQuantityChanged(String productId, int newQuantity) {
        CartUtil.updateCartItemQuantity(productId, newQuantity);
    }

    @Override
    public void onItemDeleted(String productId) {
        CartUtil.removeCartItem(productId);
    }

    public String convertToVND(String priceString) {
        String cleanPriceString = priceString.replaceAll("[^\\d]", "");
        long priceLong = Long.parseLong(cleanPriceString);
        String vndString = String.format("%,d", priceLong) + " VND";

        return vndString;
    }
}
