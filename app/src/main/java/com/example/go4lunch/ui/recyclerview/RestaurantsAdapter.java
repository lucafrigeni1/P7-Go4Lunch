package com.example.go4lunch.ui.recyclerview;

import static com.example.go4lunch.Utils.getCloseHour;
import static com.example.go4lunch.Utils.getCurrentDay;
import static com.example.go4lunch.Utils.getCurrentTime;
import static com.example.go4lunch.Utils.setDisplayHour;
import static com.example.go4lunch.Utils.getOpenHour;
import static com.example.go4lunch.Utils.setMeridiem;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.Utils;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.R;
import com.example.go4lunch.ui.activity.RestaurantDetailActivity;

import java.util.List;

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
            setView(restaurant);
            startRestaurantDetailActivity(restaurant);
        }

        private void setView(Restaurant restaurant) {
            Glide.with(picture)
                    .load(restaurant.getPhotos())
                    .apply(RequestOptions.centerCropTransform())
                    .into(picture);

            name.setText(restaurant.getName());
            distance.setText(itemView.getContext().getString(R.string.distance,
                    String.valueOf(restaurant.getDistance())));
            location.setText(restaurant.getAddress());

            setOpening(restaurant);

            people.setText(itemView.getContext().getString(R.string.participants,
                    String.valueOf(restaurant.getWorkerList().size())));

            if (Utils.setRating(restaurant) == 1){
                rating.setImageResource(R.drawable.ic_rating_1);
            } else if (Utils.setRating(restaurant) == 2){
                rating.setImageResource(R.drawable.ic_rating_2);
            } else if (Utils.setRating(restaurant) == 3){
                rating.setImageResource(R.drawable.ic_rating_3);
            }
        }

        private void setOpening(Restaurant restaurant) {
            int currentTime = getCurrentTime();
            int open = getOpenHour(restaurant, getCurrentDay(), currentTime);
            int close = getCloseHour(restaurant, getCurrentDay(), currentTime);

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
                if (minute == 0){
                    opening.setText(itemView.getContext().getString(R.string.open_soon,
                            setDisplayHour(hour) + setMeridiem(hour)));
                } else {
                    opening.setText(itemView.getContext().getString(R.string.open_soon,
                            setDisplayHour(hour) + "." + minute + setMeridiem(hour)));
                }
                opening.setTypeface(null, Typeface.ITALIC);
            } else {
                int hour = close/100;
                int minute = (close - hour * 100) % 60;
                if (minute == 0){
                    opening.setText(itemView.getContext().getString(R.string.already_open,
                            setDisplayHour(hour) + setMeridiem(hour)));
                } else {
                    opening.setText(itemView.getContext().getString(R.string.already_open,
                            setDisplayHour(hour) + "." + minute + setMeridiem(hour)));
                }
                opening.setTypeface(null, Typeface.ITALIC);
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
