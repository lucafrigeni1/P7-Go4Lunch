package com.example.go4lunch.di;

import com.example.go4lunch.viewmodel.RestaurantDataRepository;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.example.go4lunch.viewmodel.WorkerDataRepository;
import com.example.go4lunch.viewmodel.WorkerViewModel;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final RestaurantDataRepository mRestaurantDataRepository;
    private final WorkerDataRepository mWorkerDataRepository;

    public ViewModelFactory(RestaurantDataRepository restaurantDataRepository,
                            WorkerDataRepository workerDataRepository){
        this.mRestaurantDataRepository = restaurantDataRepository;
        this.mWorkerDataRepository = workerDataRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantViewModel.class)){
            return (T) new RestaurantViewModel(mRestaurantDataRepository);
        } else if (modelClass.isAssignableFrom(WorkerViewModel.class)){
            return (T) new WorkerViewModel(mWorkerDataRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
