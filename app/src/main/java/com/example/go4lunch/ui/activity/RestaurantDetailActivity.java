package com.example.go4lunch.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.di.Injections;
import com.example.go4lunch.di.ViewModelFactory;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.ui.recyclerview.RestaurantDetailWorkersAdapter;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.example.go4lunch.viewmodel.WorkerViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

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

    private WorkerViewModel workerViewModel;
    private RestaurantViewModel restaurantViewModel;

    private Worker currentUser;

    private ImageView picture;
    private ImageView rating;
    private TextView name;
    private TextView location;
    private FloatingActionButton choiceBtn;
    private ImageButton likeBtn;
    private ImageButton callBtn;
    private ImageButton websiteBtn;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_restaurant_details);

        findViewById();
        setViewModel();
        getRestaurant();
    }

    private void findViewById() {
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

    private void getRestaurant() {
        String id = getIntent().getStringExtra(EXTRA_RESTAURANT);
        restaurantViewModel.getRestaurant(id).observe(this, this::setView);
    }

    private void setView(Restaurant restaurant) {
        Glide.with(picture)
                .load(restaurant.getPhotos())
                .apply(RequestOptions.centerCropTransform())
                .into(picture);

        double rate = restaurant.getRating();
        if (rate >= 3.0 && rate < 4.0) {
            rating.setImageResource(R.drawable.ic_rating_1);
        } else if (rate >= 4.0 && rate < 4.6) {
            rating.setImageResource(R.drawable.ic_rating_2);
        } else if (rate >= 4.6) {
            rating.setImageResource(R.drawable.ic_rating_3);
        }

        name.setText(restaurant.getName());
        location.setText(restaurant.getAddress());

        setChoiceBtn(restaurant);
        setCallBtn(restaurant);
        setLikeBtn(restaurant);
        setWebsiteBtn(restaurant);
        getWorkers(restaurant);
    }

    private void setChoiceBtn(Restaurant restaurant) {
        choiceBtn.setOnClickListener(v -> {
            workerViewModel.updateWorkerChoice(restaurant.getId());
        });
    }

    private void setCallBtn(Restaurant restaurant) {
        callBtn.setOnClickListener(v -> {
            String phoneNumber = restaurant.getPhoneNumber();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(getApplicationContext(), "no phone number available", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(phoneNumber));
            }
        });
    }

    private void setLikeBtn(Restaurant restaurant) {
        likeBtn.setOnClickListener(v -> {
            workerViewModel.getCurrentUser().observe(this, worker -> {
                currentUser = worker;
            });
            List<Restaurant> favoriteRestaurant = currentUser.getFavoriteRestaurant();
            if (favoriteRestaurant.contains(restaurant)) {
                favoriteRestaurant.remove(restaurant);
            } else {
                favoriteRestaurant.add(restaurant);
            }
            workerViewModel.updateWorkerFavoriteList(favoriteRestaurant);
        });
    }

    private void setWebsiteBtn(Restaurant restaurant) {
        websiteBtn.setOnClickListener(v -> {
            String url = restaurant.getWebsite();
            if (url.isEmpty()) {
                Toast.makeText(getApplicationContext(), "no website available", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
            }
        });
    }

    private void getWorkers(Restaurant restaurant) {
        workerViewModel.getWorkersList().observe(this, workers -> {
            setWorkerList(workers, restaurant);
        });
    }

    private void setWorkerList(List<Worker> workers, Restaurant restaurant) {
        List<Worker> participants = new ArrayList<>();
        for (int i = 0; i < workers.size(); i++) {
            if (workers.get(i).getRestaurantId() != null) {
                if (workers.get(i).getRestaurantId().equals(restaurant.getId())) {
                    participants.add(workers.get(i));
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
        ViewModelFactory viewModelFactory = Injections.provideViewModelFactory(this.getApplicationContext());
        this.workerViewModel = ViewModelProviders.of(this, viewModelFactory).get(WorkerViewModel.class);
        this.restaurantViewModel = ViewModelProviders.of(this, viewModelFactory).get(RestaurantViewModel.class);
    }
}
