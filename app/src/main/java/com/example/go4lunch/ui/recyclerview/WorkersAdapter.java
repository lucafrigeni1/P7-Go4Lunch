package com.example.go4lunch.ui.recyclerview;


import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.google.gson.Gson;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WorkersAdapter extends RecyclerView.Adapter<WorkersAdapter.WorkersViewHolder> {

    private final List<Worker> workers;
    private final List<Restaurant> restaurants;

    public WorkersAdapter(final List<Worker> workers, final List<Restaurant> restaurants) {
        this.workers = workers;
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public WorkersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_worker, parent, false);
        return new WorkersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkersViewHolder holder, int position) {
        holder.bind(workers.get(position));
    }

    @Override
    public int getItemCount() {
        return workers.size();
    }

    public class WorkersViewHolder extends RecyclerView.ViewHolder {

        private final ImageView picture;
        private final TextView choice;

        public WorkersViewHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.worker_image);
            choice = itemView.findViewById(R.id.worker_choice);
        }

        public void bind(Worker worker) {
            setWorkerPicture(worker);
            setWorkerChoice(worker);
        }

        private void setWorkerPicture(Worker worker) {
            if (worker.getPicture() != null) {
                Glide.with(picture)
                        .load(worker.getPicture())
                        .apply(RequestOptions.circleCropTransform())
                        .into(picture);
            } else
                picture.setImageResource(R.drawable.ic_baseline_circle_24);
        }

        private void setWorkerChoice(Worker worker) {
            if ( worker.getRestaurantId() != null && !worker.getRestaurantId().equals("")) {
                for (int i = 0; i < restaurants.size(); i++) {
                    if (worker.getRestaurantId().equals(restaurants.get(i).getId())) {
                        choice.setText(itemView.getContext().getString(R.string.decision,
                                worker.getName(),
                                restaurants.get(i).getName()));
                    }
                }
            } else{
                choice.setText(itemView.getContext().getString(R.string.undecided, worker.getName()));
                choice.setTextColor(itemView.getContext().getResources().getColor(R.color.quantum_grey));
                choice.setTypeface(null, Typeface.ITALIC);
            }
        }
    }
}
