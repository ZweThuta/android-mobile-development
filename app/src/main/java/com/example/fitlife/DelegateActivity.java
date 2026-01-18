package com.example.fitlife;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fitlife.data.Equipment;
import com.example.fitlife.data.Exercise;
import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.WorkoutLocation;
import com.example.fitlife.data.WorkoutRoutine;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class DelegateActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_REQUEST_CODE = 100;
    private TextInputEditText etPhoneNumber;
    private RadioGroup rgDelegateType;
    private TextView tvPreview;
    private FitLifeDatabase database;
    private long routineId;
    private WorkoutRoutine routine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegate);

        routineId = getIntent().getLongExtra("routineId", -1);
        if (routineId == -1) {
            Toast.makeText(this, "Invalid workout routine", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        database = FitLifeDatabase.getInstance(this);
        routine = database.workoutRoutineDao().getRoutineById(routineId);
        if (routine == null) {
            Toast.makeText(this, "Workout routine not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        rgDelegateType = findViewById(R.id.rgDelegateType);
        tvPreview = findViewById(R.id.tvPreview);
        Button btnSendSMS = findViewById(R.id.btnSendSMS);

        RadioButton rbEquipment = findViewById(R.id.rbEquipment);
        RadioButton rbExercises = findViewById(R.id.rbExercises);
        RadioButton rbReminder = findViewById(R.id.rbReminder);

        updatePreview();

        rgDelegateType.setOnCheckedChangeListener((group, checkedId) -> updatePreview());

        btnSendSMS.setOnClickListener(v -> sendSMS());
    }

    private void updatePreview() {
        String message = buildMessage();
        tvPreview.setText(message);
    }

    private String buildMessage() {
        StringBuilder message = new StringBuilder();
        message.append("FitLife Workout: ").append(routine.name).append("\n\n");

        int checkedId = rgDelegateType.getCheckedRadioButtonId();

        if (checkedId == R.id.rbEquipment) {
            message.append("EQUIPMENT LIST:\n");
            List<Equipment> equipmentList = database.equipmentDao().getEquipmentByRoutine(routineId);
            if (equipmentList.isEmpty()) {
                message.append("No equipment required.\n");
            } else {
                String currentCategory = "";
                for (Equipment equipment : equipmentList) {
                    if (!equipment.category.equals(currentCategory)) {
                        currentCategory = equipment.category;
                        message.append("\n").append(currentCategory).append(":\n");
                    }
                    message.append("• ").append(equipment.name).append("\n");
                }
            }

            if (routine.locationId != null) {
                WorkoutLocation location = database.workoutLocationDao().getLocationById(routine.locationId);
                if (location != null) {
                    message.append("\nLocation: ").append(location.name);
                    message.append("\nAddress: ").append(location.address);
                }
            }
        } else if (checkedId == R.id.rbExercises) {
            message.append("EXERCISE CHECKLIST:\n\n");
            List<Exercise> exercises = database.exerciseDao().getExercisesByRoutine(routineId);
            if (exercises.isEmpty()) {
                message.append("No exercises added.\n");
            } else {
                for (Exercise exercise : exercises) {
                    message.append("☐ ").append(exercise.name);
                    if (exercise.sets > 0 && exercise.reps > 0) {
                        message.append(" (").append(exercise.sets).append(" sets x ").append(exercise.reps).append(" reps)");
                    }
                    message.append("\n");
                    if (!exercise.instructions.isEmpty()) {
                        message.append("   ").append(exercise.instructions).append("\n");
                    }
                    message.append("\n");
                }
            }
        } else if (checkedId == R.id.rbReminder) {
            message.append("WORKOUT REMINDER\n\n");
            message.append("Don't forget your workout: ").append(routine.name).append("\n\n");
            
            List<Exercise> exercises = database.exerciseDao().getExercisesByRoutine(routineId);
            if (!exercises.isEmpty()) {
                message.append("Exercises: ").append(exercises.size()).append("\n");
            }
            
            List<Equipment> equipmentList = database.equipmentDao().getEquipmentByRoutine(routineId);
            if (!equipmentList.isEmpty()) {
                message.append("Equipment needed: ").append(equipmentList.size()).append(" items\n");
            }

            if (routine.locationId != null) {
                WorkoutLocation location = database.workoutLocationDao().getLocationById(routine.locationId);
                if (location != null) {
                    message.append("\nLocation: ").append(location.name);
                }
            }
        }

        return message.toString();
    }

    private void sendSMS() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
            return;
        }

        String message = buildMessage();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
            } else {
                Toast.makeText(this, "SMS permission is required to send messages", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
