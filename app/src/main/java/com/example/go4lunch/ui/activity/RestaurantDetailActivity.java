package com.example.go4lunch.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.Utils;
import com.example.go4lunch.di.Injections;
import com.example.go4lunch.di.ViewModelFactory;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.ui.recyclerview.RestaurantDetailWorkersAdapter;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.example.go4lunch.viewmodel.WorkerViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.go4lunch.R.id;
import static com.example.go4lunch.R.layout;

public class RestaurantDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RESTAURANT = "RestaurantId";

    WorkerViewModel workerViewModel;
    RestaurantViewModel restaurantViewModel;

    Worker currentUser;

    ImageButton backButton;
    ImageView picture;
    ImageView rating;
    TextView name;
    TextView location;
    FloatingActionButton choiceBtn;
    ImageButton likeBtn;
    ImageButton callBtn;
    ImageButton websiteBtn;
    RecyclerView recyclerView;

    boolean isChosen;
    boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_restaurant_details);
        findViewById();
        setViewModel();
        getCurrentUser();
    }

    private void findViewById() {
        backButton = findViewById(id.back_button);
        picture = findViewById(id.restaurant_image);
        rating = findViewById(id.rating_image);
        name = findViewById(id.restaurant_name);
        location = findViewById(id.restaurant_type_and_location);
        choiceBtn = findViewById(id.choice_button);
        likeBtn = findViewById(id.like_button);
        callBtn = findViewById(id.call_button);
        websiteBtn = findViewById(id.web_button);
        recyclerView = findViewById(id.participants);
    }

    private void getCurrentUser(){
        workerViewModel.getCurrentUser().observe(this, worker -> {
            currentUser = worker;
            getRestaurant();
        });
    }

    private void getRestaurant() {
        String id = getIntent().getStringExtra(EXTRA_RESTAURANT);
        restaurantViewModel.getRestaurant(id).observe(this, this::setView);
    }

    private void setView(Restaurant restaurant) {
        Glide.with(picture)
                .load(restaurant.getPhotos())
                .apply(RequestOptions.centerCropTransform())
                .into(picture);

        if (Utils.setRating(restaurant) == 1){
            rating.setImageResource(R.drawable.ic_rating_1);
        } else if (Utils.setRating(restaurant) == 2){
            rating.setImageResource(R.drawable.ic_rating_2);
        } else if (Utils.setRating(restaurant) == 3){
            rating.setImageResource(R.drawable.ic_rating_3);
        }

        name.setText(restaurant.getName());
        location.setText(restaurant.getAddress());

        setBackButton();
        setChoiceBtn(restaurant);
        setCallBtn(restaurant);
        setLikeBtn(restaurant);
        setWebsiteBtn(restaurant);
        getWorkers(restaurant);
    }

    private void setBackButton(){
        backButton.setOnClickListener(v -> finish());
    }

    private void setChoiceBtn(Restaurant restaurant) {
        if (currentUser.getRestaurant() != null) {
            isChosen = currentUser.getRestaurant().equals(restaurant);
        } else
            isChosen = false;

        if (isChosen){
            choiceBtn.setImageResource(R.drawable.ic_baseline_check_24);
        } else
            choiceBtn.setImageResource(R.drawable.ic_baseline_uncheck_24);

        choiceBtn.setOnClickListener(v -> {
            if (isChosen) {
                workerViewModel.updateWorkerChoice(null).observe(this, aBoolean -> getCurrentUser());
            } else {
                workerViewModel.updateWorkerChoice(restaurant).observe(this, aBoolean -> getCurrentUser());
            }
        });
    }

    private void setCallBtn(Restaurant restaurant) {
        callBtn.setOnClickListener(v -> {
            if (restaurant.getPhoneNumber().isEmpty()) {
                Toast.makeText(getApplicationContext(), "no phone number available", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(restaurant.getPhoneNumber())));
                startActivity(intent);
            }
        });
    }

    private void setLikeBtn(Restaurant restaurant) {
        isFavorite = currentUser.getFavoriteRestaurant().contains(restaurant);

        if (isFavorite){
            likeBtn.setImageResource(R.drawable.ic_baseline_orange_star_24);
        } else
            likeBtn.setImageResource(R.drawable.ic_baseline_star_outline_24);

        likeBtn.setOnClickListener(v -> {
            List<Restaurant> favoriteRestaurants = new ArrayList<>(currentUser.getFavoriteRestaurant());

            if (isFavorite) {
                favoriteRestaurants.remove(restaurant);
            } else {
                favoriteRestaurants.add(restaurant);
            }
            workerViewModel.updateWorkerFavoriteList(favoriteRestaurants).observe(this, aBoolean -> getCurrentUser());
        });
    }

    private void setWebsiteBtn(Restaurant restaurant) {
        websiteBtn.setOnClickListener(v -> {
            if (restaurant.getWebsite().isEmpty()) {
                Toast.makeText(getApplicationContext(), "no website available", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.getWebsite()));
                startActivity(intent);
            }
        });
    }

    private void getWorkers(Restaurant restaurant) {
        workerViewModel.getWorkersList().observe(this, workers -> setWorkerList(workers, restaurant));
    }

    private void setWorkerList(List<Worker> workers, Restaurant restaurant) {
        List<Worker> participants = new ArrayList<>();
        for (Worker worker : workers) {
            if (worker.getRestaurant() != null) {
                if (worker.getRestaurant().getId().equals(restaurant.getId())) {
                    participants.add(worker);
                }
            }
        }
        setRecyclerView(participants);
    }

    private void setRecyclerView(List<Worker> participants) {
        RestaurantDetailWorkersAdapter adapter = new RestaurantDetailWorkersAdapter(participants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void setViewModel() {
        ViewModelFactory viewModelFactory = Injections.provideViewModelFactory();
        this.workerViewModel = ViewModelProviders.of(this, viewModelFactory).get(WorkerViewModel.class);
        this.restaurantViewModel = ViewModelProviders.of(this, viewModelFactory).get(RestaurantViewModel.class);
    }
}
