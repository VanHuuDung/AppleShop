package com.example.onlineshopapp.Model;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private Map<String, CartItem> products = new HashMap<>();
    private int total_price;
    private String user_id;

    // Constructors, getters, and setters

    public Cart() {
    }

    public Cart(String user_id) {
        this.user_id = user_id;
        this.total_price = 0;
    }

    public Map<String, CartItem> getProducts() {
        return products;
    }

    public void setProducts(Map<String, CartItem> products) {
        this.products = products;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void addProduct(String productId, int quantity, int price) {
        if (products.containsKey(productId)) {
            CartItem item = products.get(productId);
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            products.put(productId, new CartItem(quantity));
        }
        total_price += quantity * price;
    }
}

