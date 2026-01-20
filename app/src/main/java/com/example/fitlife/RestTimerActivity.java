package com.example.fitlife;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private MediaPlayer mediaPlayer;
    private Ringtone alarmRingtone;

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
        // Vibrate with a strong pattern
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] vibrationPattern = {0, 500, 200, 500, 200, 500, 200, 500};
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1));
            } else {
                vibrator.vibrate(vibrationPattern, -1);
            }
        }

        // Play alarm sound
        playAlarmSound();

        // Show completion dialog
        showTimerCompleteDialog();

        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
    }

    private void playAlarmSound() {
        try {
            // Try to use alarm sound first, then fall back to notification
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }

            // Stop any previously playing alarm
            stopAlarmSound();

            // Use MediaPlayer for better control
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, alarmUri);
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
            }
            
            mediaPlayer.setLooping(true); // Loop until user dismisses
            mediaPlayer.prepare();
            mediaPlayer.start();

            // Auto-stop after 30 seconds if user doesn't dismiss
            new Handler(Looper.getMainLooper()).postDelayed(this::stopAlarmSound, 30000);

        } catch (Exception e) {
            // Fallback to ringtone if MediaPlayer fails
            try {
                Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alarmUri == null) {
                    alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                }
                alarmRingtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
                if (alarmRingtone != null) {
                    alarmRingtone.play();
                }
            } catch (Exception ex) {
                // Ignore if sound can't play
            }
        }
    }

    private void stopAlarmSound() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
            if (alarmRingtone != null) {
                if (alarmRingtone.isPlaying()) {
                    alarmRingtone.stop();
                }
                alarmRingtone = null;
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    private void showTimerCompleteDialog() {
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("Time's Up!")
                .setMessage("Your rest period is complete. Ready to continue your workout?")
                .setPositiveButton("Got it!", (dialog, which) -> {
                    stopAlarmSound();
                    dialog.dismiss();
                })
                .setCancelable(false)
                .setOnDismissListener(dialog -> stopAlarmSound())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopAlarmSound();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't stop alarm on pause - user should hear it even if app goes to background
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop alarm when activity is no longer visible (user navigated away)
        // but keep timer running
    }
}
