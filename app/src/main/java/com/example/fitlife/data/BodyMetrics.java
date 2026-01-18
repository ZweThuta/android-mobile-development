package com.example.fitlife.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "body_metrics",
    foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("userId"), @Index("date")}
)
public class BodyMetrics {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long userId;
    public long date; // Timestamp in milliseconds
    public double weight; // in kg
    public double bodyFat; // percentage (0-100)
    public double chest; // in cm
    public double waist; // in cm
    public double hips; // in cm
    public double leftArm; // in cm
    public double rightArm; // in cm
    public double leftThigh; // in cm
    public double rightThigh; // in cm
    public double height; // in cm (stored once, but can be updated)
    public String notes;
    
    public BodyMetrics(long userId, long date, double weight, double height) {
        this.userId = userId;
        this.date = date;
        this.weight = weight;
        this.height = height;
        this.bodyFat = 0;
        this.chest = 0;
        this.waist = 0;
        this.hips = 0;
        this.leftArm = 0;
        this.rightArm = 0;
        this.leftThigh = 0;
        this.rightThigh = 0;
    }
    
    // Calculate BMI
    public double getBMI() {
        if (height > 0) {
            double heightInMeters = height / 100.0;
            return weight / (heightInMeters * heightInMeters);
        }
        return 0;
    }
}
