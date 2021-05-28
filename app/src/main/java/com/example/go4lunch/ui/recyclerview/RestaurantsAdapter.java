package com.example.go4lunch.ui.recyclerview;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.go4lunch.ui.fragments.MapsFragment.latLng;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        @RequiresApi(api = Build.VERSION_CODES.O)
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


        @RequiresApi(api = Build.VERSION_CODES.O)
        private void setOpening(Restaurant restaurant) {
            getCurrentTime();
            getOpenAndCloseHours(restaurant);
            Log.e( "setOpening: ", restaurant.getName() + " / " + open);

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

                LocalTime localTime = LocalTime.of(hour, minute);
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("h").toFormatter();

                if (minute == 0){
                    opening.setText(itemView.getContext().getString(R.string.open_soon,
                            localTime.format(dtf) + meridiem));
                } else {
                    opening.setText(itemView.getContext().getString(R.string.open_soon,
                            localTime.format(dtf) + "." + minute + meridiem));
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

                LocalTime localTime = LocalTime.of(hour, minute);
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("h").toFormatter();

                if (minute == 0){
                    opening.setText(itemView.getContext().getString(R.string.already_open,
                            localTime.format(dtf) + meridiem));
                } else {
                    opening.setText(itemView.getContext().getString(R.string.already_open,
                            localTime.format(dtf) + "." + minute + meridiem));
                }
                opening.setTypeface(null, Typeface.ITALIC);
            }

            Log.e("setOpening: ", String.valueOf(openList));
            Log.e("setOpening2: ", String.valueOf(closeList));
            Log.e("open: ", String.valueOf(open));
            Log.e("close: ", String.valueOf(close));
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
