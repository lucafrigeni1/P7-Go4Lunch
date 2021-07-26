package com.example.go4lunch.di;

import com.example.go4lunch.viewmodel.RestaurantDataRepository;
import com.example.go4lunch.viewmodel.WorkerDataRepository;

public class Injections {

    public static RestaurantDataRepository provideRestaurantDataRepository(){
        return new RestaurantDataRepository();
    }

    public static WorkerDataRepository provideWorkerDataRepository(){
        return new WorkerDataRepository();
    }

    public static ViewModelFactory provideViewModelFactory(){
        RestaurantDataRepository restaurantDataRepository = provideRestaurantDataRepository();
        WorkerDataRepository workerDataRepository = provideWorkerDataRepository();
        return new ViewModelFactory(restaurantDataRepository, workerDataRepository);
    }
}
