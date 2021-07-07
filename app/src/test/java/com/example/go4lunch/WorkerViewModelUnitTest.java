package com.example.go4lunch;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.viewmodel.WorkerDataRepository;
import com.example.go4lunch.viewmodel.WorkerViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class WorkerViewModelUnitTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    WorkerDataRepository workerDataRepository;

    WorkerViewModel workerViewModel;

    List<Worker> workerListTest = new ArrayList<>();
    List<Restaurant> restaurantListTest = new ArrayList<>();

    Worker workerTest = new Worker(
            "0",
            null,
            "luca",
            "luca@mail.com",
            null,
            null);

    Restaurant restaurantTest = new Restaurant(
            "1",
            null,
            "testName",
            null,
            "testPlace",
            null,
            null,
            null,
            0,
            null,
            0);

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        workerViewModel = new WorkerViewModel(workerDataRepository);
    }

    @Test
    public void createWorkerWithSuccess() {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        data.postValue(false);

        when(workerDataRepository.createWorker()).thenReturn(data);
        workerViewModel.createWorker().observeForever(aBoolean -> assertEquals(aBoolean, false));
    }

    @Test
    public void getCurrentUserWithSuccess() {
        MutableLiveData<Worker> data = new MutableLiveData<>();
        data.postValue(workerTest);

        when(workerDataRepository.getCurrentUser()).thenReturn(data);
        workerViewModel.getCurrentUser().observeForever(worker -> assertEquals(worker, workerTest));
    }

    @Test
    public void getWorkersListWithSuccess() {
        workerListTest.add(workerTest);
        MutableLiveData<List<Worker>> data = new MutableLiveData<>();
        data.postValue(workerListTest);

        when(workerDataRepository.getWorkersList()).thenReturn(data);
        workerViewModel.getWorkersList().observeForever(workerList -> assertEquals(workerList, workerListTest));
    }

    @Test
    public void updateWorkerChoiceWithSuccess() {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        data.postValue(false);

        when(workerDataRepository.updateWorkerChoice(restaurantTest)).thenReturn(data);
        workerViewModel.updateWorkerChoice(restaurantTest).observeForever(aBoolean -> assertEquals(aBoolean, false));
    }

    @Test
    public void updateWorkerFavoriteListWithSuccess() {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        data.postValue(false);

        restaurantListTest.add(restaurantTest);
        when(workerDataRepository.updateWorkerFavoriteList(restaurantListTest)).thenReturn(data);
        workerViewModel.updateWorkerFavoriteList(restaurantListTest).observeForever(aBoolean -> assertEquals(aBoolean, false));
    }
}