package com.example.fitlife;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.ScheduledWorkout;
import com.example.fitlife.data.WorkoutRoutine;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleWorkoutActivity extends AppCompatActivity {
    private TextInputEditText etDate, etTime, etNotes;
    private RadioGroup rgReminder;
    private FitLifeDatabase database;
    private long userId;
    private long routineId;
    private WorkoutRoutine routine;
    private Calendar selectedDate;
    private Calendar selectedTime;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_workout);

        database = FitLifeDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        routineId = getIntent().getLongExtra("routineId", -1);
        if (routineId == -1) {
            Toast.makeText(this, "Invalid workout routine", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        routine = database.workoutRoutineDao().getRoutineById(routineId);
        if (routine == null) {
            Toast.makeText(this, "Workout routine not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvRoutineName = findViewById(R.id.tvRoutineName);
        tvRoutineName.setText("Schedule: " + routine.name);

        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etNotes = findViewById(R.id.etNotes);
        rgReminder = findViewById(R.id.rgReminder);
        Button btnSchedule = findViewById(R.id.btnSchedule);

        selectedDate = Calendar.getInstance();
        selectedTime = Calendar.getInstance();

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
        btnSchedule.setOnClickListener(v -> scheduleWorkout());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                etDate.setText(dateFormat.format(selectedDate.getTime()));
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedTime.set(Calendar.MINUTE, minute);
                etTime.setText(timeFormat.format(selectedTime.getTime()));
            },
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            true
        );
        timePickerDialog.show();
    }

    private void scheduleWorkout() {
        if (etDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (etTime.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        int checkedId = rgReminder.getCheckedRadioButtonId();
        long reminderMinutes = 15; // Default

        if (checkedId == R.id.rb15min) {
            reminderMinutes = 15;
        } else if (checkedId == R.id.rb30min) {
            reminderMinutes = 30;
        } else if (checkedId == R.id.rb60min) {
            reminderMinutes = 60;
        } else if (checkedId == R.id.rbNoReminder) {
            reminderMinutes = 0;
        }

        ScheduledWorkout scheduledWorkout = new ScheduledWorkout(userId, routineId, date, time);
        scheduledWorkout.reminderTime = reminderMinutes;
        scheduledWorkout.notes = notes;

        long id = database.scheduledWorkoutDao().insert(scheduledWorkout);
        if (id > 0) {
            scheduledWorkout.id = id;
            // Schedule reminder notification
            ReminderHelper.scheduleReminder(this, scheduledWorkout);
            Toast.makeText(this, "Workout scheduled successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to schedule workout", Toast.LENGTH_SHORT).show();
        }
    }
}
