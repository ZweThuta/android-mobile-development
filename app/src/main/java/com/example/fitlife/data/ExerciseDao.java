package com.example.fitlife.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface ExerciseDao {
    @Insert
    long insert(Exercise exercise);
    
    @Query("SELECT * FROM exercises WHERE routineId = :routineId")
    List<Exercise> getExercisesByRoutine(long routineId);
    
    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    Exercise getExerciseById(long id);
    
    @Update
    void update(Exercise exercise);
    
    @Query("DELETE FROM exercises WHERE id = :id")
    void delete(long id);
    
    @Query("UPDATE exercises SET isCompleted = :isCompleted WHERE id = :id")
    void markAsCompleted(long id, boolean isCompleted);
}
