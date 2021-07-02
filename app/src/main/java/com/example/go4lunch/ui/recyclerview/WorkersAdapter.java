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
import com.example.go4lunch.R;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.ui.activity.RestaurantDetailActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class WorkersAdapter extends RecyclerView.Adapter<WorkersAdapter.WorkersViewHolder> {

    private final List<Worker> workers;

    public WorkersAdapter(final List<Worker> workers) {
        this.workers = workers;
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

    public static class WorkersViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout item;
        private final ImageView picture;
        private final TextView choice;

        public WorkersViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.worker_item);
            picture = itemView.findViewById(R.id.worker_image);
            choice = itemView.findViewById(R.id.worker_choice);
        }

        public void bind(Worker worker) {
            setWorkerPicture(worker);
            setWorkerChoice(worker);
            startRestaurantDetailActivity(worker);
        }

        private void setWorkerPicture(Worker worker) {
            if (worker.getPicture() != null) {
                Glide.with(picture)
                        .load(worker.getPicture())
                        .apply(RequestOptions.circleCropTransform())
                        .into(picture);
            } else
                picture.setImageResource(R.drawable.ic_round_person_circle_24);
        }

        private void setWorkerChoice(Worker worker) {
            if (!worker.getRestaurant().getId().isEmpty()) {
                choice.setText(itemView.getContext().getString(
                        R.string.decision, worker.getName(), worker.getRestaurant().getName()));
            } else {
                choice.setText(itemView.getContext().getString(R.string.undecided, worker.getName()));
                choice.setTextColor(itemView.getContext().getResources().getColor(R.color.quantum_grey));
                choice.setTypeface(null, Typeface.ITALIC);
            }
        }

        private void startRestaurantDetailActivity(Worker worker) {
            if (!worker.getRestaurant().getId().isEmpty())
                item.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
                    intent.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT, worker.getRestaurant().getId());
                    v.getContext().startActivity(intent);
                });
        }
    }
}
