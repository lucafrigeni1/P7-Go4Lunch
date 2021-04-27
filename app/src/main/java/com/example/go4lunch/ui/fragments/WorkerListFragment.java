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
import com.example.go4lunch.ui.recyclerview.WorkersAdapter;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.example.go4lunch.viewmodel.WorkerViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WorkerListFragment extends Fragment {

    List<Restaurant> restaurantsList = new ArrayList<>();
    private WorkerViewModel workerviewModel;
    private RestaurantViewModel restaurantViewModel;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_worker_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.worker_list);
        setViewModel();
        getWorkers();
    }

    private void getWorkers() {
        workerviewModel.getWorkersList().observe(this, this::setRecyclerView);
    }

    private void setRecyclerView(List<Worker> workers) {
        getRestaurants();
        WorkersAdapter adapter = new WorkersAdapter(workers, restaurantsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void getRestaurants() {
        restaurantViewModel.getRestaurantsList().observe(this, restaurants -> {
            restaurantsList = restaurants;
        });
    }

    //rename
    private void setViewModel() {
        ViewModelFactory viewModelFactory = Injections.provideViewModelFactory(this.getContext());
        this.workerviewModel = ViewModelProviders.of(this, viewModelFactory).get(WorkerViewModel.class);
        this.restaurantViewModel = ViewModelProviders.of(this, viewModelFactory).get(RestaurantViewModel.class);
    }
}
