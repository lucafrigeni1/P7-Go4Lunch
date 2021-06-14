package com.example.go4lunch.models;

import java.util.ArrayList;
import java.util.List;

public class Worker{

    String id;
    Restaurant restaurant;
    String name;
    String mail;
    String picture;
    List<Restaurant> favoriteRestaurant;

    public Worker() {
    }

    public Worker(String id,
                  Restaurant restaurant,
                  String name,
                  String mail,
                  String picture,
                  List<Restaurant> favoriteRestaurant) {
        this.id = id;
        this.restaurant = restaurant;
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

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public List<Restaurant> getFavoriteRestaurant() {
        if (favoriteRestaurant == null){
            return new ArrayList<>();
        } else
        return favoriteRestaurant;
    }
}
