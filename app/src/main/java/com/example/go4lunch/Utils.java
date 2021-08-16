package com.example.go4lunch;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.retrofit.Period;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class Utils {

    public static LatLng latLng;

    public static void distanceFilter(Restaurant restaurant, List<Restaurant> restaurantList){
        if (latLng != null) {
            restaurant.setDistance((int) SphericalUtil.computeDistanceBetween(latLng,
                    new LatLng(restaurant.getLocation().getLat(), restaurant.getLocation().getLng())));

            if (restaurant.getDistance() < 750) {
                restaurantList.add(restaurant);
            }
        }
    }

    public static int setRating(Restaurant restaurant) {
        double rate = restaurant.getRating();
        int stars = 0;
        if (rate >= 3.0 && rate < 4.0) {
            stars = 1;
        } else if (rate >= 4.0 && rate < 4.6) {
            stars = 2;
        } else if (rate >= 4.6) {
            stars = 3;
        }
        return stars;
    }

    public static int getCurrentTime() {
        Date date = Calendar.getInstance().getTime();
        DateFormat hourFormat = new SimpleDateFormat("HHmm", Locale.ENGLISH);
        return Integer.parseInt(hourFormat.format(date));
    }

    public static int getCurrentDay(){
        int day = 0;
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                day = 1;
                break;
            case Calendar.TUESDAY:
                day = 2;
                break;
            case Calendar.WEDNESDAY:
                day = 3;
                break;
            case Calendar.THURSDAY:
                day = 4;
                break;
            case Calendar.FRIDAY:
                day = 5;
                break;
            case Calendar.SATURDAY:
                day = 6;
                break;
            case Calendar.SUNDAY:
                day = 7;
                break;
        }
        return day;
    }

    public static int getOpenHour(Restaurant restaurant, int day, int currentTime){
        List<Integer> openList = new ArrayList<>();
        int open = 0;

        for (Period period : restaurant.getOpenHours()) {
            if (day == period.getOpen().getDay()) {
                openList.add(Integer.parseInt(period.getOpen().getTime()));
            }
        }
        Collections.sort(openList);

        if (openList.isEmpty()) {
            open = -1;
        } else if (openList.size() == 1) {
            open = openList.get(0);
        } else if (openList.size() == 2 && currentTime <= openList.get(0)) {
            open = openList.get(0);
        } else if (openList.size() == 2 && currentTime > openList.get(0) && currentTime < openList.get(1)) {
            open = openList.get(1);
        }
        return open;
    }

    public static int getCloseHour(Restaurant restaurant, int day, int currentTime) {
        List<Integer> closeList = new ArrayList<>();
        int close = 0;

        for (Period period : restaurant.getOpenHours()) {
            if (day == period.getClose().getDay()) {
                closeList.add(Integer.parseInt(period.getClose().getTime()));
            }
        }
        Collections.sort(closeList);

        if (closeList.isEmpty()) {
            close = -1;
        } else if (closeList.size() == 1) {
            close = closeList.get(0);
        } else if (closeList.size() == 2 && currentTime <= closeList.get(0)) {
            close = closeList.get(0);
        } else if (closeList.size() == 2 && currentTime > closeList.get(0) && currentTime < closeList.get(1)) {
            close = closeList.get(1);
        }
        return close;
    }

    public static String setMeridiem(int hour){
        String meridiem;
        if (hour > 11 && hour != 24){
            meridiem = "pm";
        } else {
            meridiem = "am";
        }
        return meridiem;
    }

    public static int setDisplayHour(int hour){
        if (hour >= 13) {
            return hour - 12;
        } else
            return hour;
    }
}
