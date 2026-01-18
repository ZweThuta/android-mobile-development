package com.example.fitlife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        long userId = prefs.getLong("userId", -1);

        if (userId != -1) {
            // User is logged in, go to Home
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            // User is not logged in, go to Login
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}