package com.example.fitlife;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.BodyMetrics;
import com.example.fitlife.data.FitLifeDatabase;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BodyMetricsActivity extends AppCompatActivity {
    private TextInputEditText etWeight, etHeight, etBodyFat, etChest, etWaist, etHips;
    private TextInputEditText etLeftArm, etRightArm, etLeftThigh, etRightThigh, etNotes;
    private TextView tvBMI, tvBMICategory;
    private RecyclerView rvMetrics;
    private BodyMetricsAdapter adapter;
    private FitLifeDatabase database;
    private long userId;
    private List<BodyMetrics> metricsList;
    private double lastHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_metrics);

        database = FitLifeDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etBodyFat = findViewById(R.id.etBodyFat);
        etChest = findViewById(R.id.etChest);
        etWaist = findViewById(R.id.etWaist);
        etHips = findViewById(R.id.etHips);
        etLeftArm = findViewById(R.id.etLeftArm);
        etRightArm = findViewById(R.id.etRightArm);
        etLeftThigh = findViewById(R.id.etLeftThigh);
        etRightThigh = findViewById(R.id.etRightThigh);
        etNotes = findViewById(R.id.etNotes);
        tvBMI = findViewById(R.id.tvBMI);
        tvBMICategory = findViewById(R.id.tvBMICategory);
        rvMetrics = findViewById(R.id.rvMetrics);
        Button btnSave = findViewById(R.id.btnSave);

        metricsList = new ArrayList<>();
        adapter = new BodyMetricsAdapter(metricsList, this, database);
        rvMetrics.setLayoutManager(new LinearLayoutManager(this));
        rvMetrics.setAdapter(adapter);

        // Load last height if available
        BodyMetrics latest = database.bodyMetricsDao().getLatestMetrics(userId);
        if (latest != null && latest.height > 0) {
            lastHeight = latest.height;
            etHeight.setText(String.valueOf(lastHeight));
        }

        // Calculate BMI when weight or height changes
        etWeight.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateBMI();
        });
        etHeight.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                calculateBMI();
                String heightStr = etHeight.getText().toString().trim();
                if (!heightStr.isEmpty()) {
                    try {
                        lastHeight = Double.parseDouble(heightStr);
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
            }
        });

        btnSave.setOnClickListener(v -> saveMetrics());
        loadMetrics();
        updateBMIDisplay();
    }

    private void calculateBMI() {
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        
        if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
            try {
                double weight = Double.parseDouble(weightStr);
                double height = Double.parseDouble(heightStr);
                
                if (height > 0) {
                    double heightInMeters = height / 100.0;
                    double bmi = weight / (heightInMeters * heightInMeters);
                    tvBMI.setText(String.format(Locale.getDefault(), "%.1f", bmi));
                    tvBMICategory.setText(getBMICategory(bmi));
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    private void updateBMIDisplay() {
        BodyMetrics latest = database.bodyMetricsDao().getLatestMetrics(userId);
        if (latest != null && latest.getBMI() > 0) {
            tvBMI.setText(String.format(Locale.getDefault(), "%.1f", latest.getBMI()));
            tvBMICategory.setText(getBMICategory(latest.getBMI()));
        } else {
            tvBMI.setText("--");
            tvBMICategory.setText("");
        }
    }

    private void saveMetrics() {
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        
        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Please enter weight and height", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);
            
            // Use last height if current is empty
            if (height == 0 && lastHeight > 0) {
                height = lastHeight;
            }
            
            if (height == 0) {
                Toast.makeText(this, "Please enter height", Toast.LENGTH_SHORT).show();
                return;
            }

            BodyMetrics metrics = new BodyMetrics(userId, System.currentTimeMillis(), weight, height);
            
            // Optional fields
            String bodyFatStr = etBodyFat.getText().toString().trim();
            if (!bodyFatStr.isEmpty()) {
                metrics.bodyFat = Double.parseDouble(bodyFatStr);
            }
            
            String chestStr = etChest.getText().toString().trim();
            if (!chestStr.isEmpty()) {
                metrics.chest = Double.parseDouble(chestStr);
            }
            
            String waistStr = etWaist.getText().toString().trim();
            if (!waistStr.isEmpty()) {
                metrics.waist = Double.parseDouble(waistStr);
            }
            
            String hipsStr = etHips.getText().toString().trim();
            if (!hipsStr.isEmpty()) {
                metrics.hips = Double.parseDouble(hipsStr);
            }
            
            String leftArmStr = etLeftArm.getText().toString().trim();
            if (!leftArmStr.isEmpty()) {
                metrics.leftArm = Double.parseDouble(leftArmStr);
            }
            
            String rightArmStr = etRightArm.getText().toString().trim();
            if (!rightArmStr.isEmpty()) {
                metrics.rightArm = Double.parseDouble(rightArmStr);
            }
            
            String leftThighStr = etLeftThigh.getText().toString().trim();
            if (!leftThighStr.isEmpty()) {
                metrics.leftThigh = Double.parseDouble(leftThighStr);
            }
            
            String rightThighStr = etRightThigh.getText().toString().trim();
            if (!rightThighStr.isEmpty()) {
                metrics.rightThigh = Double.parseDouble(rightThighStr);
            }
            
            metrics.notes = etNotes.getText().toString().trim();
            lastHeight = height;

            long id = database.bodyMetricsDao().insert(metrics);
            if (id > 0) {
                Toast.makeText(this, "Measurement saved!", Toast.LENGTH_SHORT).show();
                clearFields();
                loadMetrics();
                updateBMIDisplay();
            } else {
                Toast.makeText(this, "Failed to save measurement", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        etWeight.setText("");
        etBodyFat.setText("");
        etChest.setText("");
        etWaist.setText("");
        etHips.setText("");
        etLeftArm.setText("");
        etRightArm.setText("");
        etLeftThigh.setText("");
        etRightThigh.setText("");
        etNotes.setText("");
        // Keep height for next entry
    }

    private void loadMetrics() {
        metricsList.clear();
        metricsList.addAll(database.bodyMetricsDao().getMetricsByUser(userId));
        adapter.notifyDataSetChanged();
    }

    public void deleteMetrics(BodyMetrics metrics) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Measurement")
            .setMessage("Are you sure you want to delete this measurement?")
            .setPositiveButton("Delete", (dialog, which) -> {
                database.bodyMetricsDao().delete(metrics.id);
                loadMetrics();
                updateBMIDisplay();
                Toast.makeText(this, "Measurement deleted", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
