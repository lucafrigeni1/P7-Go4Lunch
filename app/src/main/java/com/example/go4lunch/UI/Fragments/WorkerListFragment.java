package com.example.go4lunch.UI.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.DI.Injections;
import com.example.go4lunch.DI.ViewModelFactory;
import com.example.go4lunch.Models.Worker;
import com.example.go4lunch.R;
import com.example.go4lunch.UI.RecyclerView.WorkersAdapter;
import com.example.go4lunch.ViewModel.WorkerViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WorkerListFragment extends Fragment {

    private WorkerViewModel mWorkerViewModel;

    private RecyclerView listWorkers;
    private List<Worker> workers = new ArrayList<>();
    private final WorkersAdapter adapter = new WorkersAdapter(workers);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);


    }

    private void findViewById(){
        //listWorkers = findViewById(R.id.worker_list);
    }

    private void setRecyclerView(){
        //listWorkers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listWorkers.setAdapter(adapter);
    }

    private void setWorkerViewModel(){
        //ViewModelFactory viewModelFactory = Injections.provideViewModelFactory(this);
        //this.mWorkerViewModel = ViewModelProviders.of(this, viewModelFactory).get(WorkerViewModel.class);
    }

    private void getWorkers(){
        mWorkerViewModel.getWorkersList();
    }

}
