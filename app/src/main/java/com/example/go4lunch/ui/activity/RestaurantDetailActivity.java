package com.example.go4lunch.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.go4lunch.ui.recyclerview.WorkersAdapter;
import com.example.go4lunch.viewmodel.WorkerViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.go4lunch.R.*;
import static com.example.go4lunch.R.drawable.ic_rating_1;
import static com.example.go4lunch.R.drawable.ic_rating_2;

public class RestaurantDetailActivity extends AppCompatActivity {


    private WorkerViewModel workerViewModel;

    private ImageView picture;
    private ImageView rating;
    private TextView name;
    private TextView location;
    private FloatingActionButton choiceBtn;
    private ImageButton likeBtn;
    private ImageButton callBtn;
    private ImageButton websiteBtn;
    private RecyclerView recyclerView;



    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_restaurant_details);
        setWorkerViewModel();
        findViewById();
        getRestaurant();
        setView();
    }

    private void findViewById(){
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

    private void getRestaurant(){
        restaurant = getIntent().getParcelableExtra("Restaurant");
    }

    private void setView(){

        Gson gson = new Gson();

        Log.e("Restaurant:", ""+ gson.toJson(restaurant));

        Glide.with(picture)
                .load(restaurant.getPhotos())
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

        setChoiceBtn();
        setCallBtn();
        setLikeBtn();
        setWebsiteBtn();
        getWorkers();
    }

    public void setChoiceBtn(){
        choiceBtn.setOnClickListener(v -> {
            workerViewModel.updateWorkerChoice(restaurant.getName());
        });
    }

    private void setCallBtn(){
        callBtn.setOnClickListener(v -> {
            String phoneNumber = restaurant.getPhoneNumber();
            if (phoneNumber.isEmpty()){
                Toast.makeText(getApplicationContext(), "no phone number available", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(phoneNumber));
            }
        });
    }

    private void setLikeBtn(){


    }

    private void setWebsiteBtn(){
        websiteBtn.setOnClickListener(v -> {
        String url = restaurant.getWebsite();
        if (url.isEmpty()){
            Toast.makeText(getApplicationContext(), "no website available", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
        }
    });
    }


    private void getWorkers() {
        workerViewModel.getWorkersList().observe(this, this::setWorkerList);
    }

    private void setWorkerList(List<Worker> workers) {
        WorkersAdapter workersAdapter = new WorkersAdapter(workers);
        recyclerView.setAdapter(workersAdapter);
    }

    private void setWorkerViewModel() {
        ViewModelFactory viewModelFactory = Injections.provideViewModelFactory(this.getApplicationContext());
        this.workerViewModel = ViewModelProviders.of(this, viewModelFactory).get(WorkerViewModel.class);
    }
}
