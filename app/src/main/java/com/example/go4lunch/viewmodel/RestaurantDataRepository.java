package com.example.go4lunch.viewmodel;

import com.example.go4lunch.Utils;
import com.example.go4lunch.retrofit.RetrofitApi;
import com.example.go4lunch.retrofit.RetrofitUtils;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.models.retrofit.Autocomplete;
import com.example.go4lunch.models.retrofit.Period;
import com.example.go4lunch.models.retrofit.PlaceDetail;
import com.example.go4lunch.models.retrofit.Places;
import com.example.go4lunch.models.retrofit.Predictions;
import com.example.go4lunch.models.retrofit.Result;
import com.example.go4lunch.models.retrofit.ResultDetails;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.go4lunch.Utils.latLng;
import static com.example.go4lunch.CollectionsUtils.restaurantsCollectionReference;
import static com.example.go4lunch.CollectionsUtils.workersCollectionReference;

public class RestaurantDataRepository {

    RetrofitApi retrofitApi = RetrofitUtils.getRetrofit().create(RetrofitApi.class);

    //READ
    public LiveData<Restaurant> getRestaurant(String id) {
        MutableLiveData<Restaurant> data = new MutableLiveData<>();
        restaurantsCollectionReference.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Restaurant restaurant = task.getResult().toObject(Restaurant.class);
                data.setValue(restaurant);
            }
        });
        return data;
    }

    public LiveData<List<Restaurant>> getRestaurantsList() {
        MutableLiveData<List<Restaurant>> data = new MutableLiveData<>();
        getCollections(data, false, null);
        return data;
    }

    public LiveData<List<Restaurant>> getFilteredRestaurantList(String input) {
        MutableLiveData<List<Restaurant>> data = new MutableLiveData<>();
        getCollections(data, true, input);
        return data;
    }

    public void getCollections(MutableLiveData<List<Restaurant>> data, boolean isFilter, String input){
        List<Worker> workerList = new ArrayList<>();
        List<Restaurant> restaurantList = new ArrayList<>();

        workersCollectionReference.get().addOnCompleteListener(taskWorker -> {
            for (QueryDocumentSnapshot document : taskWorker.getResult()) {
                Worker worker = document.toObject(Worker.class);
                workerList.add(worker);
            }

            restaurantsCollectionReference.get().addOnCompleteListener(taskRestaurant -> {
                for (QueryDocumentSnapshot document : taskRestaurant.getResult()) {
                    Restaurant restaurant = document.toObject(Restaurant.class);
                    updateRestaurantParticipants(restaurant, workerList);
                    Utils.distanceFilter(restaurant, restaurantList);
                }

               if (restaurantList.isEmpty() && latLng != null){
                   getPlaces(latLng.longitude, latLng.latitude, restaurantList, data);
               } else {
                   if (isFilter){
                       filter(input, restaurantList, data);
                   } else
                       data.setValue(restaurantList);
               }
            });
        });
    }

    public void updateRestaurantParticipants(Restaurant restaurant, List<Worker> workerList){
        List<Worker> participantList = new ArrayList<>();
        for (Worker worker : workerList) {
            if (worker.getRestaurant() != null) {
                if (worker.getRestaurant().getId().equals(restaurant.getId())) {
                    participantList.add(worker);
                }
            }
        }
        restaurant.setWorkerList(participantList);
        restaurantsCollectionReference.document(restaurant.getId()).update("workerList", participantList);
    }

    public void filter(String input, List<Restaurant> restaurantList, MutableLiveData<List<Restaurant>> data){
        List<Predictions> predictionsList = new ArrayList<>();
        List<Restaurant> filteredRestaurantList = new ArrayList<>();

        retrofitApi.getAutocomplete(input, latLng.latitude + "," + latLng.longitude,
                "1500", "establishment", RetrofitUtils.API_KEY)
                .enqueue(new Callback<Autocomplete>() {
            @Override
            public void onResponse(@NonNull Call<Autocomplete> call, @NonNull Response<Autocomplete> response) {
                predictionsList.addAll(Objects.requireNonNull(response.body()).getPredictions());

                for (Restaurant restaurant : restaurantList) {
                    for (Predictions predictions : predictionsList) {
                        if (restaurant.getId().equals(predictions.getPlaceId())) {
                            filteredRestaurantList.add(restaurant);
                        }
                    }
                }
                data.setValue(filteredRestaurantList);
            }
            @Override
            public void onFailure(@NonNull Call<Autocomplete> call, @NonNull Throwable t) {}
        });
    }

    public void getPlaces(Double longitude, Double latitude, List<Restaurant> restaurantList, MutableLiveData<List<Restaurant>> data) {
        List<Result> places = new ArrayList<>();
        retrofitApi.getNearbyPlaces(latitude + "," + longitude, "1500", "restaurant", RetrofitUtils.API_KEY)
                .enqueue(new Callback<Places>() {
                    @Override
                    public void onResponse(@NonNull Call<Places> call, @NonNull Response<Places> response) {
                        String nextPage = Objects.requireNonNull(response.body()).getNextPageToken();
                        places.addAll(response.body().getResults());
                        if (nextPage != null) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            retrofitApi.getNextPageToken(nextPage, RetrofitUtils.API_KEY).enqueue(new Callback<Places>() {
                                @Override
                                public void onResponse(@NonNull Call<Places> call, @NonNull Response<Places> response2) {
                                    places.addAll(Objects.requireNonNull(response2.body()).getResults());
                                    int counter = places.size();
                                    for (Result result : places) {
                                        counter--;
                                        getPlacesDetails(result.getPlaceId(), restaurantList, counter, data);
                                    }
                                }
                                @Override
                                public void onFailure(@NonNull Call<Places> call, @NonNull Throwable t) {}
                            });
                        } else {
                            int counter = places.size();
                            for (Result result : places) {
                                counter--;
                                getPlacesDetails(result.getPlaceId(), restaurantList, counter, data);
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<Places> call, @NonNull Throwable t) {}
                });
    }

    private void getPlacesDetails(String id, List<Restaurant> restaurantList, int counter, MutableLiveData<List<Restaurant>> data) {
        retrofitApi.getPlacesDetails(RetrofitUtils.API_KEY, id).enqueue(new Callback<PlaceDetail>() {
            @Override
            public void onResponse(@NonNull Call<PlaceDetail> call, @NonNull Response<PlaceDetail> response) {
                Restaurant restaurant = setRestaurant(Objects.requireNonNull(response.body()).getResult(), id);
                if (restaurant != null){
                    restaurantList.add(restaurant);
                }

                if (counter == 0) {
                    data.setValue(restaurantList);
                }
            }
            @Override
            public void onFailure(@NonNull Call<PlaceDetail> call, @NonNull Throwable t) {}
        });
    }

    public Restaurant setRestaurant(ResultDetails placeDetail, String id){
        List<Worker> workers = new ArrayList<>();

        String photos = "";
        if (placeDetail.getPhotos() != null && !placeDetail.getPhotos().isEmpty()) {
            photos = RetrofitUtils.BASE_URL + RetrofitUtils.PHOTO_URL
                    + placeDetail.getPhotos().get(0).getPhotoReference()
                    + "&key=" + RetrofitUtils.API_KEY;
        }

        int distance = 0;

        List<Period> openHours = new ArrayList<>();
        if (placeDetail.getOpeningHours() != null) {
            openHours = placeDetail.getOpeningHours().getPeriods();
        }

        double rating = 0;
        if (placeDetail.getRating() != null) {
            rating = placeDetail.getRating();
        }

        Restaurant restaurant = new Restaurant(
                id,
                workers,
                placeDetail.getName(),
                photos,
                placeDetail.getVicinity(),
                placeDetail.getWebsite(),
                placeDetail.getFormattedPhoneNumber(),
                placeDetail.getGeometry().getLocation(),
                distance,
                openHours,
                rating);

        if (restaurant.getPhotos() != null && !(restaurant.getPhotos().isEmpty())) {
            restaurantsCollectionReference.document(restaurant.getId()).set(restaurant);
            return restaurant;
        } else
            return null;
    }
}
