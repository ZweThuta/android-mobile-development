package com.example.fitlife;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

public class RestTimerActivity extends AppCompatActivity {
    private TextView tvTimer;
    private Button btnStart, btnPause, btnReset;
    private Button btn30s, btn1min, btn2min, btn3min, btn5min;
    private TextInputEditText etCustomTime;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 0;
    private boolean timerRunning = false;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_timer);

        // Keep screen on during timer
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tvTimer = findViewById(R.id.tvTimer);
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnReset = findViewById(R.id.btnReset);
        btn30s = findViewById(R.id.btn30s);
        btn1min = findViewById(R.id.btn1min);
        btn2min = findViewById(R.id.btn2min);
        btn3min = findViewById(R.id.btn3min);
        btn5min = findViewById(R.id.btn5min);
        etCustomTime = findViewById(R.id.etCustomTime);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        btn30s.setOnClickListener(v -> setTimer(30));
        btn1min.setOnClickListener(v -> setTimer(60));
        btn2min.setOnClickListener(v -> setTimer(120));
        btn3min.setOnClickListener(v -> setTimer(180));
        btn5min.setOnClickListener(v -> setTimer(300));

        btnStart.setOnClickListener(v -> startTimer());
        btnPause.setOnClickListener(v -> pauseTimer());
        btnReset.setOnClickListener(v -> resetTimer());

        updateTimerDisplay();
    }

    private void setTimer(int seconds) {
        if (timerRunning) {
            Toast.makeText(this, "Please stop the timer first", Toast.LENGTH_SHORT).show();
            return;
        }
        timeLeftInMillis = seconds * 1000L;
        updateTimerDisplay();
    }

    private void startTimer() {
        // Check if custom time is set
        String customTime = etCustomTime.getText().toString().trim();
        if (!customTime.isEmpty() && timeLeftInMillis == 0) {
            try {
                int seconds = Integer.parseInt(customTime);
                if (seconds > 0) {
                    timeLeftInMillis = seconds * 1000L;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (timeLeftInMillis == 0) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (timerRunning) {
            return;
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerDisplay();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                timeLeftInMillis = 0;
                updateTimerDisplay();
                onTimerFinished();
            }
        }.start();

        timerRunning = true;
        btnStart.setEnabled(false);
        btnPause.setEnabled(true);
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
        timeLeftInMillis = 0;
        updateTimerDisplay();
        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
    }

    private void updateTimerDisplay() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeft = String.format("%02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeft);
    }

    private void onTimerFinished() {
        // Vibrate
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 500, 200, 500}, -1));
            } else {
                vibrator.vibrate(new long[]{0, 500, 200, 500}, -1);
            }
        }

        // Play sound (system notification sound)
        try {
            android.media.RingtoneManager.getRingtone(
                getApplicationContext(),
                android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
            ).play();
        } catch (Exception e) {
            // Ignore if sound can't play
        }

        Toast.makeText(this, "Rest time is over! 💪", Toast.LENGTH_LONG).show();
        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
