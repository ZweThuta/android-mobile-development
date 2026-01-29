package com.example.fitlife.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Example usage of WorkoutTestData to populate the database with sample workouts.
 * This class demonstrates how to use the test data to create workout routines
 * with exercises and equipment in the FitLife database.
 * 
 * Usage:
 * WorkoutTestDataUsageExample.populateSampleWorkouts(context, userId);
 */
public class WorkoutTestDataUsageExample {
    
    /**
     * Populates the database with all sample workouts for a given user.
     * 
     * @param context Android context
     * @param userId The user ID to associate the workouts with
     */
    public static void populateSampleWorkouts(Context context, long userId) {
        FitLifeDatabase database = FitLifeDatabase.getInstance(context);
        
        // Get all sample workouts
        for (WorkoutTestData.SampleWorkout sampleWorkout : WorkoutTestData.getAllSampleWorkouts()) {
            // Create the workout routine
            WorkoutRoutine routine = WorkoutTestData.createWorkoutRoutineFromSample(sampleWorkout, userId);
            long routineId = database.workoutRoutineDao().insert(routine);
            
            // Add exercises
            for (WorkoutTestData.SampleExercise sampleExercise : sampleWorkout.exercises) {
                Exercise exercise = WorkoutTestData.createExerciseFromSample(sampleExercise, routineId);
                database.exerciseDao().insert(exercise);
            }
            
            // Add equipment
            for (WorkoutTestData.SampleEquipment sampleEquipment : sampleWorkout.equipment) {
                Equipment equipment = WorkoutTestData.createEquipmentFromSample(sampleEquipment, routineId);
                database.equipmentDao().insert(equipment);
            }
        }
    }
    
    /**
     * Populates the database with a specific sample workout.
     * 
     * @param context Android context
     * @param userId The user ID to associate the workout with
     * @param sampleWorkout The sample workout to create
     * @return The created routine ID
     */
    public static long populateSingleWorkout(Context context, long userId, 
                                             WorkoutTestData.SampleWorkout sampleWorkout) {
        FitLifeDatabase database = FitLifeDatabase.getInstance(context);
        
        // Create the workout routine
        WorkoutRoutine routine = WorkoutTestData.createWorkoutRoutineFromSample(sampleWorkout, userId);
        long routineId = database.workoutRoutineDao().insert(routine);
        
        // Add exercises
        for (WorkoutTestData.SampleExercise sampleExercise : sampleWorkout.exercises) {
            Exercise exercise = WorkoutTestData.createExerciseFromSample(sampleExercise, routineId);
            database.exerciseDao().insert(exercise);
        }
        
        // Add equipment
        for (WorkoutTestData.SampleEquipment sampleEquipment : sampleWorkout.equipment) {
            Equipment equipment = WorkoutTestData.createEquipmentFromSample(sampleEquipment, routineId);
            database.equipmentDao().insert(equipment);
        }
        
        return routineId;
    }
    
    /**
     * Example: Create a single workout programmatically
     * This shows how to create a workout without using pre-defined samples
     */
    public static long createCustomWorkoutExample(Context context, long userId) {
        FitLifeDatabase database = FitLifeDatabase.getInstance(context);
        
        // Create workout routine
        WorkoutRoutine routine = new WorkoutRoutine(
            userId,
            "Morning Cardio",
            "Quick 20-minute morning cardio session to start your day"
        );
        long routineId = database.workoutRoutineDao().insert(routine);
        
        // Add exercises
        Exercise exercise1 = new Exercise(
            routineId,
            "Jump Rope",
            "Jump rope continuously for 30 seconds, rest 30 seconds",
            5, 30
        );
        database.exerciseDao().insert(exercise1);
        
        Exercise exercise2 = new Exercise(
            routineId,
            "Burpees",
            "Full burpee: squat, plank, push-up, jump",
            4, 10
        );
        database.exerciseDao().insert(exercise2);
        
        // Add equipment
        Equipment equipment1 = new Equipment(routineId, "Jump Rope", "accessories");
        database.equipmentDao().insert(equipment1);
        
        return routineId;
    }
    
    /**
     * Get the current logged-in user ID from SharedPreferences
     * Helper method for convenience
     */
    public static long getCurrentUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FitLifePrefs", Context.MODE_PRIVATE);
        return prefs.getLong("userId", -1);
    }
}
