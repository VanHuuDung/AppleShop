package com.example.onlineshopapp.Model;

public class CartItem {
    private int quantity;

    public CartItem() {
    }

    public CartItem(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
