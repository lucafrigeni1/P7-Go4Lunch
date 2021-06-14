package com.example.go4lunch.viewmodel;

import android.app.Activity;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class WorkerViewModel extends ViewModel {

    private final WorkerDataRepository workerDataSource;

    public WorkerViewModel(WorkerDataRepository workerDataRepository){
        this.workerDataSource = workerDataRepository;
    }

    public FirebaseUser getFirebaseUser(){
        return workerDataSource.getFirebaseUser();
    }

    public LiveData<Worker> getCurrentUser(){
       return workerDataSource.getCurrentUser();
    }

    public LiveData<List<Worker>> getWorkersList(){
        return workerDataSource.getWorkersList();
    }

    public void createWorker(Worker worker){
        workerDataSource.createWorker(worker);
    }

    public LiveData<Boolean> updateWorkerChoice(Restaurant restaurant){
        return workerDataSource.updateWorkerChoice(restaurant);
    }

    public LiveData<Boolean> updateWorkerFavoriteList(List<Restaurant> favoriteRestaurants){
        return workerDataSource.updateWorkerFavoriteList(favoriteRestaurants);
    }
}
