package com.example.fitlife.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface ProgressPhotoDao {
    @Insert
    long insert(ProgressPhoto photo);
    
    @Query("SELECT * FROM progress_photos WHERE userId = :userId ORDER BY date DESC")
    List<ProgressPhoto> getPhotosByUser(long userId);
    
    @Query("SELECT * FROM progress_photos WHERE userId = :userId AND workoutId = :workoutId ORDER BY date DESC")
    List<ProgressPhoto> getPhotosByWorkout(long userId, long workoutId);
    
    @Query("SELECT * FROM progress_photos WHERE id = :id LIMIT 1")
    ProgressPhoto getPhotoById(long id);
    
    @Query("SELECT * FROM progress_photos WHERE userId = :userId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    List<ProgressPhoto> getPhotosByDateRange(long userId, long startDate, long endDate);
    
    @Update
    void update(ProgressPhoto photo);
    
    @Query("DELETE FROM progress_photos WHERE id = :id")
    void delete(long id);
}
