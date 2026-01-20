package com.example.fitlife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.ScheduledWorkout;
import com.example.fitlife.data.WorkoutRoutine;

import java.util.List;

public class ScheduledWorkoutAdapter extends RecyclerView.Adapter<ScheduledWorkoutAdapter.ViewHolder> {
    private List<ScheduledWorkout> scheduledWorkouts;
    private CalendarActivity activity;
    private FitLifeDatabase database;

    public ScheduledWorkoutAdapter(List<ScheduledWorkout> scheduledWorkouts, CalendarActivity activity, FitLifeDatabase database) {
        this.scheduledWorkouts = scheduledWorkouts;
        this.activity = activity;
        this.database = database;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_scheduled_workout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduledWorkout scheduledWorkout = scheduledWorkouts.get(position);
        WorkoutRoutine routine = database.workoutRoutineDao().getRoutineById(scheduledWorkout.routineId);
        
        if (routine != null) {
            holder.tvRoutineName.setText(routine.name);
        }
        
        holder.tvDateTime.setText(scheduledWorkout.scheduledDate + " at " + scheduledWorkout.scheduledTime);
        holder.tvStatus.setText(scheduledWorkout.status.toUpperCase());
        
        if (scheduledWorkout.reminderTime > 0) {
            holder.tvReminder.setText("Reminder: " + scheduledWorkout.reminderTime + " min before");
        } else {
            holder.tvReminder.setText("No reminder");
        }

        if ("completed".equals(scheduledWorkout.status) || "cancelled".equals(scheduledWorkout.status)) {
            holder.btnComplete.setEnabled(false);
            holder.btnCancel.setEnabled(false);
        } else {
            holder.btnComplete.setOnClickListener(v -> activity.markAsComplete(scheduledWorkout));
            holder.btnCancel.setOnClickListener(v -> activity.cancelScheduledWorkout(scheduledWorkout));
        }
    }

    @Override
    public int getItemCount() {
        return scheduledWorkouts.size();
    }

    public ScheduledWorkout getItemAt(int position) {
        if (position >= 0 && position < scheduledWorkouts.size()) {
            return scheduledWorkouts.get(position);
        }
        return null;
    }

    public void removeAt(int position) {
        if (position >= 0 && position < scheduledWorkouts.size()) {
            scheduledWorkouts.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void restoreItem(ScheduledWorkout item, int position) {
        scheduledWorkouts.add(position, item);
        notifyItemInserted(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoutineName, tvDateTime, tvStatus, tvReminder;
        android.widget.Button btnComplete, btnCancel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoutineName = itemView.findViewById(R.id.tvRoutineName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvReminder = itemView.findViewById(R.id.tvReminder);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
