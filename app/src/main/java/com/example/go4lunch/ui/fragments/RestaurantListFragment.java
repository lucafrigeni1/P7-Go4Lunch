package com.example.go4lunch.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.go4lunch.R;
import com.example.go4lunch.di.Injections;
import com.example.go4lunch.di.ViewModelFactory;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.ui.activity.MainActivity;
import com.example.go4lunch.ui.recyclerview.RestaurantsAdapter;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.example.go4lunch.viewmodel.WorkerDataRepository;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantListFragment extends Fragment {

    private RestaurantViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView errorText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.restaurant_list);
        errorText = view.findViewById(R.id.error);
        setRestaurantViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.isConnected(this.requireContext())) {
            errorText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            getRestaurants();
        } else {
            errorText.setText(getText(R.string.error_no_internet));
            errorText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void getRestaurants() {
        viewModel.getRestaurantsList().observe(this, this::setRestaurantList);
    }

    public void setRestaurantList(List<Restaurant> restaurants) {
        if (isVisible()){
            if (WorkerDataRepository.latLng == null){
                errorText.setText(getText(R.string.error_location));
                errorText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                if (restaurants.isEmpty()) {
                    errorText.setText(getText(R.string.no_restaurant_found));
                    errorText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    errorText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Collections.sort(restaurants, new Restaurant.RestaurantComparator());
                    RestaurantsAdapter adapter = new RestaurantsAdapter(restaurants);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(adapter);
                }
            }
        }
    }

    private void setRestaurantViewModel() {
        ViewModelFactory viewModelFactory = Injections.provideViewModelFactory();
        this.viewModel = ViewModelProviders.of(this, viewModelFactory).get(RestaurantViewModel.class);
    }
}
