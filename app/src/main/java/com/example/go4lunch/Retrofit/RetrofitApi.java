package com.example.go4lunch.Retrofit;

import com.example.go4lunch.models.retrofit.PlaceDetail;
import com.example.go4lunch.models.retrofit.Places;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitApi {

    @GET("nearbysearch/json")
    Call<Places> getNearbyPlaces(
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("type") String type,
            @Query("key") String key);

    @GET("details/json")
    Call<PlaceDetail> getPlacesDetails(
            @Query("key") String key,
            @Query("place_id") String placeId);

    @GET("nearbysearch/json")
    Call<Places> getNextPageToken(
            @Query("pagetoken") String page,
            @Query("key") String key);
}
