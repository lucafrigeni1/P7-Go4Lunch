package com.example.go4lunch.Models;

import android.text.format.Time;

public class Restaurant {

    String id;
    String name;
    String picture;
    String location;
    String website;
    String phoneNumber;
    int people;
    int rating;
    Time opening;
    Time closing;

    public Restaurant(String id, String name, String picture, String location, String website, String phoneNumber, int people, int rating, Time opening, Time closing) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.location = location;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.people = people;
        this.rating = rating;
        this.opening = opening;
        this.closing = closing;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Time getOpening() {
        return opening;
    }

    public void setOpening(Time opening) {
        this.opening = opening;
    }

    public Time getClosing() {
        return closing;
    }

    public void setClosing(Time closing) {
        this.closing = closing;
    }
}
