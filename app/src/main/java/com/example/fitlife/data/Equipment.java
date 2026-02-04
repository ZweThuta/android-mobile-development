package com.example.fitlife.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "equipment",
    foreignKeys = @ForeignKey(
        entity = WorkoutRoutine.class,
        parentColumns = "id",
        childColumns = "routineId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("routineId")}
)
public class Equipment {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long routineId;
    public String name;
    public String category;
    public boolean isCompleted;
    
    public Equipment(long routineId, String name, String category) {
        this.routineId = routineId;
        this.name = name;
        this.category = category;
        this.isCompleted = false;
    }
}
