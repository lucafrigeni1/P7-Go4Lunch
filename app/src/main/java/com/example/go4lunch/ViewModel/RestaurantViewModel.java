package com.example.go4lunch.ViewModel;

import java.util.concurrent.Executor;

import androidx.lifecycle.ViewModel;

public class RestaurantViewModel extends ViewModel {

    private final RestaurantDataRepository restaurantDataSource;
    private final Executor mExecutor;

    public RestaurantViewModel(RestaurantDataRepository restaurantDataRepository, Executor executor){
        this.restaurantDataSource = restaurantDataRepository;
        this.mExecutor = executor;
    }
}
