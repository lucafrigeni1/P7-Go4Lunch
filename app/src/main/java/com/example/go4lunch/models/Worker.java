package com.example.go4lunch.models;

import java.util.List;

import androidx.annotation.Nullable;

public class Worker {

    String id;
    String name;
    String mail;
    String picture;
    String choice;
    List<String> favoriteRestaurant;

    public Worker() {
    }

    public Worker(String id, String name, String mail, String picture, String choice, List<String> favoriteRestaurant) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.picture = picture;
        this.choice = choice;
        this.favoriteRestaurant = favoriteRestaurant;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public List<String> getFavoriteRestaurant() {
        return favoriteRestaurant;
    }

    public void setFavoriteRestaurant(List<String> favoriteRestaurant) {
        this.favoriteRestaurant = favoriteRestaurant;
    }
}
