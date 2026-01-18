package com.example.fitlife.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "progress_photos",
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
            childColumns = "workoutId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("userId"), @Index("workoutId")}
)
public class ProgressPhoto {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long userId;
    public Long workoutId; // Optional - can be null for general progress photos
    public String photoPath; // Path to the photo file
    public long date; // Timestamp in milliseconds
    public String notes;
    public String type; // "before", "after", "progress"
    public double weight; // Optional weight at time of photo
    
    public ProgressPhoto(long userId, String photoPath, long date) {
        this.userId = userId;
        this.photoPath = photoPath;
        this.date = date;
        this.type = "progress";
    }
}
