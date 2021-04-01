package com.example.go4lunch.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.di.Injections;
import com.example.go4lunch.di.ViewModelFactory;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.R;
import com.example.go4lunch.ui.recyclerview.WorkersAdapter;
import com.example.go4lunch.viewmodel.WorkerViewModel;

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
    private WorkersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_worker_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listWorkers = view.findViewById(R.id.worker_list);
        setWorkerViewModel();
        getWorkers();
    }

    private void setWorkerList(List<Worker> workers){
        adapter = new WorkersAdapter(workers);
        listWorkers.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        listWorkers.setAdapter(adapter);
    }

    private void setWorkerViewModel(){
        ViewModelFactory viewModelFactory = Injections.provideViewModelFactory(this.getContext());
        this.mWorkerViewModel = ViewModelProviders.of(this, viewModelFactory).get(WorkerViewModel.class);
    }

    private void getWorkers(){
        mWorkerViewModel.getWorkersList().observe(this, this::setWorkerList);
    }

}
