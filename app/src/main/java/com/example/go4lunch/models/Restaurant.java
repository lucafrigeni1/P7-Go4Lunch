package com.example.go4lunch.models;

import com.example.go4lunch.models.retrofit.Location;
import com.example.go4lunch.models.retrofit.Period;

import java.util.Comparator;
import java.util.List;

public class Restaurant{

    String id;
    List<Worker> workerList;
    String name;
    String photos;
    String address;
    String website;
    String phoneNumber;
    Location location;
    int distance;
    List<Period> openHours;
    double rating;

    public Restaurant() {
    }

    public Restaurant(String id,
                      List<Worker> workerList,
                      String name,
                      String photos,
                      String address,
                      String website,
                      String phoneNumber,
                      Location location,
                      int distance,
                      List<Period> openHours,
                      double rating) {
        this.id = id;
        this.workerList = workerList;
        this.name = name;
        this.photos = photos;
        this.address = address;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.distance = distance;
        this.openHours = openHours;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public List<Worker> getWorkerList() {
        return workerList;
    }

    public void setWorkerList(List<Worker> list) {
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

    public String getAddress() {
        return address;
    }

    public Location getLocation() {
        return location;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getRating() {
        return rating;
    }

    public List<Period> getOpenHours() {
        return openHours;
    }

    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof Restaurant){
            Restaurant ptr = (Restaurant) v;
            retVal = ptr.id.equals(this.id);
        }
        return retVal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public static class RestaurantComparator implements Comparator<Restaurant> {
        @Override
        public int compare(Restaurant o1, Restaurant o2) {
            return o1.getDistance() - o2.getDistance();
        }
    }
}
