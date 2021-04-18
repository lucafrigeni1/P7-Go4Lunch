package com.example.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;

import com.example.go4lunch.models.retrofit.Location;
import com.example.go4lunch.models.retrofit.Period;

import java.util.List;

import androidx.lifecycle.LifecycleService;

public class Restaurant implements Parcelable {

    String id;
    //List<Worker> workerList;
    String name;
    String photos;
    String address;
    String website;
    String phoneNumber;
    Location location;
    List<Period> openHours;
    double rating;
    int people;

    public Restaurant(){}

    public Restaurant(String id, String name, String photos, String address, String website, String phoneNumber, Location location, List<Period> openHours, double rating, int people) {
        this.id = id;
        this.name = name;
        this.photos = photos;
        this.address = address;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.openHours = openHours;
        this.rating = rating;
        this.people = people;
    }

    protected Restaurant(Parcel in) {
        id = in.readString();
        name = in.readString();
        photos = in.readString();
        address = in.readString();
        website = in.readString();
        phoneNumber = in.readString();
        rating = in.readDouble();
        people = in.readInt();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

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

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(photos);
        dest.writeString(address);
        dest.writeString(website);
        dest.writeString(phoneNumber);
        dest.writeDouble(rating);
        dest.writeInt(people);
    }
}
