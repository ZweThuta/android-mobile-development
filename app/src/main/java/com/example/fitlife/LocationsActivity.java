package com.example.fitlife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.WorkoutLocation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class LocationsActivity extends AppCompatActivity {
    private RecyclerView rvLocations;
    private LocationAdapter adapter;
    private FitLifeDatabase database;
    private long userId;
    private List<WorkoutLocation> locations;
    private boolean selectMode;
    private View emptyState;
    private ImageView ivEmptyIcon;
    private TextView tvEmptyTitle, tvEmptyMessage;
    private MaterialButton btnEmptyAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        database = FitLifeDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        selectMode = getIntent().getBooleanExtra("selectMode", false);

        rvLocations = findViewById(R.id.rvLocations);
        FloatingActionButton fabAddLocation = findViewById(R.id.fabAddLocation);
        emptyState = findViewById(R.id.emptyState);
        ivEmptyIcon = emptyState.findViewById(R.id.ivEmptyIcon);
        tvEmptyTitle = emptyState.findViewById(R.id.tvEmptyTitle);
        tvEmptyMessage = emptyState.findViewById(R.id.tvEmptyMessage);
        btnEmptyAction = emptyState.findViewById(R.id.btnEmptyAction);

        // Configure empty state
        ivEmptyIcon.setImageResource(android.R.drawable.ic_menu_mylocation);
        tvEmptyTitle.setText("No Locations Yet");
        tvEmptyMessage.setText("Tag your favorite gyms, parks, and workout spots to easily find them later!");
        btnEmptyAction.setText("Add Location");
        btnEmptyAction.setVisibility(View.VISIBLE);
        btnEmptyAction.setOnClickListener(v -> {
            startActivity(new Intent(LocationsActivity.this, AddLocationActivity.class));
        });

        locations = new ArrayList<>();
        adapter = new LocationAdapter(locations, this, selectMode);
        rvLocations.setLayoutManager(new LinearLayoutManager(this));
        rvLocations.setAdapter(adapter);

        fabAddLocation.setOnClickListener(v -> {
            startActivity(new Intent(LocationsActivity.this, AddLocationActivity.class));
        });

        loadLocations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocations();
    }

    private void loadLocations() {
        locations.clear();
        locations.addAll(database.workoutLocationDao().getLocationsByUser(userId));
        adapter.notifyDataSetChanged();
        
        // Show/hide empty state
        if (locations.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvLocations.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvLocations.setVisibility(View.VISIBLE);
        }
    }

    public void deleteLocation(WorkoutLocation location) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Location")
            .setMessage("Are you sure you want to delete " + location.name + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                database.workoutLocationDao().delete(location.id);
                loadLocations();
                Toast.makeText(this, "Location deleted", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    public void viewOnMap(WorkoutLocation location) {
        Intent intent = new Intent(this, MapViewActivity.class);
        intent.putExtra("locationId", location.id);
        startActivity(intent);
    }

    public void selectLocation(WorkoutLocation location) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("locationId", location.id);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
