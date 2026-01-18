package com.example.fitlife.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WorkoutHistoryDao {
    @Insert
    long insert(WorkoutHistory workoutHistory);
    
    @Query("SELECT * FROM workout_history WHERE userId = :userId ORDER BY completedAt DESC")
    List<WorkoutHistory> getWorkoutHistoryByUser(long userId);
    
    @Query("SELECT * FROM workout_history WHERE userId = :userId AND completedAt >= :startTime AND completedAt <= :endTime ORDER BY completedAt DESC")
    List<WorkoutHistory> getWorkoutHistoryByDateRange(long userId, long startTime, long endTime);
    
    @Query("SELECT COUNT(*) FROM workout_history WHERE userId = :userId")
    int getTotalWorkoutsCompleted(long userId);
    
    @Query("SELECT COUNT(*) FROM workout_history WHERE userId = :userId AND completedAt >= :startTime")
    int getWorkoutsCompletedSince(long userId, long startTime);
    
    @Query("SELECT SUM(duration) FROM workout_history WHERE userId = :userId")
    Integer getTotalWorkoutDuration(long userId);
    
    @Query("SELECT * FROM workout_history WHERE userId = :userId AND routineId = :routineId ORDER BY completedAt DESC LIMIT 1")
    WorkoutHistory getLastWorkoutForRoutine(long userId, long routineId);
    
    @Query("SELECT COUNT(*) FROM workout_history WHERE userId = :userId AND routineId = :routineId")
    int getWorkoutCountForRoutine(long userId, long routineId);
    
    @Query("SELECT AVG(rating) FROM workout_history WHERE userId = :userId AND rating > 0")
    Double getAverageRating(long userId);
}
