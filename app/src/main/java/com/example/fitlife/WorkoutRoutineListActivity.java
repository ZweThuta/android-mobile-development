package com.example.fitlife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.WorkoutRoutine;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class WorkoutRoutineListActivity extends AppCompatActivity {
    private RecyclerView rvWorkoutRoutines;
    private WorkoutRoutineAdapter adapter;
    private FitLifeDatabase database;
    private long userId;
    private List<WorkoutRoutine> routines;
    private View emptyState;
    private ImageView ivEmptyIcon;
    private TextView tvEmptyTitle, tvEmptyMessage;
    private MaterialButton btnEmptyAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_routine_list);

        database = FitLifeDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        rvWorkoutRoutines = findViewById(R.id.rvWorkoutRoutines);
        FloatingActionButton fabAddWorkout = findViewById(R.id.fabAddWorkout);
        emptyState = findViewById(R.id.emptyState);
        ivEmptyIcon = emptyState.findViewById(R.id.ivEmptyIcon);
        tvEmptyTitle = emptyState.findViewById(R.id.tvEmptyTitle);
        tvEmptyMessage = emptyState.findViewById(R.id.tvEmptyMessage);
        btnEmptyAction = emptyState.findViewById(R.id.btnEmptyAction);

        // Configure empty state
        ivEmptyIcon.setImageResource(android.R.drawable.ic_menu_view);
        tvEmptyTitle.setText("No Workouts Yet");
        tvEmptyMessage.setText("Create your first workout routine to get started on your fitness journey!");
        btnEmptyAction.setText("Create Workout");
        btnEmptyAction.setVisibility(View.VISIBLE);
        btnEmptyAction.setOnClickListener(v -> {
            startActivity(new Intent(WorkoutRoutineListActivity.this, CreateWorkoutRoutineActivity.class));
        });

        routines = new ArrayList<>();
        adapter = new WorkoutRoutineAdapter(routines, this);
        rvWorkoutRoutines.setLayoutManager(new LinearLayoutManager(this));
        rvWorkoutRoutines.setAdapter(adapter);

        fabAddWorkout.setOnClickListener(v -> {
            startActivity(new Intent(WorkoutRoutineListActivity.this, CreateWorkoutRoutineActivity.class));
        });

        loadRoutines();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoutines();
    }

    private void loadRoutines() {
        routines.clear();
        routines.addAll(database.workoutRoutineDao().getRoutinesByUser(userId));
        adapter.notifyDataSetChanged();
        
        // Show/hide empty state
        if (routines.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvWorkoutRoutines.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvWorkoutRoutines.setVisibility(View.VISIBLE);
        }
    }

    public void deleteRoutine(WorkoutRoutine routine) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Workout")
            .setMessage("Are you sure you want to delete " + routine.name + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                database.workoutRoutineDao().delete(routine.id);
                loadRoutines();
                Toast.makeText(this, "Workout deleted", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    public void editRoutine(WorkoutRoutine routine) {
        Intent intent = new Intent(this, CreateWorkoutRoutineActivity.class);
        intent.putExtra("routineId", routine.id);
        startActivity(intent);
    }

    public void markAsCompleted(WorkoutRoutine routine, boolean isCompleted) {
        routine.isCompleted = isCompleted;
        database.workoutRoutineDao().markAsCompleted(routine.id, isCompleted);
        adapter.notifyDataSetChanged();
    }

    public void delegateRoutine(WorkoutRoutine routine) {
        Intent intent = new Intent(this, DelegateActivity.class);
        intent.putExtra("routineId", routine.id);
        startActivity(intent);
    }

    public void scheduleRoutine(WorkoutRoutine routine) {
        Intent intent = new Intent(this, ScheduleWorkoutActivity.class);
        intent.putExtra("routineId", routine.id);
        startActivity(intent);
    }

    public void completeWorkout(WorkoutRoutine routine) {
        // Mark routine as completed
        routine.isCompleted = true;
        database.workoutRoutineDao().markAsCompleted(routine.id, true);
        
        // Create workout history entry
        long completedAt = System.currentTimeMillis();
        com.example.fitlife.data.WorkoutHistory history = new com.example.fitlife.data.WorkoutHistory(
            userId, routine.id, completedAt
        );
        
        // Count completed exercises
        int exercisesCompleted = 0;
        for (com.example.fitlife.data.Exercise exercise : database.exerciseDao().getExercisesByRoutine(routine.id)) {
            if (exercise.isCompleted) {
                exercisesCompleted++;
            }
        }
        history.exercisesCompleted = exercisesCompleted;
        
        database.workoutHistoryDao().insert(history);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Workout completed! Added to history.", Toast.LENGTH_SHORT).show();
    }
}
