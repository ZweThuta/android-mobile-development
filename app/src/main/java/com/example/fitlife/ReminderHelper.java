package com.example.fitlife;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.fitlife.data.ScheduledWorkout;

import java.util.Calendar;

public class ReminderHelper {
    public static void scheduleReminder(Context context, ScheduledWorkout scheduledWorkout) {
        if (scheduledWorkout.reminderTime <= 0) {
            return; // No reminder needed
        }

        try {
            // Parse date and time
            String[] dateParts = scheduledWorkout.scheduledDate.split("-");
            String[] timeParts = scheduledWorkout.scheduledTime.split(":");
            
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Calendar months are 0-indexed
            int day = Integer.parseInt(dateParts[2]);
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            Calendar workoutTime = Calendar.getInstance();
            workoutTime.set(year, month, day, hour, minute, 0);

            // Calculate reminder time (subtract reminder minutes)
            Calendar reminderTime = (Calendar) workoutTime.clone();
            reminderTime.add(Calendar.MINUTE, -(int) scheduledWorkout.reminderTime);

            // Only schedule if reminder time is in the future
            if (reminderTime.getTimeInMillis() > System.currentTimeMillis()) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, WorkoutReminderReceiver.class);
                intent.putExtra("scheduledWorkoutId", scheduledWorkout.id);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (int) scheduledWorkout.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime.getTimeInMillis(),
                        pendingIntent
                    );
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime.getTimeInMillis(),
                        pendingIntent
                    );
                }
            }
        } catch (Exception e) {
            // Handle parsing errors
        }
    }

    public static void cancelReminder(Context context, ScheduledWorkout scheduledWorkout) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WorkoutReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            (int) scheduledWorkout.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }
}
