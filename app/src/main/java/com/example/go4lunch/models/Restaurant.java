package com.example.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;

import com.example.go4lunch.models.retrofit.Location;
import com.example.go4lunch.models.retrofit.Period;

import java.util.List;

import androidx.lifecycle.LifecycleService;

public class Restaurant {

    String id;
    List<Worker> workerList;
    String name;
    String photos;
    String address;
    String website;
    String phoneNumber;
    Location location;
    List<Period> openHours;
    double rating;

    public Restaurant(){}

    public Restaurant(String id, List<Worker> workerList, String name, String photos, String address, String website, String phoneNumber, Location location, List<Period> openHours, double rating) {
        this.id = id;
        this.workerList = workerList;
        this.name = name;
        this.photos = photos;
        this.address = address;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.openHours = openHours;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public List<Worker> getWorkerList(){
        return workerList;
    }

    public void setWorkerList(List<Worker> list){
        this.workerList = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Period> getOpenHours() {
        return openHours;
    }

    public void setOpenHours(List<Period> openHours) {
        this.openHours = openHours;
    }
}
