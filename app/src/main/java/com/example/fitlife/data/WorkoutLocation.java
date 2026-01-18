package com.example.fitlife.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "workout_locations",
    foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("userId")}
)
public class WorkoutLocation {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long userId;
    public String name;
    public String address;
    public double latitude;
    public double longitude;
    public String type; // e.g., "gym", "park", "yoga studio"
    
    public WorkoutLocation(long userId, String name, String address, double latitude, double longitude, String type) {
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }
}
