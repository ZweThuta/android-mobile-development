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
    public long completedAt; // Timestamp in milliseconds
    public int duration; // Duration in minutes
    public String notes;
    public int rating; // 1-5 stars
    public int exercisesCompleted; // Number of exercises completed
    
    public WorkoutHistory(long userId, long routineId, long completedAt) {
        this.userId = userId;
        this.routineId = routineId;
        this.completedAt = completedAt;
        this.duration = 0;
        this.rating = 0;
        this.exercisesCompleted = 0;
    }
}
