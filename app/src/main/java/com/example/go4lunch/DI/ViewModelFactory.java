package com.example.go4lunch.DI;

import com.example.go4lunch.ViewModel.RestaurantDataRepository;
import com.example.go4lunch.ViewModel.RestaurantViewModel;
import com.example.go4lunch.ViewModel.WorkerDataRepository;
import com.example.go4lunch.ViewModel.WorkerViewModel;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final RestaurantDataRepository mRestaurantDataRepository;
    private final WorkerDataRepository mWorkerDataRepository;
    private final Executor mExecutor;

    public ViewModelFactory(RestaurantDataRepository restaurantDataRepository,
                            WorkerDataRepository workerDataRepository,
                            Executor executor){
        this.mRestaurantDataRepository = restaurantDataRepository;
        this.mWorkerDataRepository = workerDataRepository;
        this.mExecutor = executor;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantViewModel.class)){
            return (T) new RestaurantViewModel(mRestaurantDataRepository, mExecutor);
        } else if (modelClass.isAssignableFrom(WorkerViewModel.class)){
            return (T) new WorkerViewModel(mWorkerDataRepository, mExecutor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
