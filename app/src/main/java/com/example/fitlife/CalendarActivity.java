package com.example.fitlife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.ScheduledWorkout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private RecyclerView rvScheduledWorkouts;
    private ScheduledWorkoutAdapter adapter;
    private FitLifeDatabase database;
    private long userId;
    private List<ScheduledWorkout> scheduledWorkouts;
    private View emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        database = FitLifeDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        rvScheduledWorkouts = findViewById(R.id.rvScheduledWorkouts);
        FloatingActionButton fabSchedule = findViewById(R.id.fabSchedule);
        emptyState = findViewById(R.id.emptyState);

        scheduledWorkouts = new ArrayList<>();
        adapter = new ScheduledWorkoutAdapter(scheduledWorkouts, this, database);
        rvScheduledWorkouts.setLayoutManager(new LinearLayoutManager(this));
        rvScheduledWorkouts.setAdapter(adapter);

        // Setup swipe to delete
        setupSwipeToDelete();

        fabSchedule.setOnClickListener(v -> {
            // Show dialog to select routine to schedule
            showScheduleDialog();
        });

        loadScheduledWorkouts();
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private final ColorDrawable background = new ColorDrawable(Color.parseColor("#DC2626"));
            private final Paint textPaint = new Paint();

            {
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(48f);
                textPaint.setAntiAlias(true);
                textPaint.setTextAlign(Paint.Align.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ScheduledWorkout deletedItem = adapter.getItemAt(position);

                if (deletedItem != null) {
                    // Remove from adapter
                    adapter.removeAt(position);
                    updateEmptyState();

                    // Show snackbar with undo option
                    Snackbar snackbar = Snackbar.make(rvScheduledWorkouts,
                            "Scheduled workout deleted",
                            Snackbar.LENGTH_LONG);

                    snackbar.setAction("UNDO", v -> {
                        adapter.restoreItem(deletedItem, position);
                        updateEmptyState();
                    });

                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.setBackgroundTint(Color.parseColor("#1A1A1A"));
                    snackbar.setTextColor(Color.WHITE);

                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event != DISMISS_EVENT_ACTION) {
                                // User didn't undo, delete from database
                                ReminderHelper.cancelReminder(CalendarActivity.this, deletedItem);
                                database.scheduledWorkoutDao().delete(deletedItem.id);
                            }
                        }
                    });

                    snackbar.show();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;

                if (dX < 0) {
                    // Swiping left - show red background
                    background.setBounds(
                            itemView.getRight() + (int) dX,
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom()
                    );
                    background.draw(c);

                    // Draw delete text
                    float textY = itemView.getTop() + (itemView.getHeight() / 2f) + (textPaint.getTextSize() / 3f);
                    float textX = itemView.getRight() - 60f;

                    if (Math.abs(dX) > 150) {
                        c.drawText("Delete", textX, textY, textPaint);
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.4f;
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(rvScheduledWorkouts);
    }

    private void updateEmptyState() {
        if (scheduledWorkouts.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvScheduledWorkouts.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvScheduledWorkouts.setVisibility(View.VISIBLE);
        }
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
        updateEmptyState();
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
