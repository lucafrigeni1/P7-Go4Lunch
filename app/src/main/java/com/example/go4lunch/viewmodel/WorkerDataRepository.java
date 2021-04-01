package com.example.go4lunch.viewmodel;

import android.location.Location;
import android.util.Log;

import com.example.go4lunch.models.Worker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

public class WorkerDataRepository {


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final static String COLLECTION_NAME = "Worker";
    private final CollectionReference workersCollectionReference = db.collection(COLLECTION_NAME);

    private final MutableLiveData<List<Worker>> dataWorkerList = new MutableLiveData<>();
    private List<Worker> workerList = new ArrayList<>();


    //CREATE
    public void createWorker(Worker worker){
        workersCollectionReference
                .document(worker.getId())
                .set(worker);
    }

    //READ
    public LiveData<List<Worker>> getWorkersList(){
        workersCollectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Worker worker = document.toObject(Worker.class);
                    workerList.add(worker);
                }
                dataWorkerList.setValue(workerList);
            }
        });
        return dataWorkerList;
    }

    public Task<DocumentSnapshot> getWorker(String id){
        return workersCollectionReference.document(id).get();
    }


    //UPDATE
    public Task<Void> updateWorkerChoice(String id, String choice){
        return workersCollectionReference.document(id).update("choice", choice);
    }

    public Task<Void> updateWorkerFavoriteList(String id, List<String> favoriteRestaurant){
        return workersCollectionReference.document(id).update("favoriteRestaurant", favoriteRestaurant);
    }
}
