package com.example.go4lunch.Retrofit;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtils {

    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    public static final String NEARBY_SEARCH = "nearbysearch/json?location=";
    public static final String NEARBY_SEARCH_PARAMETER = "&radius=1500&type=restaurant";
    public static final String PHOTO_URL = "photo?maxwidth=400&photoreference=";
    public static final String API_KEY = "AIzaSyDvQNY3Hoc2titIZ-d0JfZh0w0uupLen2A";

    public static Retrofit getRetrofit(){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Gson gson = gsonBuilder.create();

        Retrofit.Builder builder = new Retrofit
                .Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging);

        return builder.client(httpClient.build()).build();
    }
}
