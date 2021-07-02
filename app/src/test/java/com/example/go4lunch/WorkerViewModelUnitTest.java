package com.example.go4lunch;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.viewmodel.WorkerDataRepository;
import com.example.go4lunch.viewmodel.WorkerViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkerViewModelUnitTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private WorkerViewModel workerViewModel;

    @Mock
    WorkerDataRepository workerDataRepository;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        workerViewModel = new WorkerViewModel(workerDataRepository);
    }

    Worker workerTest = new Worker(workerDataRepository.currentUserId,
            null, "luca", "luca@mail.com", null, null);

    Restaurant restaurantTest = new Restaurant("1", null, "testName", null,
            "testPlace", null, null, null, 0,
            null, 0);


    List<Worker> workerListTest = new ArrayList<>();
    List<Restaurant> restaurantListTest = new ArrayList<>();

    @Test
    public void getCurrentUserWithSuccess() {
        workerViewModel.getCurrentUser();
        when(workerViewModel.getCurrentUser()).thenReturn(workerTest);
        workerViewModel.getCurrentUser().observeForever(worker -> {
            assertEquals(worker, workerTest);
        });
    }

    @Test
    public void getWorkersListWithSuccess() {
        workerListTest.add(workerTest);
        workerViewModel.getWorkersList();
        when(workerViewModel.getWorkersList()).thenReturn(workerListTest);
        workerViewModel.getWorkersList().observeForever(workerList -> {
            assertEquals(workerList, workerListTest);
        });
    }

    @Test
    public void createWorkerWithSuccess() {
        Boolean isCreated = false;
        workerViewModel.createWorker();
        when(workerViewModel.createWorker()).thenReturn(isCreated);
        workerViewModel.createWorker().observeForever(aBoolean -> {
            assertEquals(aBoolean, isCreated);
        });
    }

    @Test
    public void updateWorkerChoiceWithSuccess() {
        Boolean isChosen = false;
        workerViewModel.updateWorkerChoice(restaurantTest);
        when(workerViewModel.updateWorkerChoice(restaurantTest)).thenReturn(isChosen);
        workerViewModel.updateWorkerChoice(restaurantTest).observeForever(aBoolean -> {
            assertEquals(aBoolean, isChosen);
        });
    }

    @Test
    public void updateWorkerFavoriteListWithSuccess() {
        Boolean isFavorite = false;
        restaurantListTest.add(restaurantTest);
        workerViewModel.updateWorkerFavoriteList(restaurantListTest);
        when(workerViewModel.updateWorkerFavoriteList(restaurantListTest)).thenReturn(isFavorite);
        workerViewModel.updateWorkerFavoriteList(restaurantListTest).observeForever(aBoolean -> {
            assertEquals(aBoolean, isFavorite);
        });
    }
}