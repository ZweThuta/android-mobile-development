package com.example.fitlife;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.WorkoutHistory;
import com.example.fitlife.data.WorkoutRoutine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WorkoutHistoryActivity extends AppCompatActivity {
    private RecyclerView rvWorkoutHistory;
    private WorkoutHistoryAdapter adapter;
    private FitLifeDatabase database;
    private long userId;
    private List<WorkoutHistory> workoutHistory;
    private TextView tvTotalWorkouts, tvThisWeek, tvTotalTime, tvAverageRating;
    private View emptyState;
    private ImageView ivEmptyIcon;
    private TextView tvEmptyTitle, tvEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);

        database = FitLifeDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        tvTotalWorkouts = findViewById(R.id.tvTotalWorkouts);
        tvThisWeek = findViewById(R.id.tvThisWeek);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        rvWorkoutHistory = findViewById(R.id.rvWorkoutHistory);
        emptyState = findViewById(R.id.emptyState);
        
        if (emptyState != null) {
            ivEmptyIcon = emptyState.findViewById(R.id.ivEmptyIcon);
            tvEmptyTitle = emptyState.findViewById(R.id.tvEmptyTitle);
            tvEmptyMessage = emptyState.findViewById(R.id.tvEmptyMessage);
            
            ivEmptyIcon.setImageResource(android.R.drawable.ic_menu_recent_history);
            tvEmptyTitle.setText("No Workout History");
            tvEmptyMessage.setText("Complete your first workout to see your progress here!");
            emptyState.findViewById(R.id.btnEmptyAction).setVisibility(View.GONE);
        }

        workoutHistory = new ArrayList<>();
        adapter = new WorkoutHistoryAdapter(workoutHistory, database);
        rvWorkoutHistory.setLayoutManager(new LinearLayoutManager(this));
        rvWorkoutHistory.setAdapter(adapter);

        loadStatistics();
        loadWorkoutHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics();
        loadWorkoutHistory();
    }

    private void loadStatistics() {
        // Total workouts
        int totalWorkouts = database.workoutHistoryDao().getTotalWorkoutsCompleted(userId);
        tvTotalWorkouts.setText(String.valueOf(totalWorkouts));

        // This week
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        long weekStart = calendar.getTimeInMillis();
        
        int thisWeek = database.workoutHistoryDao().getWorkoutsCompletedSince(userId, weekStart);
        tvThisWeek.setText(String.valueOf(thisWeek));

        // Total time
        Integer totalDuration = database.workoutHistoryDao().getTotalWorkoutDuration(userId);
        if (totalDuration == null) totalDuration = 0;
        int hours = totalDuration / 60;
        int minutes = totalDuration % 60;
        if (hours > 0) {
            tvTotalTime.setText(hours + "h " + minutes + "m");
        } else {
            tvTotalTime.setText(minutes + "m");
        }

        // Average rating
        Double avgRating = database.workoutHistoryDao().getAverageRating(userId);
        if (avgRating == null || avgRating == 0) {
            tvAverageRating.setText("N/A");
        } else {
            tvAverageRating.setText(String.format(Locale.getDefault(), "%.1f", avgRating));
        }
    }

    private void loadWorkoutHistory() {
        workoutHistory.clear();
        workoutHistory.addAll(database.workoutHistoryDao().getWorkoutHistoryByUser(userId));
        adapter.notifyDataSetChanged();
        
        // Show/hide empty state
        if (emptyState != null) {
            if (workoutHistory.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
                rvWorkoutHistory.setVisibility(View.GONE);
            } else {
                emptyState.setVisibility(View.GONE);
                rvWorkoutHistory.setVisibility(View.VISIBLE);
            }
        }
    }
}
