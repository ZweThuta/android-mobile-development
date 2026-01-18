package com.example.fitlife.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface EquipmentDao {
    @Insert
    long insert(Equipment equipment);
    
    @Query("SELECT * FROM equipment WHERE routineId = :routineId")
    List<Equipment> getEquipmentByRoutine(long routineId);
    
    @Query("SELECT * FROM equipment WHERE id = :id LIMIT 1")
    Equipment getEquipmentById(long id);
    
    @Update
    void update(Equipment equipment);
    
    @Query("DELETE FROM equipment WHERE id = :id")
    void delete(long id);
    
    @Query("UPDATE equipment SET isCompleted = :isCompleted WHERE id = :id")
    void markAsCompleted(long id, boolean isCompleted);
    
    @Query("SELECT DISTINCT category FROM equipment WHERE routineId = :routineId")
    List<String> getCategoriesByRoutine(long routineId);
}
