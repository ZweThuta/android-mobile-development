package com.example.fitlife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.WorkoutHistory;
import com.example.fitlife.data.WorkoutRoutine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutHistoryAdapter extends RecyclerView.Adapter<WorkoutHistoryAdapter.ViewHolder> {
    private List<WorkoutHistory> workoutHistory;
    private FitLifeDatabase database;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public WorkoutHistoryAdapter(List<WorkoutHistory> workoutHistory, FitLifeDatabase database) {
        this.workoutHistory = workoutHistory;
        this.database = database;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_workout_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutHistory history = workoutHistory.get(position);
        WorkoutRoutine routine = database.workoutRoutineDao().getRoutineById(history.routineId);
        
        if (routine != null) {
            holder.tvRoutineName.setText(routine.name);
        }
        
        holder.tvDate.setText(dateFormat.format(new Date(history.completedAt)));
        holder.tvDuration.setText("Duration: " + history.duration + " min");
        
        if (history.rating > 0) {
            StringBuilder stars = new StringBuilder();
            for (int i = 0; i < history.rating; i++) {
                stars.append("⭐");
            }
            holder.tvRating.setText("Rating: " + stars.toString());
        } else {
            holder.tvRating.setText("No rating");
        }
        
        if (history.notes != null && !history.notes.isEmpty()) {
            holder.tvNotes.setText(history.notes);
            holder.tvNotes.setVisibility(View.VISIBLE);
        } else {
            holder.tvNotes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return workoutHistory.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoutineName, tvDate, tvDuration, tvRating, tvNotes;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoutineName = itemView.findViewById(R.id.tvRoutineName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvNotes = itemView.findViewById(R.id.tvNotes);
        }
    }
}
