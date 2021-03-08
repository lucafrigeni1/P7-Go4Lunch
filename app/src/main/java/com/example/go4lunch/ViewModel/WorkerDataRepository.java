package com.example.go4lunch.ViewModel;

import com.example.go4lunch.Models.Worker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class WorkerDataRepository {

    private final static String COLLECTION_NAME = "workers";

    public static CollectionReference getWorkersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createWorker(String id, String name, String mail, String picture, String choice, List<String> favoriteRestaurant){
        Worker workerToCreate = new Worker(id, name, mail, picture, choice, favoriteRestaurant);
        return WorkerDataRepository.getWorkersCollection().document(id).set(workerToCreate);
    }

    public static Task<QuerySnapshot> getWorkerList(){
        return WorkerDataRepository.getWorkersCollection().get();
    }

    public static Task<DocumentSnapshot> getWorker(String id){
        return WorkerDataRepository.getWorkersCollection().document(id).get();
    }

    public static Task<Void> updateWorkerChoice(String id, String choice){
        return WorkerDataRepository.getWorkersCollection().document(id).update("choice", choice);
    }

    public static Task<Void> updateWorkerFavoriteList(String id, List<String> favoriteRestaurant){
        return WorkerDataRepository.getWorkersCollection().document(id).update("favoriteRestaurant", favoriteRestaurant);
    }
}
