package com.example.go4lunch.viewmodel;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static com.example.go4lunch.Utils.workersCollectionReference;

public class WorkerDataRepository {

    public String currentUserId = FirebaseAuth.getInstance().getUid();
    public static LatLng latLng;

    public FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    //CREATE
    public LiveData<Boolean> createWorker() {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        FirebaseUser firebaseUser = getFirebaseUser();
        AtomicBoolean isCreated = new AtomicBoolean(false);

        workersCollectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Worker worker = document.toObject(Worker.class);
                    if (worker.getId().equals(firebaseUser.getUid())){
                        isCreated.set(true);
                    }
                }

                if (!isCreated.get()){
                    List<Restaurant> favoriteRestaurant = new ArrayList<>();
                    Worker worker = new Worker(
                            firebaseUser.getUid(),
                            null,
                            firebaseUser.getDisplayName(),
                            firebaseUser.getEmail(),
                            null,
                            favoriteRestaurant);

                    workersCollectionReference.document(worker.getId()).set(worker);
                }
                data.setValue(true);
            }
        });
        return data;
    }

    //READ
    public LiveData<Worker> getCurrentUser() {
        MutableLiveData<Worker> data = new MutableLiveData<>();
        workersCollectionReference.document(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Worker worker = task.getResult().toObject(Worker.class);
                data.setValue(worker);
            }
        });
        return data;
    }

    public LiveData<List<Worker>> getWorkersList() {
        MutableLiveData<List<Worker>> data = new MutableLiveData<>();
        List<Worker> workerList = new ArrayList<>();
        workersCollectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Worker worker = document.toObject(Worker.class);
                    workerList.add(worker);
                }
                data.setValue(workerList);
            }
        });
        return data;
    }

    //UPDATE
    public LiveData<Boolean> updateWorkerChoice(Restaurant restaurant) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        workersCollectionReference
                .document(currentUserId)
                .update("restaurant", restaurant)
                .addOnCompleteListener(task -> data.postValue(true));
        return data;
    }

    public LiveData<Boolean> updateWorkerFavoriteList(List<Restaurant> favoriteRestaurants) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        workersCollectionReference
                .document(currentUserId)
                .update("favoriteRestaurant", favoriteRestaurants)
                .addOnCompleteListener(task -> data.postValue(true));
        return data;
    }
}
