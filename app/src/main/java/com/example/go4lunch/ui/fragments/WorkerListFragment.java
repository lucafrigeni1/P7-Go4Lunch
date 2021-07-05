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
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.ui.activity.MainActivity;
import com.example.go4lunch.ui.recyclerview.WorkersAdapter;
import com.example.go4lunch.viewmodel.WorkerViewModel;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WorkerListFragment extends Fragment {

    private WorkerViewModel workerviewModel;
    private RecyclerView recyclerView;
    private TextView errorText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_worker_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.worker_list);
        errorText = view.findViewById(R.id.error);
        setViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.isConnected(this.requireContext())) {
            errorText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            getWorkers();
        } else {
            errorText.setText(getText(R.string.error_no_internet));
            errorText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void getWorkers() {
        workerviewModel.getWorkersList().observe(this, this::setRecyclerView);
    }

    private void setRecyclerView(List<Worker> workers) {
        for (Worker worker : workers){
            if (worker.getRestaurant() == null){
                Restaurant restaurant = new Restaurant("",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        null,
                        0);
                worker.setRestaurant(restaurant);
            }
        }
        Collections.sort(workers,(o1, o2) -> o2.getRestaurant().getId().compareTo(o1.getRestaurant().getId()));

        WorkersAdapter adapter = new WorkersAdapter(workers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void setViewModel() {
        ViewModelFactory viewModelFactory = Injections.provideViewModelFactory();
        this.workerviewModel = ViewModelProviders.of(this, viewModelFactory).get(WorkerViewModel.class);
    }
}
