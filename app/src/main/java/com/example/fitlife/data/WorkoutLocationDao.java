package com.example.fitlife.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface WorkoutLocationDao {
    @Insert
    long insert(WorkoutLocation location);
    
    @Query("SELECT * FROM workout_locations WHERE userId = :userId")
    List<WorkoutLocation> getLocationsByUser(long userId);
    
    @Query("SELECT * FROM workout_locations WHERE id = :id LIMIT 1")
    WorkoutLocation getLocationById(long id);
    
    @Update
    void update(WorkoutLocation location);
    
    @Query("DELETE FROM workout_locations WHERE id = :id")
    void delete(long id);
}
