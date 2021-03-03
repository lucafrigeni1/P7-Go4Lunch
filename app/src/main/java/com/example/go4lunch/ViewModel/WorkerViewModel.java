package com.example.go4lunch.ViewModel;

import java.util.concurrent.Executor;

import androidx.lifecycle.ViewModel;

public class WorkerViewModel extends ViewModel {

    private final WorkerDataRepository workerDataSource;
    private final Executor mExecutor;

    public WorkerViewModel(WorkerDataRepository workerDataRepository, Executor executor){
        this.workerDataSource = workerDataRepository;
        this.mExecutor = executor;
    }
}
