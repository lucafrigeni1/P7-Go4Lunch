package com.example.go4lunch.viewmodel;

import android.util.Log;

import com.example.go4lunch.Retrofit.RetrofitApi;
import com.example.go4lunch.Retrofit.RetrofitUtils;
import com.example.go4lunch.models.retrofit.Location;
import com.example.go4lunch.models.retrofit.Period;
import com.example.go4lunch.models.retrofit.Photo;
import com.example.go4lunch.models.retrofit.PlaceDetail;
import com.example.go4lunch.models.retrofit.Places;
import com.example.go4lunch.models.Restaurant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDataRepository {

    MutableLiveData<List<Restaurant>> placesList;
    List<Restaurant> restaurantsList;
    Restaurant restaurant;

    //READ
    public LiveData<List<Restaurant>> getRestaurantsList(){
        return null;
    }

    public LiveData<List<Restaurant>> getPlaces(Double longitude, Double latitude){
        placesList = new MutableLiveData<>();

        String location = latitude + "," + longitude;
        String radius = "1500" ;
        String type = "restaurant";
        String key = "AIzaSyDvQNY3Hoc2titIZ-d0JfZh0w0uupLen2A";

        RetrofitApi retrofitApi = RetrofitUtils.getRetrofit().create(RetrofitApi.class);
        Call<Places> call = retrofitApi.getNearbyPlaces(location, radius, type, key);

        call.enqueue(new Callback<Places>() {
            @Override
            public void onResponse(@NonNull Call<Places> call, @NonNull Response<Places> response) {
                Places places = response.body();

                restaurantsList = new ArrayList<>();

                for (int i = 0 ; i < places.getResults().size(); i++) {

                    String id = places.getResults().get(i).getPlaceId();
                    Call<PlaceDetail> callDetails = retrofitApi.getPlacesDetails(key, id);
                    callDetails.enqueue(new Callback<PlaceDetail>() {

                        @Override
                        public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                            PlaceDetail placeDetail = response.body();

                            String name = placeDetail.getResult().getName();
                            String photos = "";
                            if (!placeDetail.getResult().getPhotos().isEmpty()){
                                photos = placeDetail.getResult().getPhotos().get(0).getPhotoReference();
                            }

                            String address = placeDetail.getResult().getVicinity();
                            String website = placeDetail.getResult().getWebsite();
                            String phoneNumber = placeDetail.getResult().getFormattedPhoneNumber();
                            Location location = placeDetail.getResult().getGeometry().getLocation();
                            List<Period> openHours = placeDetail.getResult().getOpeningHours().getPeriods();
                            double rating = placeDetail.getResult().getRating();
                            int people = 0;

                            restaurant = new Restaurant(id, name, photos, address, website, phoneNumber, location ,openHours, rating, people);
                            restaurantsList.add(restaurant);
                        }

                        @Override
                        public void onFailure(Call<PlaceDetail> call, Throwable t) {
                            Log.e("a" , t.getMessage());
                        }
                    });
                }
                placesList.postValue(restaurantsList);
            }

            @Override
            public void onFailure(@NonNull Call<Places> call, @NonNull Throwable t) {
                Log.e("a" , t.getMessage());
            }
        });
        return placesList;
    }

    public void getDeviceLocation(){

    }

}
