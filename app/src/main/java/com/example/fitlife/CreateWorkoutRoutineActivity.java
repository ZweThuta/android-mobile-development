package com.example.fitlife;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.Equipment;
import com.example.fitlife.data.Exercise;
import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.WorkoutLocation;
import com.example.fitlife.data.WorkoutRoutine;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateWorkoutRoutineActivity extends AppCompatActivity implements SensorEventListener {
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
    private ImageView ivRoutineImage;
    private Button btnSelectImage, btnRemoveImage;
    private String routineImagePath;
    
    // Image handling
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private static final int CAMERA_REQUEST_CODE = 201;
    private static final int GALLERY_REQUEST_CODE = 202;
    private String currentPhotoPath;
    private Uri photoUri;
    
    // Shake detection
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private long lastShakeTime = 0;
    private float lastX, lastY, lastZ;
    private static final int SHAKE_THRESHOLD = 800;
    private static final int MIN_TIME_BETWEEN_SHAKES = 2000;

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
        ivRoutineImage = findViewById(R.id.ivRoutineImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnRemoveImage = findViewById(R.id.btnRemoveImage);

        exercises = new ArrayList<>();
        equipmentList = new ArrayList<>();

        exerciseAdapter = new ExerciseAdapter(exercises, this);
        equipmentAdapter = new EquipmentAdapter(equipmentList, this);

        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        rvExercises.setAdapter(exerciseAdapter);
        
        // Setup swipe gestures for exercises
        setupSwipeGestures();

        rvEquipment.setLayoutManager(new LinearLayoutManager(this));
        rvEquipment.setAdapter(equipmentAdapter);
        
        // Setup shake detection
        setupShakeDetection();

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
        
        // Image selection
        btnSelectImage.setOnClickListener(v -> showImageOptions());
        btnRemoveImage.setOnClickListener(v -> removeImage());

        if (routineId != -1) {
            loadWorkoutRoutine();
        }
    }
    
    private void setupSwipeGestures() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, 
                                @NonNull RecyclerView.ViewHolder viewHolder, 
                                @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION || position >= exercises.size()) {
                    return;
                }
                
                Exercise exercise = exercises.get(position);
                
                if (direction == ItemTouchHelper.LEFT) {
                    // Swipe left to delete
                    deleteExercise(exercise);
                    Toast.makeText(CreateWorkoutRoutineActivity.this, 
                        "Exercise deleted", Toast.LENGTH_SHORT).show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Swipe right to mark as complete
                    boolean newState = !exercise.isCompleted;
                    markExerciseAsCompleted(exercise, newState);
                    String message = newState ? "Exercise marked as complete" : "Exercise marked as incomplete";
                    Toast.makeText(CreateWorkoutRoutineActivity.this, 
                        message, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onChildDraw(@NonNull android.graphics.Canvas c, 
                                  @NonNull RecyclerView recyclerView, 
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    int position = viewHolder.getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION || position >= exercises.size()) {
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        return;
                    }
                    
                    View itemView = viewHolder.itemView;
                    
                    // Background color for swipe actions
                    int backgroundColor;
                    android.graphics.drawable.Drawable icon;
                    
                    if (dX > 0) {
                        // Swiping right (mark complete)
                        boolean isCompleted = exercises.get(position).isCompleted;
                        backgroundColor = ContextCompat.getColor(CreateWorkoutRoutineActivity.this, 
                            isCompleted ? R.color.warning : R.color.success);
                        icon = ContextCompat.getDrawable(CreateWorkoutRoutineActivity.this, 
                            android.R.drawable.ic_menu_revert);
                    } else {
                        // Swiping left (delete)
                        backgroundColor = ContextCompat.getColor(CreateWorkoutRoutineActivity.this, 
                            R.color.error);
                        icon = ContextCompat.getDrawable(CreateWorkoutRoutineActivity.this, 
                            android.R.drawable.ic_menu_delete);
                    }
                    
                    // Draw background
                    android.graphics.Paint paint = new android.graphics.Paint();
                    paint.setColor(backgroundColor);
                    if (dX > 0) {
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), 
                                  dX, (float) itemView.getBottom(), paint);
                    } else {
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), 
                                  (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                    }
                    
                    // Draw icon and text
                    if (icon != null) {
                        int iconMargin = 48;
                        int iconSize = 48;
                        int iconTop = itemView.getTop() + (itemView.getBottom() - itemView.getTop() - iconSize) / 2;
                        int iconLeft = dX > 0 
                            ? (int) (itemView.getLeft() + iconMargin)
                            : (int) (itemView.getRight() - iconMargin - iconSize);
                        int iconRight = iconLeft + iconSize;
                        int iconBottom = iconTop + iconSize;
                        
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(c);
                    }
                }
                
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvExercises);
    }
    
    private void setupShakeDetection() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }
    
    private void resetWorkoutList() {
        if (exercises.isEmpty()) {
            Toast.makeText(this, "No exercises to reset", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new AlertDialog.Builder(this)
            .setTitle("Reset Workout List")
            .setMessage("Are you sure you want to clear all exercises? This action cannot be undone.")
            .setPositiveButton("Reset", (dialog, which) -> {
                // Delete exercises from database
                for (Exercise exercise : exercises) {
                    if (exercise.id > 0) {
                        database.exerciseDao().delete(exercise.id);
                    }
                }
                
                // Clear list
                exercises.clear();
                exerciseAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Workout list reset", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
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
        } else if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                if (currentPhotoPath != null) {
                    routineImagePath = currentPhotoPath;
                    displayImage(routineImagePath);
                }
            } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                Uri selectedImage = data.getData();
                try {
                    routineImagePath = copyImageToAppStorage(selectedImage);
                    if (routineImagePath != null) {
                        displayImage(routineImagePath);
                    }
                } catch (IOException e) {
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
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
            
            // Load routine image
            if (routine.imagePath != null && !routine.imagePath.isEmpty()) {
                routineImagePath = routine.imagePath;
                displayImage(routineImagePath);
            }
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
            routine.imagePath = routineImagePath;
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
            routine.imagePath = routineImagePath;
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
    
    private void showImageOptions() {
        new AlertDialog.Builder(this)
            .setTitle("Select Routine Image")
            .setItems(new String[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
                if (which == 0) {
                    takePhoto();
                } else {
                    chooseFromGallery();
                }
            })
            .show();
    }
    
    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                    "com.example.fitlife.fileprovider",
                    photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }
    
    private void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }
    
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "ROUTINE_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            return null;
        }
    }
    
    private String copyImageToAppStorage(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "ROUTINE_" + timeStamp + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName);
        
        FileOutputStream outputStream = new FileOutputStream(imageFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.close();
        inputStream.close();
        
        return imageFile.getAbsolutePath();
    }
    
    private void displayImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivRoutineImage.setImageBitmap(bitmap);
                ivRoutineImage.setVisibility(View.VISIBLE);
                btnRemoveImage.setVisibility(View.VISIBLE);
            }
        }
    }
    
    private void removeImage() {
        routineImagePath = null;
        ivRoutineImage.setVisibility(View.GONE);
        btnRemoveImage.setVisibility(View.GONE);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            
            long currentTime = System.currentTimeMillis();
            
            if ((currentTime - lastUpdate) > 100) {
                long diffTime = (currentTime - lastUpdate);
                lastUpdate = currentTime;
                
                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;
                
                if (speed > SHAKE_THRESHOLD && (currentTime - lastShakeTime) > MIN_TIME_BETWEEN_SHAKES) {
                    // Shake detected
                    lastShakeTime = currentTime;
                    runOnUiThread(() -> resetWorkoutList());
                }
                
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for accelerometer
    }
}
