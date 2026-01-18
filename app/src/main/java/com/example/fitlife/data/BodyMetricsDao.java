package com.example.fitlife.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface BodyMetricsDao {
    @Insert
    long insert(BodyMetrics metrics);
    
    @Query("SELECT * FROM body_metrics WHERE userId = :userId ORDER BY date DESC")
    List<BodyMetrics> getMetricsByUser(long userId);
    
    @Query("SELECT * FROM body_metrics WHERE userId = :userId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    List<BodyMetrics> getMetricsByDateRange(long userId, long startDate, long endDate);
    
    @Query("SELECT * FROM body_metrics WHERE id = :id LIMIT 1")
    BodyMetrics getMetricsById(long id);
    
    @Query("SELECT * FROM body_metrics WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    BodyMetrics getLatestMetrics(long userId);
    
    @Query("SELECT * FROM body_metrics WHERE userId = :userId ORDER BY date ASC LIMIT 1")
    BodyMetrics getFirstMetrics(long userId);
    
    @Update
    void update(BodyMetrics metrics);
    
    @Query("DELETE FROM body_metrics WHERE id = :id")
    void delete(long id);
}
