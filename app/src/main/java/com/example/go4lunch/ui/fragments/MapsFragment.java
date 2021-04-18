package com.example.go4lunch.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.Retrofit.RetrofitApi;
import com.example.go4lunch.di.Injections;
import com.example.go4lunch.di.ViewModelFactory;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.ui.activity.RestaurantDetailActivity;
import com.example.go4lunch.ui.activity.SettingsActivity;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private RestaurantViewModel restaurantViewModel;

    private GoogleMap map;
    private PlacesClient placesClient;
    FusedLocationProviderClient fusedLocationProviderClient;

    private static final String TAG = MapsFragment.class.getSimpleName();
    private CameraPosition cameraPosition;

    private Location lastKnownLocation;
    double currentLat;
    double currentLong;
    LatLng currentLatLng;
    LatLng defaultLocation = new LatLng(48.259386, 7.454241);

    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    String nearbyPlaces = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ defaultLocation + "&radius=1500&type=restaurant&key=AIzaSyDvQNY3Hoc2titIZ-d0JfZh0w0uupLen2A";
    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=48.259386,7.454241&radius=1500&type=restaurant&key=AIzaSyDvQNY3Hoc2titIZ-d0JfZh0w0uupLen2A
    FloatingActionButton floatingActionButton;
    List<Restaurant> restaurantList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);

        floatingActionButton = view.findViewById(R.id.location_button);

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setRestaurantViewModel();
        init();

        setFloatingActionButton();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMapStyle();

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        setMarkers();

    }

    private void init() {
        Places.initialize(Objects.requireNonNull(this.getActivity()), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this.getActivity());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    private void setFloatingActionButton() {
        floatingActionButton.setOnClickListener(v -> getDeviceLocation());
    }

    private void setMapStyle(){
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(Objects.requireNonNull(this.getContext()), R.raw.style_maps));
    }

    //ALLOW THE APP TO SEE THE USER LOCATION
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this.getActivity()),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    //GET USER LOCATION AND ADAPT CAMERA POSITION
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(Objects.requireNonNull(this.getActivity()), task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            getLocation();
                            getPlaces();
                            setMarkers();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void getLocation(){
        currentLat = lastKnownLocation.getLatitude();
        currentLong = lastKnownLocation.getLongitude();
        currentLatLng = new LatLng(currentLat, currentLong);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));
    }

    private void getPlaces(){
        restaurantViewModel.getPlaces(currentLong,currentLat).observe(this, restaurants -> {
            Log.e("restaurants", ""+ restaurants.size());
            restaurantList = restaurants;
        });
    }

    private void setMarkers(){
        for (int i = 0; i < restaurantList.size(); i++){
            Log.e("restaurantslist", ""+ restaurantList.size());

            com.example.go4lunch.models.retrofit.Location location = restaurantList.get(i).getLocation();
            double lat = location.getLat();
            double lng = location.getLng();
            LatLng latLng = new LatLng(lat, lng);

            Bitmap bm;

            if (restaurantList.get(i).getPeople() == 0){
                bm = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_marker_red);
            }else {
                bm = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_marker_green);
            }

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(bm))
                    .title(restaurantList.get(i).getName())
            );
        }

        map.setOnInfoWindowClickListener(marker -> {
            for (Restaurant restaurant : restaurantList){
                if (restaurant.getName().equals(marker.getTitle())){
                    Intent intent = new Intent(this.getContext(), RestaurantDetailActivity.class);
                    intent.putExtra("Restaurant", restaurant);
                    this.getContext().startActivity(intent);
                }
            }
        });
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setRestaurantViewModel(){
        ViewModelFactory viewModelFactory = Injections.provideViewModelFactory(this.getContext());
        this.restaurantViewModel = ViewModelProviders.of(this, viewModelFactory).get(RestaurantViewModel.class);
    }
}