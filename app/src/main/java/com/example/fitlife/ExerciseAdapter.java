package com.example.fitlife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.Exercise;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private List<Exercise> exercises;
    private CreateWorkoutRoutineActivity activity;

    public ExerciseAdapter(List<Exercise> exercises, CreateWorkoutRoutineActivity activity) {
        this.exercises = exercises;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.tvExerciseName.setText(exercise.name);
        holder.tvSetsReps.setText(exercise.sets + " sets x " + exercise.reps + " reps");
        holder.tvInstructions.setText(exercise.instructions.isEmpty() ? "No instructions" : exercise.instructions);
        holder.cbCompleted.setChecked(exercise.isCompleted);

        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            activity.markExerciseAsCompleted(exercise, isChecked);
        });

        holder.btnEdit.setOnClickListener(v -> activity.editExercise(exercise));
        holder.btnDelete.setOnClickListener(v -> activity.deleteExercise(exercise));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName, tvSetsReps, tvInstructions;
        CheckBox cbCompleted;
        android.widget.Button btnEdit, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
            tvSetsReps = itemView.findViewById(R.id.tvSetsReps);
            tvInstructions = itemView.findViewById(R.id.tvInstructions);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
