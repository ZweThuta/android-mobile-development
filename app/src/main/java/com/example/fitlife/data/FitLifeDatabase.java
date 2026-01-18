package com.example.fitlife.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
    entities = {User.class, WorkoutRoutine.class, Exercise.class, Equipment.class, WorkoutLocation.class, ScheduledWorkout.class, WorkoutHistory.class, ProgressPhoto.class, BodyMetrics.class},
    version = 3,
    exportSchema = false
)
public abstract class FitLifeDatabase extends RoomDatabase {
    private static FitLifeDatabase INSTANCE;
    
    public abstract UserDao userDao();
    public abstract WorkoutRoutineDao workoutRoutineDao();
    public abstract ExerciseDao exerciseDao();
    public abstract EquipmentDao equipmentDao();
    public abstract WorkoutLocationDao workoutLocationDao();
    public abstract ScheduledWorkoutDao scheduledWorkoutDao();
    public abstract WorkoutHistoryDao workoutHistoryDao();
    public abstract ProgressPhotoDao progressPhotoDao();
    public abstract BodyMetricsDao bodyMetricsDao();
    
    public static synchronized FitLifeDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                context.getApplicationContext(),
                FitLifeDatabase.class,
                "fitlife_database"
            ).allowMainThreadQueries() // For simplicity in prototype
             .fallbackToDestructiveMigration() // For prototype - recreates DB on version change
             .build();
        }
        return INSTANCE;
    }
}
