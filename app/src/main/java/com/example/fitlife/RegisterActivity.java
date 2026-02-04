package com.example.fitlife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private TextInputLayout tilUsername, tilEmail, tilPassword, tilConfirmPassword;
    private FitLifeDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FitLifeDatabase.getInstance(this);
        tilUsername = findViewById(R.id.tilUsername);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> register());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        // Clear errors when user starts typing
        etUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilUsername.setError(null);
            }
        });

        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilEmail.setError(null);
            }
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilPassword.setError(null);
                tilConfirmPassword.setError(null);
            }
        });

        etConfirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilConfirmPassword.setError(null);
            }
        });
    }

    private void register() {
        tilUsername.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Username is required");
            tilUsername.requestFocus();
            isValid = false;
        } else if (username.length() < 3) {
            tilUsername.setError("Username must be at least 3 characters");
            tilUsername.requestFocus();
            isValid = false;
        } else if (username.length() > 20) {
            tilUsername.setError("Username must be less than 20 characters");
            tilUsername.requestFocus();
            isValid = false;
        } else if (!username.matches("^[a-zA-Z0-9_]+$")) {
            tilUsername.setError("Username can only contain letters, numbers, and underscores");
            tilUsername.requestFocus();
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            if (isValid) {
                tilEmail.requestFocus();
            }
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            if (isValid) {
                tilEmail.requestFocus();
            }
            isValid = false;
        } else {
            // Check if email already exists
            if (database.userDao().getUserByEmail(email) != null) {
                tilEmail.setError("This email is already registered");
                if (isValid) {
                    tilEmail.requestFocus();
                }
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            if (isValid) {
                tilPassword.requestFocus();
            }
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            if (isValid) {
                tilPassword.requestFocus();
            }
            isValid = false;
        } else if (password.length() > 50) {
            tilPassword.setError("Password must be less than 50 characters");
            if (isValid) {
                tilPassword.requestFocus();
            }
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            if (isValid) {
                tilConfirmPassword.requestFocus();
            }
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            if (isValid) {
                tilConfirmPassword.requestFocus();
            }
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        User user = new User(username, email, password);
        long userId = database.userDao().insert(user);

        if (userId > 0) {
            // Save user ID to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
            prefs.edit().putLong("userId", userId).putString("username", username).apply();

            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        } else {
            tilEmail.setError("Registration failed. Please try again.");
            tilEmail.requestFocus();
        }
    }
}
