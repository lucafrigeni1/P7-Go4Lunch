package com.example.go4lunch.models;

import java.util.List;

import androidx.annotation.Nullable;

public class Worker {

    String id;
    String restaurantId;
    String name;
    String mail;
    String picture;
    List<Restaurant> favoriteRestaurant;

    public Worker() {
    }

    public Worker(String id, String restaurantId, String name, String mail, String picture, List<Restaurant> favoriteRestaurant) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.mail = mail;
        this.picture = picture;
        this.favoriteRestaurant = favoriteRestaurant;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String id) {
        this.restaurantId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<Restaurant> getFavoriteRestaurant() {
        return favoriteRestaurant;
    }

    public void setFavoriteRestaurant(List<Restaurant> favoriteRestaurant) {
        this.favoriteRestaurant = favoriteRestaurant;
    }
}
