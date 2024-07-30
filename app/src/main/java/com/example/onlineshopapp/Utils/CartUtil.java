package com.example.onlineshopapp.Utils;

import androidx.annotation.NonNull;

import com.example.onlineshopapp.Model.Cart;
import com.example.onlineshopapp.Model.CartItem;
import com.example.onlineshopapp.Model.Products;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartUtil {
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final String userId = auth.getCurrentUser().getUid();
    private static final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Carts").child(userId);
    private static final DatabaseReference proRef = FirebaseDatabase.getInstance().getReference("Products");

    public static void addToCart(String productId, int quantity, int price) {
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Cart cart;
                if (snapshot.exists()) {
                    cart = snapshot.getValue(Cart.class);
                } else {
                    cart = new Cart(userId);
                }
                cart.addProduct(productId, quantity, price);
                cartRef.setValue(cart);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void updateCartItemQuantity(String productId, int quantity) {
        cartRef.child("products").child(productId).child("quantity").setValue(quantity);
        updateTotalPrice();
    }

    public static void removeCartItem(String productId) {
        cartRef.child("products").child(productId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateTotalPrice();
                checkAndDeleteCartIfEmpty();
            }
        });
    }

    public static void updateTotalPrice() {
        cartRef.child("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot productsSnapshot = task.getResult();
                List<Task<DataSnapshot>> tasks = new ArrayList<>();

                for (DataSnapshot productSnapshot : productsSnapshot.getChildren()) {
                    String productId = productSnapshot.getKey();
                    tasks.add(proRef.child(productId).get());
                }

                Tasks.whenAllComplete(tasks).addOnCompleteListener(productTasks -> {
                    int totalPrice = 0;

                    for (Task<DataSnapshot> productTask : tasks) {
                        if (productTask.isSuccessful()) {
                            DataSnapshot productSnapshot = productTask.getResult();
                            Integer price = productSnapshot.child("price").getValue(Integer.class);
                            if (price != null) {
                                String productId = productSnapshot.getKey();
                                CartItem item = productsSnapshot.child(productId).getValue(CartItem.class);
                                if (item != null) {
                                    totalPrice += price * item.getQuantity();
                                }
                            }
                        }
                    }

                    cartRef.child("total_price").setValue(totalPrice);
                });
            }
        });
    }

    private static void checkAndDeleteCartIfEmpty() {

        DatabaseReference child = cartRef.child("products");
        child.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot productsSnapshot = task.getResult();
                if (!productsSnapshot.exists() || productsSnapshot.getChildrenCount() == 0) {
                    cartRef.removeValue();
                }
            }
        });
    }
}

