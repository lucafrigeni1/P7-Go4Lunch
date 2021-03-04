package com.example.go4lunch.UI.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.go4lunch.Models.Restaurant;
import com.example.go4lunch.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantsViewHolder> {

    private List<Restaurant> restaurants;
    
    RestaurantsAdapter(List<Restaurant> restaurants){
        this.restaurants = restaurants;
    }
    
    @NonNull
    @Override
    public RestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_restaurant, parent, false);
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

    public class RestaurantsViewHolder extends RecyclerView.ViewHolder {

        private final ImageView picture;
        private final TextView name;
        private final TextView distance;
        private final TextView location;
        private final TextView opening;
        private final TextView people;
        private final ImageView rating;

        public RestaurantsViewHolder(@NonNull View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.restaurant_image);
            name = itemView.findViewById(R.id.restaurant_name);
            distance = itemView.findViewById(R.id.distance);
            location = itemView.findViewById(R.id.restaurant_type_and_location);
            opening = itemView.findViewById(R.id.opening);
            people = itemView.findViewById(R.id.participants);
            rating = itemView.findViewById(R.id.star);
        }

        public void bind(Restaurant restaurant) {
            //picture
            name.setText(restaurant.getName());
            //distance
            location.setText(restaurant.getLocation());
            //opening
            people.setText(restaurant.getPeople());
            //rating
        }
    }
}
