package com.example.fitlife.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "workout_routines",
    foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("userId")}
)
public class WorkoutRoutine {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long userId;
    public String name;
    public String description;
    public String imagePath;
    public boolean isCompleted;
    public Long locationId;
    
    public WorkoutRoutine(long userId, String name, String description) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.isCompleted = false;
    }
}
