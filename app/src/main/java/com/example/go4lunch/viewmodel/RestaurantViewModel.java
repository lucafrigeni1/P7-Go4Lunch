package com.example.go4lunch.viewmodel;

import com.example.go4lunch.models.Restaurant;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class RestaurantViewModel extends ViewModel {

    private final RestaurantDataRepository restaurantDataSource;

    public RestaurantViewModel(RestaurantDataRepository restaurantDataRepository){
        this.restaurantDataSource = restaurantDataRepository;
    }

    public void getPlaces(Double longitude, Double latitude){
        restaurantDataSource.getPlaces(longitude,latitude);
    }

    public LiveData<Restaurant> getRestaurant(String id){
        return restaurantDataSource.getRestaurant(id);
    }

    public LiveData<List<Restaurant>> getRestaurantsList(){
        return restaurantDataSource.getRestaurantList();
    }

    public LiveData<List<Restaurant>> getFilteredRestaurantsList(String input){
        return restaurantDataSource.getFilteredRestaurantList(input);
    }
}
