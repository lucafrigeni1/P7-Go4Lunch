package com.example.go4lunch.DI;

import android.content.Context;

import com.example.go4lunch.ViewModel.RestaurantDataRepository;
import com.example.go4lunch.ViewModel.WorkerDataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injections {

    public static RestaurantDataRepository provideRestaurantDataRepository(Context context){
        return new RestaurantDataRepository();
    }

    public static WorkerDataRepository provideWorkerDataRepository(Context context){
        return new WorkerDataRepository();
    }

    public static Executor provideExecutor(){return Executors.newSingleThreadExecutor();}

    public static ViewModelFactory provideViewModelFactory(Context context){
        RestaurantDataRepository restaurantDataRepository = provideRestaurantDataRepository(context);
        WorkerDataRepository workerDataRepository = provideWorkerDataRepository(context);
        Executor executor = provideExecutor();
        return new ViewModelFactory(restaurantDataRepository, workerDataRepository, executor);
    }
}
