package com.example.fitlife;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.WorkoutLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {
    private FitLifeDatabase database;
    private long locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        locationId = getIntent().getLongExtra("locationId", -1);
        if (locationId == -1) {
            finish();
            return;
        }

        database = FitLifeDatabase.getInstance(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            WorkoutLocation location = database.workoutLocationDao().getLocationById(locationId);
            if (location != null) {
                LatLng latLng = new LatLng(location.latitude, location.longitude);
                googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(location.name)
                    .snippet(location.address));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        } catch (Exception e) {
            android.widget.Toast.makeText(this, 
                "Map unavailable. Location: " + 
                database.workoutLocationDao().getLocationById(locationId).name + 
                "\nCoordinates: " + 
                database.workoutLocationDao().getLocationById(locationId).latitude + ", " +
                database.workoutLocationDao().getLocationById(locationId).longitude, 
                android.widget.Toast.LENGTH_LONG).show();
        }
    }
}
