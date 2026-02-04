package com.example.fitlife.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface WorkoutRoutineDao {
    @Insert
    long insert(WorkoutRoutine routine);
    
    @Query("SELECT * FROM workout_routines WHERE userId = :userId")
    List<WorkoutRoutine> getRoutinesByUser(long userId);
    
    @Query("SELECT * FROM workout_routines WHERE id = :id LIMIT 1")
    WorkoutRoutine getRoutineById(long id);
     
    @Update
    void update(WorkoutRoutine routine);
    
    @Query("DELETE FROM workout_routines WHERE id = :id")
    void delete(long id);
    
    @Query("UPDATE workout_routines SET isCompleted = :isCompleted WHERE id = :id")
    void markAsCompleted(long id, boolean isCompleted);
}
