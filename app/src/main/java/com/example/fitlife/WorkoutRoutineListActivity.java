package com.example.fitlife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.WorkoutRoutine;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

        // Setup swipe gestures (left to delete, right to edit)
        setupSwipeGestures();

        fabAddWorkout.setOnClickListener(v -> {
            startActivity(new Intent(WorkoutRoutineListActivity.this, CreateWorkoutRoutineActivity.class));
        });

        loadRoutines();
    }

    private void setupSwipeGestures() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private final ColorDrawable deleteBackground = new ColorDrawable(Color.parseColor("#DC2626"));
            private final ColorDrawable editBackground = new ColorDrawable(Color.parseColor("#FFFFFF"));
            private final Paint deleteTextPaint = new Paint();
            private final Paint editTextPaint = new Paint();

            {
                deleteTextPaint.setColor(Color.WHITE);
                deleteTextPaint.setTextSize(48f);
                deleteTextPaint.setAntiAlias(true);
                deleteTextPaint.setTextAlign(Paint.Align.RIGHT);

                editTextPaint.setColor(Color.parseColor("#000000"));
                editTextPaint.setTextSize(48f);
                editTextPaint.setAntiAlias(true);
                editTextPaint.setTextAlign(Paint.Align.LEFT);
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
                WorkoutRoutine routine = adapter.getRoutineAt(position);
                
                if (routine != null) {
                    if (direction == ItemTouchHelper.LEFT) {
                        // Swipe left - Delete
                        adapter.removeAt(position);
                        updateEmptyState();

                        Snackbar snackbar = Snackbar.make(rvWorkoutRoutines, 
                                "\"" + routine.name + "\" deleted", 
                                Snackbar.LENGTH_LONG);
                        
                        snackbar.setAction("UNDO", v -> {
                            adapter.restoreItem(routine, position);
                            updateEmptyState();
                        });
                        
                        snackbar.setActionTextColor(Color.WHITE);
                        snackbar.setBackgroundTint(Color.parseColor("#1A1A1A"));
                        snackbar.setTextColor(Color.WHITE);
                        
                        snackbar.addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (event != DISMISS_EVENT_ACTION) {
                                    database.workoutRoutineDao().delete(routine.id);
                                }
                            }
                        });
                        
                        snackbar.show();
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        // Swipe right - Edit
                        adapter.notifyItemChanged(position); // Reset the swipe
                        editRoutine(routine);
                    }
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, 
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                
                View itemView = viewHolder.itemView;
                
                if (dX < 0) {
                    // Swiping left - show red delete background
                    deleteBackground.setBounds(
                            itemView.getRight() + (int) dX,
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom()
                    );
                    deleteBackground.draw(c);

                    float textY = itemView.getTop() + (itemView.getHeight() / 2f) + (deleteTextPaint.getTextSize() / 3f);
                    float textX = itemView.getRight() - 60f;
                    
                    if (Math.abs(dX) > 150) {
                        c.drawText("Delete", textX, textY, deleteTextPaint);
                    }
                } else if (dX > 0) {
                    // Swiping right - show white edit background
                    editBackground.setBounds(
                            itemView.getLeft(),
                            itemView.getTop(),
                            itemView.getLeft() + (int) dX,
                            itemView.getBottom()
                    );
                    editBackground.draw(c);

                    float textY = itemView.getTop() + (itemView.getHeight() / 2f) + (editTextPaint.getTextSize() / 3f);
                    float textX = itemView.getLeft() + 60f;
                    
                    if (dX > 150) {
                        c.drawText("Edit", textX, textY, editTextPaint);
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
        itemTouchHelper.attachToRecyclerView(rvWorkoutRoutines);
    }

    private void updateEmptyState() {
        if (routines.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvWorkoutRoutines.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvWorkoutRoutines.setVisibility(View.VISIBLE);
        }
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
        updateEmptyState();
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
