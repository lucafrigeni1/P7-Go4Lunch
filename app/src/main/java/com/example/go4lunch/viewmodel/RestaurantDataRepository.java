package com.example.go4lunch.viewmodel;

import android.util.Log;

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
import com.google.gson.Gson;
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

    private static List<Worker> workerList = new ArrayList<>();
    private static List<Restaurant> restaurantList = new ArrayList<>();
    private static List<Restaurant> filteredRestaurantList = new ArrayList<>();


    //CREATE
    public void addRestaurant(Restaurant restaurant) {
        restaurantsCollectionReference
                .document(restaurant.getId())
                .set(restaurant);
    }

    public void getPlaces(Double longitude, Double latitude) {
        List<Result> places = new ArrayList<>();

        retrofitApi.getNearbyPlaces(latitude + "," + longitude, "1500", "restaurant", RetrofitUtils.API_KEY)
                .enqueue(new Callback<Places>() {
            @Override
            public void onResponse(@NonNull Call<Places> call, @NonNull Response<Places> response) {
                String nextPage = response.body().getNextPageToken();
                places.addAll(response.body().getResults());
                if (nextPage != null){
                    retrofitApi.getNextPageToken(nextPage,RetrofitUtils.API_KEY).enqueue(new Callback<Places>() {
                        @Override
                        public void onResponse(Call<Places> call, Response<Places> response) {
                            places.addAll(response.body().getResults());
                        }

                        @Override
                        public void onFailure(Call<Places> call, Throwable t) {

                        }
                    });
                }

                for (int i = 0; i < places.size(); i++) {
                    getPlacesDetails(places.get(i).getPlaceId());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Places> call, @NonNull Throwable t) {
                Log.e("a", t.getMessage());
            }
        });
    }

    private void getPlacesDetails(String id) {
        retrofitApi.getPlacesDetails(RetrofitUtils.API_KEY, id).enqueue(new Callback<PlaceDetail>() {
            @Override
            public void onResponse(@NonNull Call<PlaceDetail> call, @NonNull Response<PlaceDetail> response) {
                ResultDetails placeDetail = response.body().getResult();
                List<Worker> list = new ArrayList<>();

                String photos = "";
                if (placeDetail.getPhotos() != null && !placeDetail.getPhotos().isEmpty()) {
                    photos = RetrofitUtils.BASE_URL
                            + RetrofitUtils.PHOTO_URL
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

                Restaurant restaurant = new Restaurant(id,
                        list,
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

            @Override
            public void onFailure(@NonNull Call<PlaceDetail> call, @NonNull Throwable t) {
                Log.e("a", t.getMessage());
            }
        });
    }

    //READ
    public LiveData<Restaurant> getRestaurant(String id) {
        MutableLiveData<Restaurant> dataRestaurant = new MutableLiveData<>();
        restaurantsCollectionReference.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Restaurant restaurant = task.getResult().toObject(Restaurant.class);
                dataRestaurant.setValue(restaurant);
            }
        });
        return dataRestaurant;
    }

    public LiveData<List<Restaurant>> getRestaurantList(){
        MutableLiveData<List<Restaurant>> data = new MutableLiveData<>();
        workersCollectionReference.get().addOnCompleteListener(task -> {
            workerList = new ArrayList<>();
            for (QueryDocumentSnapshot document : task.getResult()) {
                Worker worker = document.toObject(Worker.class);
                workerList.add(worker);
            }
            getRestaurantsFromFirebase(data, workerList);
        });
        return data;
    }

    public LiveData<List<Restaurant>> getFilteredRestaurantList(String input){
        MutableLiveData<List<Restaurant>> data = new MutableLiveData<>();
        List<Predictions> predictionsList = new ArrayList<>();

        workersCollectionReference.get().addOnCompleteListener(task -> {
            workerList = new ArrayList<>();
            for (QueryDocumentSnapshot document : task.getResult()) {
                Worker worker = document.toObject(Worker.class);
                workerList.add(worker);
            }
            getRestaurantsFromFirebase(data, workerList);
            // TODO

            retrofitApi.getAutocomplete(input,
                    latLng.latitude + "," + latLng.longitude,
                    "1500",
                    "establishment",
                    RetrofitUtils.API_KEY).enqueue(new Callback<Autocomplete>() {
                @Override
                public void onResponse(Call<Autocomplete> call, Response<Autocomplete> response) {
                    filteredRestaurantList = new ArrayList<>();
                    predictionsList.addAll(response.body().getPredictions());

                    for (Restaurant restaurant : restaurantList){
                        for (Predictions predictions: predictionsList){
                            if (restaurant.getId().equals(predictions.getPlaceId())){
                                filteredRestaurantList.add(restaurant);
                            }
                        }
                    }
                    data.setValue(filteredRestaurantList);
                }
                @Override
                public void onFailure(Call<Autocomplete> call, Throwable t) {
                }
            });
        });
        return data;
    }

    public void getRestaurantsFromFirebase(MutableLiveData<List<Restaurant>> data, List<Worker> workerList){
        restaurantsCollectionReference.get().addOnCompleteListener(task -> {
            restaurantList = new ArrayList<>();
            for (QueryDocumentSnapshot document : task.getResult()) {
                Restaurant restaurant = document.toObject(Restaurant.class);

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

                restaurant.setDistance((int) SphericalUtil.computeDistanceBetween(latLng,
                        new LatLng(restaurant.getLocation().getLat(), restaurant.getLocation().getLng())));

                restaurantList.add(restaurant);

                for (Restaurant restaurant1 : restaurantList){
                    if (restaurant1.getDistance() > 750){
                        restaurantList.remove(restaurant1);
                    }
                }

            }
            data.setValue(restaurantList);
        });
    }
}
