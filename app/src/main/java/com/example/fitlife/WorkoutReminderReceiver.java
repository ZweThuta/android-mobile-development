package com.example.fitlife;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.ScheduledWorkout;
import com.example.fitlife.data.WorkoutRoutine;

public class WorkoutReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "workout_reminders";
    private static final int NOTIFICATION_ID = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        long scheduledWorkoutId = intent.getLongExtra("scheduledWorkoutId", -1);
        if (scheduledWorkoutId == -1) return;

        FitLifeDatabase database = FitLifeDatabase.getInstance(context);
        ScheduledWorkout scheduledWorkout = database.scheduledWorkoutDao().getScheduledWorkoutById(scheduledWorkoutId);
        
        if (scheduledWorkout == null || !"scheduled".equals(scheduledWorkout.status)) {
            return;
        }

        WorkoutRoutine routine = database.workoutRoutineDao().getRoutineById(scheduledWorkout.routineId);
        if (routine == null) return;

        createNotificationChannel(context);
        sendNotification(context, routine.name, scheduledWorkout);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Workout Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for scheduled workouts");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(Context context, String workoutName, ScheduledWorkout scheduledWorkout) {
        Intent intent = new Intent(context, CalendarActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_today)
            .setContentTitle("Workout Reminder: " + workoutName)
            .setContentText("Your workout is scheduled for " + scheduledWorkout.scheduledDate + " at " + scheduledWorkout.scheduledTime)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) scheduledWorkout.id, builder.build());
    }
}
