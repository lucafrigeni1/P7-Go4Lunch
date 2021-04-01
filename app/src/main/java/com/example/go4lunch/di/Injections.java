package com.example.go4lunch.di;

import android.content.Context;

import com.example.go4lunch.viewmodel.RestaurantDataRepository;
import com.example.go4lunch.viewmodel.WorkerDataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injections {

    public static RestaurantDataRepository provideRestaurantDataRepository(Context context){
        return new RestaurantDataRepository();
    }

    public static WorkerDataRepository provideWorkerDataRepository(Context context){
        return new WorkerDataRepository();
    }

    public static ViewModelFactory provideViewModelFactory(Context context){
        RestaurantDataRepository restaurantDataRepository = provideRestaurantDataRepository(context);
        WorkerDataRepository workerDataRepository = provideWorkerDataRepository(context);
        return new ViewModelFactory(restaurantDataRepository, workerDataRepository);
    }
}
