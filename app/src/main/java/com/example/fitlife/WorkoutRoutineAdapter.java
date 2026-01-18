package com.example.fitlife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.WorkoutRoutine;

import java.util.List;

public class WorkoutRoutineAdapter extends RecyclerView.Adapter<WorkoutRoutineAdapter.ViewHolder> {
    private List<WorkoutRoutine> routines;
    private WorkoutRoutineListActivity activity;

    public WorkoutRoutineAdapter(List<WorkoutRoutine> routines, WorkoutRoutineListActivity activity) {
        this.routines = routines;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_workout_routine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutRoutine routine = routines.get(position);
        holder.tvRoutineName.setText(routine.name);
        holder.tvDescription.setText(routine.description.isEmpty() ? "No description" : routine.description);
        holder.cbCompleted.setChecked(routine.isCompleted);

        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            activity.markAsCompleted(routine, isChecked);
        });

        holder.btnSchedule.setOnClickListener(v -> activity.scheduleRoutine(routine));
        holder.btnEdit.setOnClickListener(v -> activity.editRoutine(routine));
        holder.btnDelete.setOnClickListener(v -> activity.deleteRoutine(routine));
        holder.btnDelegate.setOnClickListener(v -> activity.delegateRoutine(routine));
        holder.btnComplete.setOnClickListener(v -> activity.completeWorkout(routine));
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoutineName, tvDescription;
        CheckBox cbCompleted;
        android.widget.Button btnSchedule, btnEdit, btnDelete, btnDelegate, btnComplete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoutineName = itemView.findViewById(R.id.tvRoutineName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);
            btnSchedule = itemView.findViewById(R.id.btnSchedule);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDelegate = itemView.findViewById(R.id.btnDelegate);
            btnComplete = itemView.findViewById(R.id.btnComplete);
        }
    }
}
