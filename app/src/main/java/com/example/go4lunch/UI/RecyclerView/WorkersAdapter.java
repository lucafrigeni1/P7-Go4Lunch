package com.example.go4lunch.UI.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.go4lunch.Models.Restaurant;
import com.example.go4lunch.Models.Worker;
import com.example.go4lunch.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WorkersAdapter extends RecyclerView.Adapter<WorkersAdapter.WorkersViewHolder> {

    private List<Worker> workers;

    WorkersAdapter(final List<Worker>workers){
        this.workers = workers;
    }

    @NonNull
    @Override
    public WorkersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_worker, parent, false);
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
        Restaurant restaurant;

        public WorkersViewHolder(@NonNull View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.worker_image);
            choice = itemView.findViewById(R.id.worker_choice);
        }

        public void bind(Worker worker) {
            //picture
            choice.setText(worker.getName() + R.string.decision + restaurant.getName());
        }
    }
}
