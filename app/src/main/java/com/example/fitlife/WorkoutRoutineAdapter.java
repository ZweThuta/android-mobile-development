package com.example.fitlife;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.WorkoutRoutine;

import java.io.File;
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
        
        // Display routine image if available
        if (routine.imagePath != null && !routine.imagePath.isEmpty()) {
            File imgFile = new File(routine.imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.ivRoutineImage.setImageBitmap(bitmap);
                holder.ivRoutineImage.setVisibility(View.VISIBLE);
            } else {
                holder.ivRoutineImage.setVisibility(View.GONE);
            }
        } else {
            holder.ivRoutineImage.setVisibility(View.GONE);
        }

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

    public WorkoutRoutine getRoutineAt(int position) {
        if (position >= 0 && position < routines.size()) {
            return routines.get(position);
        }
        return null;
    }

    public void removeAt(int position) {
        if (position >= 0 && position < routines.size()) {
            routines.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void restoreItem(WorkoutRoutine routine, int position) {
        routines.add(position, routine);
        notifyItemInserted(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRoutineImage;
        TextView tvRoutineName, tvDescription;
        CheckBox cbCompleted;
        android.widget.Button btnSchedule, btnEdit, btnDelete, btnDelegate, btnComplete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoutineImage = itemView.findViewById(R.id.ivRoutineImage);
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
