package com.example.onlineshopapp.Model;

import java.util.HashMap;

public class Products {
    String id;
    Boolean active;
    String category_id;
    String created_at;
    String description;
    String image_url;
    Boolean isFeatured;
    String name;
    int price;
    int quantity;
    double rating;
    private HashMap<String, String> specifications;

    public HashMap<String, String> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(HashMap<String, String> specifications) {
        this.specifications = specifications;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Boolean getFeatured() {
        return isFeatured;
    }

    public void setFeatured(Boolean featured) {
        isFeatured = featured;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Products() {
    }

    public Products(Boolean active, String category_id, String created_at, String description, String image_url, Boolean isFeatured, String name, int price, int quantity, double rating, HashMap<String, String> specifications) {
        this.active = active;
        this.category_id = category_id;
        this.created_at = created_at;
        this.description = description;
        this.image_url = image_url;
        this.isFeatured = isFeatured;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.rating = rating;
        this.specifications = specifications;
    }
}
