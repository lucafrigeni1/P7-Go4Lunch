package com.example.go4lunch;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CollectionsUtils {

    public static final CollectionReference restaurantsCollectionReference =
            FirebaseFirestore.getInstance().collection("Restaurant");

    public static final CollectionReference workersCollectionReference =
            FirebaseFirestore.getInstance().collection("Worker");
}
