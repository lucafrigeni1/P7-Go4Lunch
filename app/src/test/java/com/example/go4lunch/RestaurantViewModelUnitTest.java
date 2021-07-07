package com.example.go4lunch;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.viewmodel.RestaurantDataRepository;
import com.example.go4lunch.viewmodel.RestaurantViewModel;

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
public class RestaurantViewModelUnitTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private RestaurantViewModel restaurantViewModel;

    @Mock
    RestaurantDataRepository restaurantDataRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        restaurantViewModel = new RestaurantViewModel(restaurantDataRepository);
    }

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

    List<Restaurant> restaurantListTest = new ArrayList<>();

    @Test
    public void getRestaurantWithSuccess() {
        MutableLiveData<Restaurant> data = new MutableLiveData<>();
        data.postValue(restaurantTest);

        when(restaurantDataRepository.getRestaurant("1")).thenReturn(data);
        restaurantViewModel.getRestaurant("1").observeForever(restaurant -> assertEquals(restaurant, restaurantTest));
    }

    @Test
    public void getRestaurantsListWithSuccess() {
        restaurantListTest.add(restaurantTest);
        MutableLiveData<List<Restaurant>> data = new MutableLiveData<>();
        data.postValue(restaurantListTest);

        when(restaurantDataRepository.getRestaurantsList()).thenReturn(data);
        restaurantViewModel.getRestaurantsList().observeForever(restaurantList -> assertEquals(restaurantList, restaurantListTest));
    }

    @Test
    public void getFilteredRestaurantsListWithSuccess() {
        restaurantListTest.add(restaurantTest);
        MutableLiveData<List<Restaurant>> data = new MutableLiveData<>();
        data.postValue(restaurantListTest);

        when(restaurantDataRepository.getFilteredRestaurantList("testName")).thenReturn(data);
        restaurantViewModel.getFilteredRestaurantsList("testName").observeForever(restaurantList -> assertEquals(restaurantList, restaurantListTest));
    }
}