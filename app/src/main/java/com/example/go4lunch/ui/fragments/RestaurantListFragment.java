package com.example.go4lunch.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.di.Injections;
import com.example.go4lunch.di.ViewModelFactory;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.ui.recyclerview.RestaurantsAdapter;
import com.example.go4lunch.ui.recyclerview.WorkersAdapter;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.example.go4lunch.viewmodel.WorkerViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantListFragment extends Fragment {

    private RestaurantViewModel restaurantViewModel;

    private RecyclerView listRestaurants;
    private RestaurantsAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listRestaurants = view.findViewById(R.id.restaurant_list);
        setRestaurantViewModel();
        getRestaurants();
    }

    private void setRestaurantList(List<Restaurant> restaurants){
        adapter = new RestaurantsAdapter(restaurants);
        listRestaurants.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        listRestaurants.setAdapter(adapter);
    }

    private void setRestaurantViewModel(){
        ViewModelFactory viewModelFactory = Injections.provideViewModelFactory(this.getContext());
        this.restaurantViewModel = ViewModelProviders.of(this, viewModelFactory).get(RestaurantViewModel.class);
    }

    private void getRestaurants(){
        restaurantViewModel.getRestaurantsList().observe(this, this::setRestaurantList);
    }

}
