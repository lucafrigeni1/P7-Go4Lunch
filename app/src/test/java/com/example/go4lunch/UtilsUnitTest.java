package com.example.go4lunch;

import static com.example.go4lunch.Utils.latLng;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.retrofit.Close;
import com.example.go4lunch.models.retrofit.Location;
import com.example.go4lunch.models.retrofit.Open;
import com.example.go4lunch.models.retrofit.Period;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class UtilsUnitTest {

    List<Restaurant> restaurantList = new ArrayList<>();

    int time = Utils.getCurrentTime();
    int day = Utils.getCurrentDay();

    Open open1 = new Open(day, "1200");
    Close close1 = new Close(day, "1400");
    Period period1 = new Period(close1, open1);

    Open open2A = new Open(day,"1200");
    Close close2A = new Close(day, "1400");
    Period period2A = new Period(close2A, open2A);

    Open open2B = new Open(day,"1900");
    Close close2B = new Close(day, "2100");
    Period period2B = new Period(close2B, open2B);

    Location location1 = new Location(48.2706222, 7.2791659);
    Location location2 = new Location(45.6933934, 4.9410804);

    Restaurant restaurant1 = new Restaurant(
            "0", null, "", "", "", "", "", location1, 0, null, 3
    );

    Restaurant restaurant2 = new Restaurant(
            "1", null, "", "", "", "", "", location2, 0, null, 5
    );

    @Before
    public void setup(){
        List<Period> periodList1 = new ArrayList<>();
        List<Period> periodList2 = new ArrayList<>();

        periodList1.add(period1);
        periodList2.add(period2A);
        periodList2.add(period2B);
        restaurant1.setOpenHours(periodList1);
        restaurant2.setOpenHours(periodList2);
        restaurantList.add(restaurant1);
        restaurantList.add(restaurant2);
    }

    @Test
    public void setDistanceFilterWithSuccess(){
        latLng = new LatLng(48.2706222, 7.2791659);
        List<Restaurant> testList = new ArrayList<>();

        for (Restaurant restaurant : restaurantList){
            Utils.distanceFilter(restaurant, testList);
        }

        assertTrue(testList.contains(restaurant1));
        assertFalse(testList.contains(restaurant2));
    }

    @Test
    public void setRatingWithSuccess(){
        assertEquals(1, Utils.setRating(restaurant1));
        assertEquals(3, Utils.setRating(restaurant2));
    }

    @Test
    public void getOpenHourWithSuccess(){
        assertEquals(1200, Utils.getOpenHour(restaurant1, day, time));

        if (time < 1200) {
            assertEquals(1200, Utils.getOpenHour(restaurant2, day, time));
        } else
        assertEquals(1900, Utils.getOpenHour(restaurant2, day, time));
    }

    @Test
    public void getCloseHourWithSuccess(){
        assertEquals(1400, Utils.getCloseHour(restaurant1, day, time));

        if (time < 1400) {
            assertEquals(1400, Utils.getCloseHour(restaurant2, day, time));
        } else
            assertEquals(2100, Utils.getCloseHour(restaurant2, day, time));
    }
}
