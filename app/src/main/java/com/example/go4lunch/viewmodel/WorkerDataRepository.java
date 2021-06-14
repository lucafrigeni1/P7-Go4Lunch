package com.example.go4lunch.viewmodel;

import android.util.Log;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WorkerDataRepository {

    public final static CollectionReference workersCollectionReference =
            FirebaseFirestore.getInstance().collection("Worker");

    public static String currentUserId;

    public static LatLng latLng;

    //CREATE
    public void createWorker(Worker worker) {
        workersCollectionReference
                .document(worker.getId())
                .set(worker);
    }

    //READ
    public LiveData<List<Worker>> getWorkersList() {
        MutableLiveData<List<Worker>> dataWorkerList = new MutableLiveData<>();
        workersCollectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Worker> workerList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Worker worker = document.toObject(Worker.class);
                    workerList.add(worker);
                }
                dataWorkerList.setValue(workerList);
            }
        });
        return dataWorkerList;
    }

    public LiveData<Worker> getCurrentUser() {
        MutableLiveData<Worker> result = new MutableLiveData<>();
        workersCollectionReference.document(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Worker worker = task.getResult().toObject(Worker.class);
                result.setValue(worker);
                Gson gson = new Gson();
                Log.e("Worker: ", gson.toJson(worker));
                Log.e("WorkerID: ", currentUserId);
            }
        });
        return result;
    }

    public FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    //UPDATE
    public LiveData<Boolean> updateWorkerChoice(Restaurant restaurant) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        workersCollectionReference
                .document(currentUserId)
                .update("restaurant", restaurant)
                .addOnCompleteListener(task -> result.postValue(true));
        return result;
    }

    public LiveData<Boolean> updateWorkerFavoriteList(List<Restaurant> favoriteRestaurants) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        workersCollectionReference
                .document(currentUserId)
                .update("favoriteRestaurant", favoriteRestaurants)
                .addOnCompleteListener(task -> result.postValue(true));
        return result;
    }
}
