package com.example.fitlife.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "exercises",
    foreignKeys = @ForeignKey(
        entity = WorkoutRoutine.class,
        parentColumns = "id",
        childColumns = "routineId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("routineId")}
)
public class Exercise {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long routineId;
    public String name;
    public String instructions;
    public int sets;
    public int reps;
    public String imagePath;
    public boolean isCompleted;
    
    public Exercise(long routineId, String name, String instructions, int sets, int reps) {
        this.routineId = routineId;
        this.name = name;
        this.instructions = instructions;
        this.sets = sets;
        this.reps = reps;
        this.isCompleted = false;
    }
}
