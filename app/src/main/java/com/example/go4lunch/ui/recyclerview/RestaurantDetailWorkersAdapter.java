package com.example.go4lunch.ui.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.models.Worker;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantDetailWorkersAdapter extends RecyclerView.Adapter<RestaurantDetailWorkersAdapter.RestaurantDetailWorkersViewHolder> {

    private final List<Worker> workers;

    public RestaurantDetailWorkersAdapter(final List<Worker> workers) {
        this.workers = workers;
    }

    @NonNull
    @Override
    public RestaurantDetailWorkersAdapter.RestaurantDetailWorkersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_worker, parent, false);
        return new RestaurantDetailWorkersAdapter.RestaurantDetailWorkersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantDetailWorkersAdapter.RestaurantDetailWorkersViewHolder holder, int position) {
        holder.bind(workers.get(position));
    }

    @Override
    public int getItemCount() {
        return workers.size();
    }

    public static class RestaurantDetailWorkersViewHolder extends RecyclerView.ViewHolder {

        private final ImageView picture;
        private final TextView choice;
        private final ImageView separation;

        public RestaurantDetailWorkersViewHolder(@NonNull View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.worker_image);
            choice = itemView.findViewById(R.id.worker_choice);
            separation = itemView.findViewById(R.id.separation);
        }

        public void bind(Worker worker) {
            if (worker.getPicture() != null) {
                Glide.with(picture)
                        .load(worker.getPicture())
                        .apply(RequestOptions.circleCropTransform())
                        .into(picture);
            } else
                picture.setImageResource(R.drawable.ic_baseline_circle_24);

            choice.setText(itemView.getContext().getString(R.string.join, worker.getName()));

            separation.setVisibility(View.GONE);
        }
    }
}
