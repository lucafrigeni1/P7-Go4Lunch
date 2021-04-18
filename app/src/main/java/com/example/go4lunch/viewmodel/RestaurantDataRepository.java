package com.example.go4lunch.viewmodel;

import android.util.Log;

import com.example.go4lunch.Retrofit.RetrofitApi;
import com.example.go4lunch.Retrofit.RetrofitUtils;
import com.example.go4lunch.di.Injections;
import com.example.go4lunch.di.ViewModelFactory;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.models.retrofit.Period;
import com.example.go4lunch.models.retrofit.PlaceDetail;
import com.example.go4lunch.models.retrofit.Places;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDataRepository {

    private final CollectionReference restaurantsCollectionReference =
            FirebaseFirestore.getInstance().collection("Restaurant");

    RetrofitApi retrofitApi = RetrofitUtils.getRetrofit().create(RetrofitApi.class);

    MutableLiveData<List<Restaurant>> placesList = new MutableLiveData<>();
    private final MutableLiveData<List<Restaurant>> restaurants = new MutableLiveData<>();
    List<Restaurant> restaurantsList = new ArrayList<>();


    public void addRestaurant(Restaurant restaurant) {
        restaurantsCollectionReference
                .document(restaurant.getId())
                .set(restaurant);
    }

    //READ
    public LiveData<List<Restaurant>> getRestaurantsList() {
        restaurantsCollectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Restaurant restaurant = document.toObject(Restaurant.class);
                    restaurantsList.add(restaurant);
                }
                restaurants.setValue(restaurantsList);
            }
        });
        return restaurants;
    }

    public LiveData<List<Restaurant>> getPlaces(Double longitude, Double latitude) {

        String location = latitude + "," + longitude, radius = "1500", type = "restaurant", key = "AIzaSyDvQNY3Hoc2titIZ-d0JfZh0w0uupLen2A";
        Call<Places> call = retrofitApi.getNearbyPlaces(location, radius, type, key);

        call.enqueue(new Callback<Places>() {
            @Override
            public void onResponse(@NonNull Call<Places> call, @NonNull Response<Places> response) {
                Places places = response.body();
                int placesSize = places.getResults().size();

                for (int i = 0; i < placesSize; i++) {
                    String id = places.getResults().get(i).getPlaceId();
                    getPlacesDetails(key, id, placesSize);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Places> call, @NonNull Throwable t) {
                Log.e("a", t.getMessage());
            }
        });
        return placesList;
    }

    private void getPlacesDetails(String key, String id, int restaurantListSize) {
        Call<PlaceDetail> callDetails = retrofitApi.getPlacesDetails(key, id);
        callDetails.enqueue(new Callback<PlaceDetail>() {

            @Override
            public void onResponse(@NonNull Call<PlaceDetail> call, @NonNull Response<PlaceDetail> response) {
                PlaceDetail placeDetail = response.body();
                Gson gson = new Gson();
                Log.e("onResponse: ", gson.toJson(placeDetail.getResult().getPhotos()));

                String photos = "";
                if (placeDetail.getResult().getPhotos() != null && !placeDetail.getResult().getPhotos().isEmpty()) {
                    photos = placeDetail.getResult().getPhotos().get(0).getPhotoReference();
                }

                Log.e("onResponse: 1 ", photos);
                List<Period> openHours = new ArrayList<>();
                if (placeDetail.getResult().getOpeningHours() != null) {
                    openHours = placeDetail.getResult().getOpeningHours().getPeriods();
                }

                double rating = 0;
                if (placeDetail.getResult().getRating() != null) {
                    rating = placeDetail.getResult().getRating();
                }

                Restaurant restaurant = new Restaurant(id,
                        placeDetail.getResult().getName(),
                        photos,
                        placeDetail.getResult().getVicinity(),
                        placeDetail.getResult().getWebsite(),
                        placeDetail.getResult().getFormattedPhoneNumber(),
                        placeDetail.getResult().getGeometry().getLocation(),
                        openHours,
                        rating,
                        0);
                restaurantsList.add(restaurant);
                addRestaurant(restaurant);

                if (restaurantsList.size() == restaurantListSize) {
                    placesList.postValue(restaurantsList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceDetail> call, @NonNull Throwable t) {
                Log.e("a", t.getMessage());
            }
        });
    }

    private void checkRestaurantParticipant(){
    }



}
