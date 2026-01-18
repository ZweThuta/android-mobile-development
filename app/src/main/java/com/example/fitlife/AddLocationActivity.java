package com.example.fitlife;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.WorkoutLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

public class AddLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 200;
    private TextInputEditText etLocationName, etAddress, etType, etLatitude, etLongitude;
    private TextView tvCoordinates, tvMapNote;
    private FitLifeDatabase database;
    private long userId;
    private GoogleMap googleMap;
    private LatLng selectedLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean mapsAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        database = FitLifeDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        etLocationName = findViewById(R.id.etLocationName);
        etAddress = findViewById(R.id.etAddress);
        etType = findViewById(R.id.etType);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        tvCoordinates = findViewById(R.id.tvCoordinates);
        tvMapNote = findViewById(R.id.tvMapNote);
        Button btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        Button btnSave = findViewById(R.id.btnSave);

        // Try to initialize map, but handle gracefully if API key is missing
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
                mapsAvailable = true;
            }
        } catch (Exception e) {
            // Maps not available - show note and hide map
            tvMapNote.setVisibility(android.view.View.VISIBLE);
            tvMapNote.setText("Note: Map view requires Google Maps API key. You can still enter coordinates manually.");
            findViewById(R.id.map).setVisibility(android.view.View.GONE);
            mapsAvailable = false;
        }

        // Update coordinates when manually entered
        etLatitude.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCoordinatesFromInput();
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        etLongitude.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCoordinatesFromInput();
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        btnGetCurrentLocation.setOnClickListener(v -> getCurrentLocation());
        btnSave.setOnClickListener(v -> saveLocation());
    }

    @Override
    public void onMapReady(GoogleMap map) {
        try {
            googleMap = map;
            mapsAvailable = true;

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            }

            googleMap.setOnMapClickListener(latLng -> {
                selectedLocation = latLng;
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                etLatitude.setText(String.valueOf(latLng.latitude));
                etLongitude.setText(String.valueOf(latLng.longitude));
                tvCoordinates.setText("Coordinates: " + latLng.latitude + ", " + latLng.longitude);
            });
        } catch (Exception e) {
            // Handle map initialization errors gracefully
            mapsAvailable = false;
            tvMapNote.setVisibility(android.view.View.VISIBLE);
            tvMapNote.setText("Map unavailable. Please enter coordinates manually.");
        }
    }

    private void updateCoordinatesFromInput() {
        String latStr = etLatitude.getText().toString().trim();
        String lngStr = etLongitude.getText().toString().trim();
        
        if (!latStr.isEmpty() && !lngStr.isEmpty()) {
            try {
                double lat = Double.parseDouble(latStr);
                double lng = Double.parseDouble(lngStr);
                selectedLocation = new LatLng(lat, lng);
                tvCoordinates.setText("Coordinates: " + lat + ", " + lng);
            } catch (NumberFormatException e) {
                // Invalid input, ignore
            }
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, location -> {
                if (location != null) {
                    selectedLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    etLatitude.setText(String.valueOf(selectedLocation.latitude));
                    etLongitude.setText(String.valueOf(selectedLocation.longitude));
                    tvCoordinates.setText("Coordinates: " + selectedLocation.latitude + ", " + selectedLocation.longitude);
                    
                    if (googleMap != null && mapsAvailable) {
                        try {
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions()
                                .position(selectedLocation)
                                .title("Current Location"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15));
                        } catch (Exception e) {
                            // Map not available, but coordinates are set
                        }
                    }
                    Toast.makeText(this, "Location retrieved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Unable to get current location. Please enter coordinates manually.", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveLocation() {
        String name = etLocationName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String type = etType.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter location name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get coordinates from manual input or selected location
        String latStr = etLatitude.getText().toString().trim();
        String lngStr = etLongitude.getText().toString().trim();
        
        if (selectedLocation == null && (!latStr.isEmpty() && !lngStr.isEmpty())) {
            try {
                double lat = Double.parseDouble(latStr);
                double lng = Double.parseDouble(lngStr);
                selectedLocation = new LatLng(lat, lng);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid coordinates. Please enter valid latitude and longitude.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        if (selectedLocation == null) {
            Toast.makeText(this, "Please enter coordinates manually, use current location, or select on map", Toast.LENGTH_SHORT).show();
            return;
        }

        if (type.isEmpty()) {
            type = "General";
        }

        WorkoutLocation location = new WorkoutLocation(
            userId,
            name,
            address,
            selectedLocation.latitude,
            selectedLocation.longitude,
            type
        );

        long locationId = database.workoutLocationDao().insert(location);
        if (locationId > 0) {
            Toast.makeText(this, "Location saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save location", Toast.LENGTH_SHORT).show();
        }
    }
}
