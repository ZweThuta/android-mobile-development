package com.example.fitlife;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.Equipment;
import com.example.fitlife.data.Exercise;
import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.WorkoutLocation;
import com.example.fitlife.data.WorkoutRoutine;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CreateWorkoutRoutineActivity extends AppCompatActivity {
    private TextInputEditText etRoutineName, etDescription;
    private RecyclerView rvExercises, rvEquipment;
    private ExerciseAdapter exerciseAdapter;
    private EquipmentAdapter equipmentAdapter;
    private FitLifeDatabase database;
    private long userId;
    private long routineId = -1;
    private List<Exercise> exercises;
    private List<Equipment> equipmentList;
    private Long selectedLocationId;
    private TextView tvSelectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout_routine);

        database = FitLifeDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        routineId = getIntent().getLongExtra("routineId", -1);

        etRoutineName = findViewById(R.id.etRoutineName);
        etDescription = findViewById(R.id.etDescription);
        rvExercises = findViewById(R.id.rvExercises);
        rvEquipment = findViewById(R.id.rvEquipment);
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);

        exercises = new ArrayList<>();
        equipmentList = new ArrayList<>();

        exerciseAdapter = new ExerciseAdapter(exercises, this);
        equipmentAdapter = new EquipmentAdapter(equipmentList, this);

        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        rvExercises.setAdapter(exerciseAdapter);

        rvEquipment.setLayoutManager(new LinearLayoutManager(this));
        rvEquipment.setAdapter(equipmentAdapter);

        Button btnAddExercise = findViewById(R.id.btnAddExercise);
        Button btnAddEquipment = findViewById(R.id.btnAddEquipment);
        Button btnSelectLocation = findViewById(R.id.btnSelectLocation);
        Button btnSave = findViewById(R.id.btnSave);

        btnAddExercise.setOnClickListener(v -> showAddExerciseDialog());
        btnAddEquipment.setOnClickListener(v -> showAddEquipmentDialog());
        btnSelectLocation.setOnClickListener(v -> {
            Intent intent = new Intent(this, LocationsActivity.class);
            intent.putExtra("selectMode", true);
            startActivityForResult(intent, 100);
        });
        btnSave.setOnClickListener(v -> saveWorkoutRoutine());

        if (routineId != -1) {
            loadWorkoutRoutine();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedLocationId = data.getLongExtra("locationId", -1);
            if (selectedLocationId != -1) {
                WorkoutLocation location = database.workoutLocationDao().getLocationById(selectedLocationId);
                if (location != null) {
                    tvSelectedLocation.setText("Selected: " + location.name);
                }
            }
        }
    }

    private void loadWorkoutRoutine() {
        WorkoutRoutine routine = database.workoutRoutineDao().getRoutineById(routineId);
        if (routine != null) {
            etRoutineName.setText(routine.name);
            etDescription.setText(routine.description);
            selectedLocationId = routine.locationId;
            if (selectedLocationId != null) {
                WorkoutLocation location = database.workoutLocationDao().getLocationById(selectedLocationId);
                if (location != null) {
                    tvSelectedLocation.setText("Selected: " + location.name);
                }
            }

            exercises.clear();
            exercises.addAll(database.exerciseDao().getExercisesByRoutine(routineId));
            exerciseAdapter.notifyDataSetChanged();

            equipmentList.clear();
            equipmentList.addAll(database.equipmentDao().getEquipmentByRoutine(routineId));
            equipmentAdapter.notifyDataSetChanged();
        }
    }

    private void showAddExerciseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_exercise, null);
        builder.setView(view);

        TextInputEditText etExerciseName = view.findViewById(R.id.etExerciseName);
        TextInputEditText etInstructions = view.findViewById(R.id.etInstructions);
        TextInputEditText etSets = view.findViewById(R.id.etSets);
        TextInputEditText etReps = view.findViewById(R.id.etReps);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = etExerciseName.getText().toString().trim();
            String instructions = etInstructions.getText().toString().trim();
            String setsStr = etSets.getText().toString().trim();
            String repsStr = etReps.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter exercise name", Toast.LENGTH_SHORT).show();
                return;
            }

            int sets = setsStr.isEmpty() ? 0 : Integer.parseInt(setsStr);
            int reps = repsStr.isEmpty() ? 0 : Integer.parseInt(repsStr);

            Exercise exercise = new Exercise(routineId != -1 ? routineId : 0, name, instructions, sets, reps);
            if (routineId != -1) {
                long exerciseId = database.exerciseDao().insert(exercise);
                exercise.id = exerciseId;
            }
            exercises.add(exercise);
            exerciseAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showAddEquipmentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_equipment, null);
        builder.setView(view);

        TextInputEditText etEquipmentName = view.findViewById(R.id.etEquipmentName);
        TextInputEditText etCategory = view.findViewById(R.id.etCategory);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = etEquipmentName.getText().toString().trim();
            String category = etCategory.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter equipment name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (category.isEmpty()) {
                category = "General";
            }

            Equipment equipment = new Equipment(routineId != -1 ? routineId : 0, name, category);
            if (routineId != -1) {
                long equipmentId = database.equipmentDao().insert(equipment);
                equipment.id = equipmentId;
            }
            equipmentList.add(equipment);
            equipmentAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void saveWorkoutRoutine() {
        String name = etRoutineName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter workout routine name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (routineId == -1) {
            // Create new routine
            WorkoutRoutine routine = new WorkoutRoutine(userId, name, description);
            routine.locationId = selectedLocationId;
            routineId = database.workoutRoutineDao().insert(routine);

            // Save exercises
            for (Exercise exercise : exercises) {
                exercise.routineId = routineId;
                database.exerciseDao().insert(exercise);
            }

            // Save equipment
            for (Equipment equipment : equipmentList) {
                equipment.routineId = routineId;
                database.equipmentDao().insert(equipment);
            }

            Toast.makeText(this, "Workout routine created!", Toast.LENGTH_SHORT).show();
        } else {
            // Update existing routine
            WorkoutRoutine routine = database.workoutRoutineDao().getRoutineById(routineId);
            routine.name = name;
            routine.description = description;
            routine.locationId = selectedLocationId;
            database.workoutRoutineDao().update(routine);

            Toast.makeText(this, "Workout routine updated!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    public void deleteExercise(Exercise exercise) {
        if (exercise.id > 0) {
            database.exerciseDao().delete(exercise.id);
        }
        exercises.remove(exercise);
        exerciseAdapter.notifyDataSetChanged();
    }

    public void editExercise(Exercise exercise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_exercise, null);
        builder.setView(view);

        TextInputEditText etExerciseName = view.findViewById(R.id.etExerciseName);
        TextInputEditText etInstructions = view.findViewById(R.id.etInstructions);
        TextInputEditText etSets = view.findViewById(R.id.etSets);
        TextInputEditText etReps = view.findViewById(R.id.etReps);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        etExerciseName.setText(exercise.name);
        etInstructions.setText(exercise.instructions);
        etSets.setText(String.valueOf(exercise.sets));
        etReps.setText(String.valueOf(exercise.reps));

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            exercise.name = etExerciseName.getText().toString().trim();
            exercise.instructions = etInstructions.getText().toString().trim();
            String setsStr = etSets.getText().toString().trim();
            String repsStr = etReps.getText().toString().trim();
            exercise.sets = setsStr.isEmpty() ? 0 : Integer.parseInt(setsStr);
            exercise.reps = repsStr.isEmpty() ? 0 : Integer.parseInt(repsStr);

            if (exercise.id > 0) {
                database.exerciseDao().update(exercise);
            }
            exerciseAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public void markExerciseAsCompleted(Exercise exercise, boolean isCompleted) {
        exercise.isCompleted = isCompleted;
        if (exercise.id > 0) {
            database.exerciseDao().markAsCompleted(exercise.id, isCompleted);
        }
        exerciseAdapter.notifyDataSetChanged();
    }

    public void deleteEquipment(Equipment equipment) {
        if (equipment.id > 0) {
            database.equipmentDao().delete(equipment.id);
        }
        equipmentList.remove(equipment);
        equipmentAdapter.notifyDataSetChanged();
    }

    public void markEquipmentAsCompleted(Equipment equipment, boolean isCompleted) {
        equipment.isCompleted = isCompleted;
        if (equipment.id > 0) {
            database.equipmentDao().markAsCompleted(equipment.id, isCompleted);
        }
        equipmentAdapter.notifyDataSetChanged();
    }
}
