package com.example.fitlife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitlife.data.FitLifeDatabase;

public class HomeActivity extends AppCompatActivity {
    private FitLifeDatabase database;
    private long userId;
    private TextView tvWorkoutCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        database = FitLifeDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        String username = prefs.getString("username", "User");
        tvWelcome.setText("Welcome, " + username + "!");

        tvWorkoutCount = findViewById(R.id.tvWorkoutCount);
        
        // Card click listeners - entire cards are clickable
        findViewById(R.id.cardMyWorkouts).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, WorkoutRoutineListActivity.class));
        });

        findViewById(R.id.cardCreateWorkout).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, CreateWorkoutRoutineActivity.class));
        });

        findViewById(R.id.cardLocations).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, LocationsActivity.class));
        });

        findViewById(R.id.cardCalendar).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, CalendarActivity.class));
        });

        findViewById(R.id.cardHistory).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, WorkoutHistoryActivity.class));
        });

        findViewById(R.id.cardRestTimer).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, RestTimerActivity.class));
        });

        findViewById(R.id.cardProgressPhotos).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProgressPhotosActivity.class));
        });

        findViewById(R.id.cardBodyMetrics).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, BodyMetricsActivity.class));
        });
        
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWorkoutCount();
    }

    private void updateWorkoutCount() {
        int count = database.workoutRoutineDao().getRoutinesByUser(userId).size();
        tvWorkoutCount.setText(count + " routine" + (count != 1 ? "s" : ""));
    }
}
