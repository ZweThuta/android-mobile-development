package com.example.fitlife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.ScheduledWorkout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private RecyclerView rvScheduledWorkouts;
    private ScheduledWorkoutAdapter adapter;
    private FitLifeDatabase database;
    private long userId;
    private List<ScheduledWorkout> scheduledWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        database = FitLifeDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        rvScheduledWorkouts = findViewById(R.id.rvScheduledWorkouts);
        FloatingActionButton fabSchedule = findViewById(R.id.fabSchedule);

        scheduledWorkouts = new ArrayList<>();
        adapter = new ScheduledWorkoutAdapter(scheduledWorkouts, this, database);
        rvScheduledWorkouts.setLayoutManager(new LinearLayoutManager(this));
        rvScheduledWorkouts.setAdapter(adapter);

        fabSchedule.setOnClickListener(v -> {
            // Show dialog to select routine to schedule
            showScheduleDialog();
        });

        loadScheduledWorkouts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadScheduledWorkouts();
    }

    private void loadScheduledWorkouts() {
        scheduledWorkouts.clear();
        scheduledWorkouts.addAll(database.scheduledWorkoutDao().getScheduledWorkoutsByUser(userId));
        adapter.notifyDataSetChanged();
    }

    private void showScheduleDialog() {
        List<com.example.fitlife.data.WorkoutRoutine> routines = database.workoutRoutineDao().getRoutinesByUser(userId);
        if (routines.isEmpty()) {
            Toast.makeText(this, "Please create a workout routine first", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] routineNames = new String[routines.size()];
        for (int i = 0; i < routines.size(); i++) {
            routineNames[i] = routines.get(i).name;
        }

        new AlertDialog.Builder(this)
            .setTitle("Select Workout to Schedule")
            .setItems(routineNames, (dialog, which) -> {
                Intent intent = new Intent(CalendarActivity.this, ScheduleWorkoutActivity.class);
                intent.putExtra("routineId", routines.get(which).id);
                startActivity(intent);
            })
            .show();
    }

    public void markAsComplete(ScheduledWorkout scheduledWorkout) {
        scheduledWorkout.status = "completed";
        database.scheduledWorkoutDao().updateStatus(scheduledWorkout.id, "completed");
        
        // Cancel reminder
        ReminderHelper.cancelReminder(this, scheduledWorkout);
        
        // Create workout history entry
        long completedAt = System.currentTimeMillis();
        com.example.fitlife.data.WorkoutHistory history = new com.example.fitlife.data.WorkoutHistory(
            userId, scheduledWorkout.routineId, completedAt
        );
        database.workoutHistoryDao().insert(history);
        
        loadScheduledWorkouts();
        Toast.makeText(this, "Workout marked as complete!", Toast.LENGTH_SHORT).show();
    }

    public void cancelScheduledWorkout(ScheduledWorkout scheduledWorkout) {
        new AlertDialog.Builder(this)
            .setTitle("Cancel Workout")
            .setMessage("Are you sure you want to cancel this scheduled workout?")
            .setPositiveButton("Cancel Workout", (dialog, which) -> {
                scheduledWorkout.status = "cancelled";
                database.scheduledWorkoutDao().updateStatus(scheduledWorkout.id, "cancelled");
                // Cancel reminder
                ReminderHelper.cancelReminder(this, scheduledWorkout);
                loadScheduledWorkouts();
                Toast.makeText(this, "Workout cancelled", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Keep", null)
            .show();
    }
}
