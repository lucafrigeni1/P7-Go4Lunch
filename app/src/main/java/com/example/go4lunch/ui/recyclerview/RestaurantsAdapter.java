package com.example.go4lunch.ui.recyclerview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.di.Injections;
import com.example.go4lunch.di.ViewModelFactory;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.R;
import com.example.go4lunch.ui.activity.RestaurantDetailActivity;
import com.example.go4lunch.viewmodel.RestaurantViewModel;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
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

            item =  itemView.findViewById(R.id.item);
            picture = itemView.findViewById(R.id.restaurant_image);
            name = itemView.findViewById(R.id.restaurant_name);
            distance = itemView.findViewById(R.id.distance);
            location = itemView.findViewById(R.id.restaurant_type_and_location);
            opening = itemView.findViewById(R.id.opening);
            people = itemView.findViewById(R.id.participants_number);
            rating = itemView.findViewById(R.id.star);
        }

        public void bind(Restaurant restaurant) {
            item.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
                intent.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT, restaurant.getId());
                v.getContext().startActivity(intent);
            });


                Glide.with(picture)
                        .load(restaurant.getPhotos())
                        .apply(RequestOptions.centerCropTransform())
                        .into(picture);
                Log.e("bind: ", restaurant.getName() + " " + restaurant.getPhotos());

            name.setText(restaurant.getName());
            distance.setText("0" + "m");
            location.setText(restaurant.getAddress());
            opening.setText("0");
            people.setText("(" +  restaurant.getWorkerList().size() + ")");

            double rate = restaurant.getRating();
            if (rate >= 3.0 && rate < 4.0) {
                rating.setImageResource(R.drawable.ic_rating_1);
            } else if (rate >= 4.0 && rate < 4.6) {
                rating.setImageResource(R.drawable.ic_rating_2);
            } else if (rate >= 4.6) {
                rating.setImageResource(R.drawable.ic_rating_3);
            }
        }
    }

}
