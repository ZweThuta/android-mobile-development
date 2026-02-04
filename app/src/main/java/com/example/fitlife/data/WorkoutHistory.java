package com.example.fitlife.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "workout_history",
    foreignKeys = {
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "userId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = WorkoutRoutine.class,
            parentColumns = "id",
            childColumns = "routineId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("userId"), @Index("routineId"), @Index("completedAt")}
)
public class WorkoutHistory {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long userId;
    public long routineId;
    public long completedAt;
    public int duration;
    public String notes;
    public int rating;
    public int exercisesCompleted;
    
    public WorkoutHistory(long userId, long routineId, long completedAt) {
        this.userId = userId;
        this.routineId = routineId;
        this.completedAt = completedAt;
        this.duration = 0;
        this.rating = 0;
        this.exercisesCompleted = 0;
    }
}
