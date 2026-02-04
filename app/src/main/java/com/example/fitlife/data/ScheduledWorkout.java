package com.example.fitlife.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "scheduled_workouts",
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
    indices = {@Index("userId"), @Index("routineId")}
)
public class ScheduledWorkout {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long userId;
    public long routineId;
    public String scheduledDate;
    public String scheduledTime;
    public String status;
    public long reminderTime;
    public String notes;
    public ScheduledWorkout(long userId, long routineId, String scheduledDate, String scheduledTime) {
        this.userId = userId;
        this.routineId = routineId;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.status = "scheduled";
        this.reminderTime = 15;
    }
}
