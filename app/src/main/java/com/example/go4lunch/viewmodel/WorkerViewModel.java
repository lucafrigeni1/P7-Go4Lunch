package com.example.go4lunch.viewmodel;

import com.example.go4lunch.models.Worker;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class WorkerViewModel extends ViewModel {

    private final WorkerDataRepository workerDataSource;

    public WorkerViewModel(WorkerDataRepository workerDataRepository){
        this.workerDataSource = workerDataRepository;
    }

    public LiveData<List<Worker>> getWorkersList(){
        return workerDataSource.getWorkersList();
    }

    public void createWorker(Worker worker){
        workerDataSource.createWorker(worker);
    }
}
