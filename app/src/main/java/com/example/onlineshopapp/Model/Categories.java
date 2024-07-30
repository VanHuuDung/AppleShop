package com.example.onlineshopapp.Model;

public class Categories {
    String id;
    String image_url;
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Categories() {
    }

    public Categories(String image_url, String name) {
        this.image_url = image_url;
        this.name = name;
    }
}
