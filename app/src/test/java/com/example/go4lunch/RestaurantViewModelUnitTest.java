package com.example.go4lunch;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.viewmodel.RestaurantDataRepository;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.example.go4lunch.viewmodel.WorkerDataRepository;
import com.example.go4lunch.viewmodel.WorkerViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantViewModelUnitTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private RestaurantViewModel restaurantViewModel;

    @Mock
    RestaurantDataRepository restaurantDataRepository;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        restaurantViewModel = new RestaurantViewModel(restaurantDataRepository);
    }

    Restaurant restaurantTest = new Restaurant("1", null, "testName", null,
            "testPlace", null, null, null, 0, null, 0);

    List<Restaurant> restaurantListTest = new ArrayList<>();

    @Test
    public void getPlacesWithSuccess() {
        restaurantViewModel.getPlaces(48.2708084,7.2782985);
    }

    @Test
    public void getRestaurantWithSuccess() {
        restaurantViewModel.getRestaurant("1");
        when(restaurantViewModel.getRestaurant("1")).thenReturn(restaurantTest);
        restaurantViewModel.getRestaurant("1").observeForever(restaurant -> {
            assertEquals(restaurant, restaurantTest);
        });
    }

    @Test
    public void getRestaurantsListWithSuccess() {
        restaurantListTest.add(restaurantTest);
        restaurantViewModel.getRestaurantsList();
        when(restaurantViewModel.getRestaurantsList()).thenReturn(restaurantListTest);
        restaurantViewModel.getRestaurantsList().observeForever(restaurantList -> {
            assertEquals(restaurantList, restaurantListTest);
        });
    }

    @Test
    public void getFilteredRestaurantsListWithSuccess() {
        restaurantListTest.add(restaurantTest);
        restaurantViewModel.getFilteredRestaurantsList("testName");
        when(restaurantViewModel.getFilteredRestaurantsList("testName")).thenReturn(restaurantListTest);
        restaurantViewModel.getFilteredRestaurantsList("testName").observeForever(restaurantList -> {
            assertEquals(restaurantList, restaurantListTest);
        });
    }
}