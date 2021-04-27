package com.example.go4lunch.viewmodel;

import android.util.Log;

import com.example.go4lunch.Retrofit.RetrofitApi;
import com.example.go4lunch.Retrofit.RetrofitUtils;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.models.retrofit.Period;
import com.example.go4lunch.models.retrofit.PlaceDetail;
import com.example.go4lunch.models.retrofit.Places;
import com.example.go4lunch.models.retrofit.Result;
import com.example.go4lunch.models.retrofit.ResultDetails;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDataRepository {

    private final CollectionReference restaurantsCollectionReference =
            FirebaseFirestore.getInstance().collection("Restaurant");

    private final CollectionReference workersCollectionReference =
            FirebaseFirestore.getInstance().collection("Worker");

    RetrofitApi retrofitApi = RetrofitUtils.getRetrofit().create(RetrofitApi.class);

    private static List<Worker> workerList = new ArrayList<>();
    private static List<Restaurant> restaurantList = new ArrayList<>();

    //CREATE
    public void addRestaurant(Restaurant restaurant) {
        restaurantsCollectionReference
                .document(restaurant.getId())
                .set(restaurant);
    }

    public void getPlaces(Double longitude, Double latitude) {
        String location = latitude + "," + longitude, radius = "1500", type = "restaurant", key = RetrofitUtils.API_KEY;
        retrofitApi.getNearbyPlaces(location, radius, type, key).enqueue(new Callback<Places>() {
            @Override
            public void onResponse(@NonNull Call<Places> call, @NonNull Response<Places> response) {
                List<Result> places = response.body().getResults();
                for (int i = 0; i < places.size(); i++) {
                    getPlacesDetails(key, places.get(i).getPlaceId());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Places> call, @NonNull Throwable t) {
                Log.e("a", t.getMessage());
            }
        });
    }

    private void getPlacesDetails(String key, String id) {
        retrofitApi.getPlacesDetails(key, id).enqueue(new Callback<PlaceDetail>() {
            @Override
            public void onResponse(@NonNull Call<PlaceDetail> call, @NonNull Response<PlaceDetail> response) {
                ResultDetails placeDetail = response.body().getResult();
                List<Worker> list = new ArrayList<>();

                String photos = "";
                if (placeDetail.getPhotos() != null && !placeDetail.getPhotos().isEmpty()) {
                    String photoReference = placeDetail.getPhotos().get(0).getPhotoReference();
                    photos = RetrofitUtils.PHOTO_URL + photoReference + "&key=" + key;
                }
                List<Period> openHours = new ArrayList<>();
                if (placeDetail.getOpeningHours() != null) {
                    openHours = placeDetail.getOpeningHours().getPeriods();
                }

                double rating = 0;
                if (placeDetail.getRating() != null) {
                    rating = placeDetail.getRating();
                }

                Restaurant restaurant = new Restaurant(id,
                        list,
                        placeDetail.getName(),
                        photos,
                        placeDetail.getVicinity(),
                        placeDetail.getWebsite(),
                        placeDetail.getFormattedPhoneNumber(),
                        placeDetail.getGeometry().getLocation(),
                        openHours,
                        rating);

                if (restaurant.getPhotos() != null && !(restaurant.getPhotos().isEmpty())) {
                    addRestaurant(restaurant);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceDetail> call, @NonNull Throwable t) {
                Log.e("a", t.getMessage());
            }
        });
    }

    //READ
    public LiveData<Restaurant> getRestaurant(String id) {
        MutableLiveData<Restaurant> dataRestaurant = new MutableLiveData<>();
        Log.e("getRestaurant1: ", id);
        restaurantsCollectionReference.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Restaurant restaurant = task.getResult().toObject(Restaurant.class);
                Gson gson = new Gson();
                Log.e("getRestaurant2: ", gson.toJson(restaurant));
                dataRestaurant.setValue(restaurant);
            }
        });


        return dataRestaurant;
    }

    public LiveData<List<Restaurant>> getRestaurantsList() {
        getCollections();
        checkRestaurantsParticipants();
        MutableLiveData<List<Restaurant>> data = new MutableLiveData<>();
        data.setValue(restaurantList);
        return data;
    }

    public void getCollections(){
        workersCollectionReference.get().addOnCompleteListener(task -> {
            workerList = new ArrayList<>();
            for (QueryDocumentSnapshot document : task.getResult()) {
                Worker worker = document.toObject(Worker.class);
                workerList.add(worker);
            }
        });
        
        restaurantsCollectionReference.get().addOnCompleteListener(task -> {
            restaurantList = new ArrayList<>();
            for (QueryDocumentSnapshot document : task.getResult()) {
                Restaurant restaurant = document.toObject(Restaurant.class);
                restaurantList.add(restaurant);
            }
        });
    }

    public void checkRestaurantsParticipants(){
        for (int i = 0; i < restaurantList.size(); i++) {
            List<Worker> participantList = new ArrayList<>();
            for (int a = 0; a < workerList.size(); a++) {
                if (workerList.get(a).getRestaurantId() != null) {
                    if (workerList.get(a).getRestaurantId().equals(restaurantList.get(i).getId())) {
                        participantList.add(workerList.get(a));
                    }
                }
            }
            restaurantList.get(i).setWorkerList(participantList);

            restaurantsCollectionReference
                    .document(restaurantList.get(i).getId())
                    .update("workerList", restaurantList.get(i).getWorkerList());
        }
    }
}
