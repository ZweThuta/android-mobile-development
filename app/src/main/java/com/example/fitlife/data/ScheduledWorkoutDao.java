package com.example.fitlife.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface ScheduledWorkoutDao {
    @Insert
    long insert(ScheduledWorkout scheduledWorkout);
    
    @Query("SELECT * FROM scheduled_workouts WHERE userId = :userId ORDER BY scheduledDate ASC, scheduledTime ASC")
    List<ScheduledWorkout> getScheduledWorkoutsByUser(long userId);
    
    @Query("SELECT * FROM scheduled_workouts WHERE userId = :userId AND scheduledDate = :date ORDER BY scheduledTime ASC")
    List<ScheduledWorkout> getScheduledWorkoutsByDate(long userId, String date);
    
    @Query("SELECT * FROM scheduled_workouts WHERE id = :id LIMIT 1")
    ScheduledWorkout getScheduledWorkoutById(long id);
    
    @Query("SELECT * FROM scheduled_workouts WHERE userId = :userId AND scheduledDate >= :startDate AND scheduledDate <= :endDate ORDER BY scheduledDate ASC, scheduledTime ASC")
    List<ScheduledWorkout> getScheduledWorkoutsByDateRange(long userId, String startDate, String endDate);
    
    @Update
    void update(ScheduledWorkout scheduledWorkout);
    
    @Query("DELETE FROM scheduled_workouts WHERE id = :id")
    void delete(long id);
    
    @Query("UPDATE scheduled_workouts SET status = :status WHERE id = :id")
    void updateStatus(long id, String status);
}
