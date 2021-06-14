package com.example.go4lunch.ui.recyclerview;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.R;
import com.example.go4lunch.models.retrofit.Period;
import com.example.go4lunch.ui.activity.RestaurantDetailActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantsViewHolder> {

    private final List<Restaurant> restaurants;
    
    public RestaurantsAdapter(List<Restaurant> restaurants){
        this.restaurants = restaurants;
    }
    
    @NonNull
    @Override
    public RestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantsViewHolder holder, int position) {
        holder.bind(restaurants.get(position));
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public static class RestaurantsViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout item;
        private final ImageView picture;
        private final TextView name;
        private final TextView distance;
        private final TextView location;
        private final TextView opening;
        private final TextView people;
        private final ImageView rating;

        int currentTime;
        int day;
        List<Integer> openList;
        List<Integer> closeList;
        int open;
        int close;

        public RestaurantsViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.restaurant_item);
            picture = itemView.findViewById(R.id.restaurant_image);
            name = itemView.findViewById(R.id.restaurant_name);
            distance = itemView.findViewById(R.id.distance);
            location = itemView.findViewById(R.id.restaurant_type_and_location);
            opening = itemView.findViewById(R.id.opening);
            people = itemView.findViewById(R.id.participants_number);
            rating = itemView.findViewById(R.id.star);
        }

        public void bind(Restaurant restaurant) {
            setMainInformation(restaurant);
            setOpening(restaurant);
            setDistance(restaurant);
            setParticipants(restaurant);
            setRating(restaurant);
            startRestaurantDetailActivity(restaurant);
        }

        private void setMainInformation(Restaurant restaurant) {
            name.setText(restaurant.getName());
            location.setText(restaurant.getAddress());

            Glide.with(picture)
                    .load(restaurant.getPhotos())
                    .apply(RequestOptions.centerCropTransform())
                    .into(picture);
        }

        private void setOpening(Restaurant restaurant) {
            getCurrentTime();
            getOpenAndCloseHours(restaurant);

            if (open == 0 && close == -1) {
                opening.setText(R.string.always_open);
                opening.setTypeface(null, Typeface.ITALIC);
            } else if (open == -1 && close == -1 || close < currentTime) {
                opening.setText(R.string.closed);
                opening.setTextColor(itemView.getContext().getResources().getColor(R.color.quantum_googredA700));
                opening.setTypeface(null, Typeface.BOLD);
            } else if ((close - currentTime) < 30) {
                opening.setText(R.string.closing_soon);
                opening.setTextColor(itemView.getContext().getResources().getColor(R.color.quantum_googredA700));
                opening.setTypeface(null, Typeface.BOLD);
            } else if (currentTime < open && open < close) {
                int hour = open/100;
                int minute = (open - hour * 100) % 60;

                String meridiem;
                if (hour > 11 && hour != 24){
                    meridiem = "pm";
                } else {
                    meridiem = "am";
                }

                int displayHour = setDisplayHour(hour);

                if (minute == 0){
                    opening.setText(itemView.getContext().getString(R.string.open_soon,
                            displayHour + meridiem));
                } else {
                    opening.setText(itemView.getContext().getString(R.string.open_soon,
                            displayHour + "." + minute + meridiem));
                }
                opening.setTypeface(null, Typeface.ITALIC);
            } else {
                int hour = close/100;
                int minute = (close - hour * 100) % 60;

                String meridiem;
                if (hour > 11 && hour != 24){
                    meridiem = "pm";
                } else {
                    meridiem = "am";
                }

                int displayHour = setDisplayHour(hour);

                if (minute == 0){
                    opening.setText(itemView.getContext().getString(R.string.already_open,
                            displayHour + meridiem));
                } else {
                    opening.setText(itemView.getContext().getString(R.string.already_open,
                            displayHour + "." + minute + meridiem));
                }
                opening.setTypeface(null, Typeface.ITALIC);
            }
        }

        private int setDisplayHour(int hour){
            if (hour == 13){
                hour = 1;
            } else if (hour == 14){
                hour = 2;
            } else if (hour == 15){
                hour = 3;
            } else if (hour == 16){
                hour = 4;
            } else if (hour == 17){
                hour = 5;
            } else if (hour == 18){
                hour = 6;
            } else if (hour == 19){
                hour = 7;
            } else if (hour == 20){
                hour = 8;
            } else if (hour == 21){
                hour = 9;
            } else if (hour == 22){
                hour = 10;
            } else if (hour == 23){
                hour = 11;
            } else if (hour == 24){
                hour = 12;
            }
            return hour;
        }

        private void getCurrentTime() {
            Date date = Calendar.getInstance().getTime();
            DateFormat hourFormat = new SimpleDateFormat("HHmm", Locale.ENGLISH);
            currentTime = Integer.parseInt(hourFormat.format(date));

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
        }

        private void getOpenAndCloseHours(Restaurant restaurant) {
            openList = new ArrayList<>();
            closeList = new ArrayList<>();

            for (Period period : restaurant.getOpenHours()) {
                if (day == period.getOpen().getDay()) {
                    openList.add(Integer.parseInt(period.getOpen().getTime()));
                }

                if (day == period.getClose().getDay()) {
                    closeList.add(Integer.parseInt(period.getClose().getTime()));
                }
            }

            Collections.sort(openList);
            Collections.sort(closeList);

            if (openList.isEmpty()) {
                open = -1;
            } else if (openList.size() == 1) {
                open = openList.get(0);
            } else if (openList.size() == 2 && currentTime <= openList.get(0)) {
                open = openList.get(0);
            } else if (openList.size() == 2 && currentTime > openList.get(0) && currentTime < openList.get(1)) {
                open = openList.get(1);
            }

            if (closeList.isEmpty()) {
                close = -1;
            } else if (closeList.size() == 1) {
                close = closeList.get(0);
            } else if (closeList.size() == 2 && currentTime <= closeList.get(0)) {
                close = closeList.get(0);
            } else if (closeList.size() == 2 && currentTime > closeList.get(0) && currentTime < closeList.get(1)) {
                close = closeList.get(1);
            }
        }

        private void setDistance(Restaurant restaurant) {
            distance.setText(itemView.getContext().getString(R.string.distance,
                    String.valueOf(restaurant.getDistance())));
        }

        private void setParticipants(Restaurant restaurant) {
            people.setText(itemView.getContext().getString(R.string.participants,
                    String.valueOf(restaurant.getWorkerList().size())));
        }

        private void setRating(Restaurant restaurant) {
            double rate = restaurant.getRating();
            if (rate >= 3.0 && rate < 4.0) {
                rating.setImageResource(R.drawable.ic_rating_1);
            } else if (rate >= 4.0 && rate < 4.6) {
                rating.setImageResource(R.drawable.ic_rating_2);
            } else if (rate >= 4.6) {
                rating.setImageResource(R.drawable.ic_rating_3);
            }
        }

        private void startRestaurantDetailActivity(Restaurant restaurant) {
            item.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
                intent.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT, restaurant.getId());
                v.getContext().startActivity(intent);
            });
        }
    }
}
