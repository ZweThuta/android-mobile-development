package com.example.fitlife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.WorkoutLocation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

        // Setup swipe to delete (only when not in select mode)
        if (!selectMode) {
            setupSwipeToDelete();
        }

        fabAddLocation.setOnClickListener(v -> {
            startActivity(new Intent(LocationsActivity.this, AddLocationActivity.class));
        });

        loadLocations();
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
                WorkoutLocation deletedItem = adapter.getItemAt(position);

                if (deletedItem != null) {
                    // Remove from adapter
                    adapter.removeAt(position);
                    updateEmptyState();

                    // Show snackbar with undo option
                    Snackbar snackbar = Snackbar.make(rvLocations,
                            "\"" + deletedItem.name + "\" deleted",
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
                                database.workoutLocationDao().delete(deletedItem.id);
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
        itemTouchHelper.attachToRecyclerView(rvLocations);
    }

    private void updateEmptyState() {
        if (locations.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvLocations.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvLocations.setVisibility(View.VISIBLE);
        }
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
        updateEmptyState();
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
