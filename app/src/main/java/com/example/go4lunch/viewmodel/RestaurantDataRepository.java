package com.example.go4lunch.viewmodel;

import com.example.go4lunch.Retrofit.RetrofitApi;
import com.example.go4lunch.Retrofit.RetrofitUtils;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

import static com.example.go4lunch.viewmodel.WorkerDataRepository.latLng;
import static com.example.go4lunch.viewmodel.WorkerDataRepository.workersCollectionReference;

public class RestaurantDataRepository {

    public final static CollectionReference restaurantsCollectionReference =
            FirebaseFirestore.getInstance().collection("Restaurant");

    RetrofitApi retrofitApi = RetrofitUtils.getRetrofit().create(RetrofitApi.class);

    //CREATE
    public void addRestaurant(Restaurant restaurant) {
        restaurantsCollectionReference.document(restaurant.getId()).set(restaurant);
    }

    public void getPlaces(Double longitude, Double latitude) {
        List<Result> places = new ArrayList<>();

        retrofitApi.getNearbyPlaces(latitude + "," + longitude, "1500", "restaurant", RetrofitUtils.API_KEY)
                .enqueue(new Callback<Places>() {
                    @Override
                    public void onResponse(@NonNull Call<Places> call, @NonNull Response<Places> response) {
                        String nextPage = Objects.requireNonNull(response.body()).getNextPageToken();
                        places.addAll(response.body().getResults());
                        if (nextPage != null) {
                            retrofitApi.getNextPageToken(nextPage, RetrofitUtils.API_KEY).enqueue(new Callback<Places>() {
                                @Override
                                public void onResponse(@NonNull Call<Places> call, @NonNull Response<Places> response) {
                                    places.addAll(Objects.requireNonNull(response.body()).getResults());
                                }

                                @Override
                                public void onFailure(@NonNull Call<Places> call, @NonNull Throwable t) {}
                            });
                        }
                        for (Result result : places) {
                            getPlacesDetails(result.getPlaceId());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Places> call, @NonNull Throwable t) {}
                });
    }

    private void getPlacesDetails(String id) {
        retrofitApi.getPlacesDetails(RetrofitUtils.API_KEY, id).enqueue(new Callback<PlaceDetail>() {
            @Override
            public void onResponse(@NonNull Call<PlaceDetail> call, @NonNull Response<PlaceDetail> response) {
                setRestaurant(Objects.requireNonNull(response.body()).getResult(), id);
            }

            @Override
            public void onFailure(@NonNull Call<PlaceDetail> call, @NonNull Throwable t) {}
        });
    }

    public void setRestaurant(ResultDetails placeDetail, String id){
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
            addRestaurant(restaurant);
        }
    }

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

    public LiveData<List<Restaurant>> getRestaurantList() {
        MutableLiveData<List<Restaurant>> data = new MutableLiveData<>();
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
                    distanceFilter(restaurant, restaurantList);
                }
                data.setValue(restaurantList);
            });
        });
        return data;
    }

    public LiveData<List<Restaurant>> getFilteredRestaurantList(String input) {
        MutableLiveData<List<Restaurant>> data = new MutableLiveData<>();
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
                    distanceFilter(restaurant, restaurantList);
                }
                filter(input, restaurantList, data);
            });
        });
        return data;
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

    public void distanceFilter(Restaurant restaurant, List<Restaurant> restaurantList){
        if (latLng != null) {
            restaurant.setDistance((int) SphericalUtil.computeDistanceBetween(latLng,
                    new LatLng(restaurant.getLocation().getLat(), restaurant.getLocation().getLng())));

            if (restaurant.getDistance() < 750) {
                restaurantList.add(restaurant);
            }
        }
    }

    public void filter(String input, List<Restaurant> restaurantList, MutableLiveData data){
        List<Predictions> predictionsList = new ArrayList<>();
        List<Restaurant> filteredRestaurantList = new ArrayList<>();

        retrofitApi.getAutocomplete(input,
                latLng.latitude + "," + latLng.longitude,
                "1500",
                "establishment",
                RetrofitUtils.API_KEY).enqueue(new Callback<Autocomplete>() {
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
}
