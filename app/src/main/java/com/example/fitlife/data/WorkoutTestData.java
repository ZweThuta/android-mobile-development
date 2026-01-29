package com.example.fitlife.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class providing meaningful sample test data for creating workouts.
 * This class contains pre-defined workout routines with exercises and equipment
 * that can be used for testing and demonstration purposes.
 */
public class WorkoutTestData {
    
    /**
     * Sample workout routine data structure
     */
    public static class SampleWorkout {
        public String name;
        public String description;
        public List<SampleExercise> exercises;
        public List<SampleEquipment> equipment;
        public String locationType; // Optional: "gym", "home", "park", "yoga studio"
        
        public SampleWorkout(String name, String description, String locationType) {
            this.name = name;
            this.description = description;
            this.locationType = locationType;
            this.exercises = new ArrayList<>();
            this.equipment = new ArrayList<>();
        }
    }
    
    public static class SampleExercise {
        public String name;
        public String instructions;
        public int sets;
        public int reps;
        
        public SampleExercise(String name, String instructions, int sets, int reps) {
            this.name = name;
            this.instructions = instructions;
            this.sets = sets;
            this.reps = reps;
        }
    }
    
    public static class SampleEquipment {
        public String name;
        public String category;
        
        public SampleEquipment(String name, String category) {
            this.name = name;
            this.category = category;
        }
    }
    
    /**
     * Get a list of all sample workouts
     */
    public static List<SampleWorkout> getAllSampleWorkouts() {
        List<SampleWorkout> workouts = new ArrayList<>();
        
        workouts.add(getFullBodyStrengthWorkout());
        workouts.add(getUpperBodyWorkout());
        workouts.add(getLowerBodyWorkout());
        workouts.add(getCardioHIITWorkout());
        workouts.add(getYogaFlowWorkout());
        workouts.add(getHomeBodyweightWorkout());
        workouts.add(getChestAndTricepsWorkout());
        workouts.add(getBackAndBicepsWorkout());
        workouts.add(getLegDayWorkout());
        workouts.add(getCoreStrengthWorkout());
        
        return workouts;
    }
    
    /**
     * Full Body Strength Training Workout
     */
    public static SampleWorkout getFullBodyStrengthWorkout() {
        SampleWorkout workout = new SampleWorkout(
            "Full Body Strength",
            "A comprehensive full-body workout targeting all major muscle groups. Perfect for building overall strength and muscle mass. Rest 60-90 seconds between sets.",
            "gym"
        );
        
        workout.exercises.add(new SampleExercise(
            "Barbell Squat",
            "Stand with feet shoulder-width apart, bar on upper back. Lower down by bending knees and hips, keeping back straight. Go down until thighs are parallel to floor, then push back up.",
            4, 8
        ));
        
        workout.exercises.add(new SampleExercise(
            "Bench Press",
            "Lie on bench, grip bar slightly wider than shoulders. Lower bar to chest with control, pause briefly, then press up explosively. Keep feet flat on floor.",
            4, 8
        ));
        
        workout.exercises.add(new SampleExercise(
            "Bent-Over Barbell Row",
            "Bend at hips, keep back straight. Pull bar to lower chest/upper abdomen, squeezing shoulder blades together. Lower with control.",
            3, 10
        ));
        
        workout.exercises.add(new SampleExercise(
            "Overhead Press",
            "Stand with feet hip-width apart. Press bar from shoulder level to overhead, keeping core tight. Lower with control.",
            3, 8
        ));
        
        workout.exercises.add(new SampleExercise(
            "Romanian Deadlift",
            "Hold bar with overhand grip. Hinge at hips, keeping legs mostly straight. Lower bar along legs until you feel stretch in hamstrings, then return.",
            3, 10
        ));
        
        workout.equipment.add(new SampleEquipment("Barbell", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Weight Plates", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Bench", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Power Rack", "strength equipment"));
        
        return workout;
    }
    
    /**
     * Upper Body Focus Workout
     */
    public static SampleWorkout getUpperBodyWorkout() {
        SampleWorkout workout = new SampleWorkout(
            "Upper Body Blast",
            "Intensive upper body workout focusing on chest, shoulders, back, and arms. Great for building upper body strength and definition.",
            "gym"
        );
        
        workout.exercises.add(new SampleExercise(
            "Incline Dumbbell Press",
            "Set bench to 30-45 degree incline. Press dumbbells from chest level upward and slightly forward. Lower with control.",
            4, 10
        ));
        
        workout.exercises.add(new SampleExercise(
            "Pull-Ups",
            "Hang from bar with palms facing away. Pull body up until chin clears bar. Lower with control. Use assistance if needed.",
            3, 8
        ));
        
        workout.exercises.add(new SampleExercise(
            "Lateral Raises",
            "Stand holding dumbbells at sides. Raise arms out to sides until parallel to floor. Lower slowly.",
            3, 12
        ));
        
        workout.exercises.add(new SampleExercise(
            "Cable Flyes",
            "Set cables at chest height. With slight bend in elbows, bring handles together in front of chest. Return with control.",
            3, 12
        ));
        
        workout.exercises.add(new SampleExercise(
            "Barbell Curls",
            "Stand holding barbell with underhand grip. Curl bar to chest, squeezing biceps. Lower with control.",
            3, 10
        ));
        
        workout.exercises.add(new SampleExercise(
            "Tricep Dips",
            "Sit on bench edge, hands gripping edge. Slide forward, lower body by bending arms, then push back up.",
            3, 12
        ));
        
        workout.equipment.add(new SampleEquipment("Dumbbells", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Pull-Up Bar", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Cable Machine", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Incline Bench", "strength equipment"));
        
        return workout;
    }
    
    /**
     * Lower Body Focus Workout
     */
    public static SampleWorkout getLowerBodyWorkout() {
        SampleWorkout workout = new SampleWorkout(
            "Leg Day Power",
            "Comprehensive lower body workout targeting quads, hamstrings, glutes, and calves. Prepare for DOMS (Delayed Onset Muscle Soreness)!",
            "gym"
        );
        
        workout.exercises.add(new SampleExercise(
            "Back Squat",
            "Place bar on upper back. Feet shoulder-width apart. Lower until thighs parallel to floor, then drive up through heels.",
            5, 6
        ));
        
        workout.exercises.add(new SampleExercise(
            "Romanian Deadlift",
            "Hinge at hips, keep back straight. Lower bar along legs until hamstring stretch, then return to standing.",
            4, 8
        ));
        
        workout.exercises.add(new SampleExercise(
            "Leg Press",
            "Sit in leg press machine, feet shoulder-width on platform. Lower weight by bending knees, then press up explosively.",
            4, 12
        ));
        
        workout.exercises.add(new SampleExercise(
            "Walking Lunges",
            "Step forward into lunge position, both knees at 90 degrees. Push off front foot, step forward with back leg. Repeat.",
            3, 12
        ));
        
        workout.exercises.add(new SampleExercise(
            "Calf Raises",
            "Stand on platform or step, heels hanging off. Rise up on toes as high as possible, then lower slowly.",
            4, 15
        ));
        
        workout.exercises.add(new SampleExercise(
            "Leg Curls",
            "Lie face down on leg curl machine. Curl heels toward glutes, squeezing hamstrings. Lower with control.",
            3, 12
        ));
        
        workout.equipment.add(new SampleEquipment("Barbell", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Leg Press Machine", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Leg Curl Machine", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Weight Plates", "strength equipment"));
        
        return workout;
    }
    
    /**
     * Cardio HIIT Workout
     */
    public static SampleWorkout getCardioHIITWorkout() {
        SampleWorkout workout = new SampleWorkout(
            "HIIT Cardio Blast",
            "High-intensity interval training workout to boost cardiovascular fitness and burn calories. 30 seconds work, 30 seconds rest.",
            "gym"
        );
        
        workout.exercises.add(new SampleExercise(
            "Burpees",
            "Squat down, place hands on floor. Jump feet back to plank. Do push-up. Jump feet forward, then jump up with arms overhead.",
            5, 10
        ));
        
        workout.exercises.add(new SampleExercise(
            "Mountain Climbers",
            "Start in plank position. Alternately bring knees to chest rapidly, keeping core tight and hips level.",
            4, 20
        ));
        
        workout.exercises.add(new SampleExercise(
            "Jumping Jacks",
            "Stand with feet together, arms at sides. Jump while spreading legs and raising arms overhead. Return to start.",
            4, 30
        ));
        
        workout.exercises.add(new SampleExercise(
            "High Knees",
            "Run in place, bringing knees up to chest level. Pump arms naturally. Maintain quick pace.",
            4, 30
        ));
        
        workout.exercises.add(new SampleExercise(
            "Jump Squats",
            "Perform regular squat, then explosively jump up. Land softly and immediately go into next squat.",
            4, 15
        ));
        
        workout.exercises.add(new SampleExercise(
            "Plank Hold",
            "Hold plank position with straight body from head to heels. Keep core tight, don't let hips sag.",
            3, 60
        ));
        
        workout.equipment.add(new SampleEquipment("Exercise Mat", "mats"));
        workout.equipment.add(new SampleEquipment("Timer", "accessories"));
        
        return workout;
    }
    
    /**
     * Yoga Flow Workout
     */
    public static SampleWorkout getYogaFlowWorkout() {
        SampleWorkout workout = new SampleWorkout(
            "Vinyasa Flow",
            "A flowing yoga sequence connecting breath with movement. Improves flexibility, balance, and mental clarity. Hold each pose for 5-8 breaths.",
            "yoga studio"
        );
        
        workout.exercises.add(new SampleExercise(
            "Sun Salutation A",
            "Start in Mountain Pose. Inhale arms up, exhale forward fold. Inhale half lift, exhale plank. Lower to Chaturanga, Upward Dog, Downward Dog. Step forward, repeat.",
            3, 1
        ));
        
        workout.exercises.add(new SampleExercise(
            "Warrior I",
            "Step one foot back, turn it out 45 degrees. Front knee over ankle, back leg straight. Arms reach up, gaze forward.",
            2, 1
        ));
        
        workout.exercises.add(new SampleExercise(
            "Warrior II",
            "From Warrior I, open hips to side. Front arm forward, back arm back, both parallel to floor. Gaze over front hand.",
            2, 1
        ));
        
        workout.exercises.add(new SampleExercise(
            "Triangle Pose",
            "From Warrior II, straighten front leg. Reach front hand down to shin or block, other arm up. Open chest.",
            2, 1
        ));
        
        workout.exercises.add(new SampleExercise(
            "Tree Pose",
            "Stand on one leg, place other foot on inner thigh or calf (not knee). Bring hands to heart or overhead. Find balance.",
            2, 1
        ));
        
        workout.exercises.add(new SampleExercise(
            "Child's Pose",
            "Kneel, sit back on heels. Fold forward, arms extended or by sides. Rest forehead on mat. Breathe deeply.",
            3, 1
        ));
        
        workout.equipment.add(new SampleEquipment("Yoga Mat", "mats"));
        workout.equipment.add(new SampleEquipment("Yoga Blocks", "accessories"));
        workout.equipment.add(new SampleEquipment("Yoga Strap", "accessories"));
        
        return workout;
    }
    
    /**
     * Home Bodyweight Workout
     */
    public static SampleWorkout getHomeBodyweightWorkout() {
        SampleWorkout workout = new SampleWorkout(
            "Home Bodyweight Circuit",
            "No equipment needed! Perfect for home workouts. Complete all exercises, rest 60 seconds, repeat circuit 3 times.",
            "home"
        );
        
        workout.exercises.add(new SampleExercise(
            "Push-Ups",
            "Start in plank position. Lower body until chest nearly touches floor, keeping body straight. Push back up.",
            3, 15
        ));
        
        workout.exercises.add(new SampleExercise(
            "Bodyweight Squats",
            "Stand with feet shoulder-width apart. Lower down as if sitting in chair, keeping knees behind toes. Return to standing.",
            3, 20
        ));
        
        workout.exercises.add(new SampleExercise(
            "Plank",
            "Hold body in straight line from head to heels. Support on forearms and toes. Keep core engaged.",
            3, 45
        ));
        
        workout.exercises.add(new SampleExercise(
            "Lunges",
            "Step forward into lunge, both knees at 90 degrees. Push back to start. Alternate legs.",
            3, 12
        ));
        
        workout.exercises.add(new SampleExercise(
            "Bicycle Crunches",
            "Lie on back, hands behind head. Bring opposite knee to opposite elbow, alternating sides. Keep core engaged.",
            3, 20
        ));
        
        workout.exercises.add(new SampleExercise(
            "Glute Bridges",
            "Lie on back, knees bent, feet flat. Lift hips up, squeezing glutes. Lower with control.",
            3, 15
        ));
        
        workout.equipment.add(new SampleEquipment("Exercise Mat", "mats"));
        
        return workout;
    }
    
    /**
     * Chest and Triceps Workout
     */
    public static SampleWorkout getChestAndTricepsWorkout() {
        SampleWorkout workout = new SampleWorkout(
            "Chest & Triceps",
            "Focused workout for building chest and triceps strength. Perfect for upper body push day.",
            "gym"
        );
        
        workout.exercises.add(new SampleExercise(
            "Flat Bench Press",
            "Lie on flat bench, grip bar slightly wider than shoulders. Lower to chest, pause, press up explosively.",
            4, 8
        ));
        
        workout.exercises.add(new SampleExercise(
            "Incline Dumbbell Press",
            "Set bench to 30-45 degrees. Press dumbbells from chest upward and slightly forward.",
            4, 10
        ));
        
        workout.exercises.add(new SampleExercise(
            "Cable Crossover",
            "Set cables high. With slight bend in elbows, bring handles together below chest level.",
            3, 12
        ));
        
        workout.exercises.add(new SampleExercise(
            "Close-Grip Bench Press",
            "Grip bar narrower than shoulder-width. Lower to lower chest, press up focusing on triceps.",
            3, 10
        ));
        
        workout.exercises.add(new SampleExercise(
            "Overhead Tricep Extension",
            "Hold dumbbell or cable overhead. Lower behind head by bending elbows, then extend up.",
            3, 12
        ));
        
        workout.exercises.add(new SampleExercise(
            "Tricep Rope Pushdown",
            "Stand at cable machine, rope attachment. Push down until arms fully extended, squeeze triceps.",
            3, 15
        ));
        
        workout.equipment.add(new SampleEquipment("Barbell", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Dumbbells", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Cable Machine", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Bench", "strength equipment"));
        
        return workout;
    }
    
    /**
     * Back and Biceps Workout
     */
    public static SampleWorkout getBackAndBicepsWorkout() {
        SampleWorkout workout = new SampleWorkout(
            "Back & Biceps",
            "Comprehensive back and biceps workout for building width and thickness. Focus on mind-muscle connection.",
            "gym"
        );
        
        workout.exercises.add(new SampleExercise(
            "Deadlift",
            "Stand with feet hip-width, bar over mid-foot. Hinge at hips, grip bar. Drive through heels, stand up tall, squeezing glutes.",
            4, 5
        ));
        
        workout.exercises.add(new SampleExercise(
            "Pull-Ups",
            "Hang from bar, palms facing away. Pull body up until chin clears bar. Lower with control.",
            4, 8
        ));
        
        workout.exercises.add(new SampleExercise(
            "Barbell Row",
            "Bend at hips, keep back straight. Pull bar to lower chest/upper abdomen, squeezing shoulder blades.",
            4, 8
        ));
        
        workout.exercises.add(new SampleExercise(
            "Lat Pulldown",
            "Sit at lat pulldown machine. Pull bar to upper chest, squeezing lats. Control the negative.",
            3, 12
        ));
        
        workout.exercises.add(new SampleExercise(
            "Barbell Curls",
            "Stand holding barbell with underhand grip. Curl to chest, squeeze biceps. Lower slowly.",
            4, 10
        ));
        
        workout.exercises.add(new SampleExercise(
            "Hammer Curls",
            "Hold dumbbells with neutral grip (palms facing each other). Curl up, squeeze biceps and brachialis.",
            3, 12
        ));
        
        workout.equipment.add(new SampleEquipment("Barbell", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Pull-Up Bar", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Lat Pulldown Machine", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Dumbbells", "strength equipment"));
        
        return workout;
    }
    
    /**
     * Leg Day Workout
     */
    public static SampleWorkout getLegDayWorkout() {
        SampleWorkout workout = new SampleWorkout(
            "Ultimate Leg Day",
            "Intensive leg workout for maximum muscle growth. Focus on proper form and full range of motion.",
            "gym"
        );
        
        workout.exercises.add(new SampleExercise(
            "Barbell Back Squat",
            "Bar on upper back. Lower until thighs parallel to floor, keeping knees tracking over toes. Drive up through heels.",
            5, 6
        ));
        
        workout.exercises.add(new SampleExercise(
            "Romanian Deadlift",
            "Hinge at hips, keep back straight. Lower bar along legs until hamstring stretch, return to standing.",
            4, 8
        ));
        
        workout.exercises.add(new SampleExercise(
            "Leg Press",
            "Sit in machine, feet shoulder-width on platform. Lower weight, then press up explosively.",
            4, 15
        ));
        
        workout.exercises.add(new SampleExercise(
            "Walking Lunges",
            "Step forward into lunge, both knees at 90 degrees. Push off, step forward with back leg.",
            3, 12
        ));
        
        workout.exercises.add(new SampleExercise(
            "Leg Extensions",
            "Sit in leg extension machine. Extend legs until fully straight, squeezing quads. Lower with control.",
            3, 15
        ));
        
        workout.exercises.add(new SampleExercise(
            "Standing Calf Raises",
            "Stand on platform, heels hanging off. Rise up on toes as high as possible, lower slowly.",
            4, 20
        ));
        
        workout.equipment.add(new SampleEquipment("Barbell", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Leg Press Machine", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Leg Extension Machine", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Weight Plates", "strength equipment"));
        
        return workout;
    }
    
    /**
     * Core Strength Workout
     */
    public static SampleWorkout getCoreStrengthWorkout() {
        SampleWorkout workout = new SampleWorkout(
            "Core Strength Builder",
            "Comprehensive core workout targeting all abdominal muscles and obliques. Great for stability and strength.",
            "gym"
        );
        
        workout.exercises.add(new SampleExercise(
            "Plank",
            "Hold body in straight line, support on forearms and toes. Keep core tight, don't let hips sag.",
            3, 60
        ));
        
        workout.exercises.add(new SampleExercise(
            "Russian Twists",
            "Sit leaning back slightly, knees bent. Hold weight, rotate torso side to side, touching weight to floor each side.",
            3, 20
        ));
        
        workout.exercises.add(new SampleExercise(
            "Hanging Leg Raises",
            "Hang from pull-up bar. Raise legs up, keeping them straight. Lower with control.",
            3, 12
        ));
        
        workout.exercises.add(new SampleExercise(
            "Cable Crunches",
            "Kneel at cable machine, rope attachment. Crunch down, bringing elbows toward knees. Squeeze abs.",
            3, 15
        ));
        
        workout.exercises.add(new SampleExercise(
            "Side Plank",
            "Support on one forearm and side of foot. Keep body straight, hold. Switch sides.",
            3, 45
        ));
        
        workout.exercises.add(new SampleExercise(
            "Dead Bug",
            "Lie on back, arms up, knees at 90 degrees. Lower opposite arm and leg simultaneously, return, alternate.",
            3, 12
        ));
        
        workout.equipment.add(new SampleEquipment("Exercise Mat", "mats"));
        workout.equipment.add(new SampleEquipment("Pull-Up Bar", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Cable Machine", "strength equipment"));
        workout.equipment.add(new SampleEquipment("Medicine Ball", "accessories"));
        
        return workout;
    }
    
    /**
     * Helper method to create WorkoutRoutine from sample data
     * Note: userId should be set when actually creating the routine
     */
    public static WorkoutRoutine createWorkoutRoutineFromSample(SampleWorkout sample, long userId) {
        return new WorkoutRoutine(userId, sample.name, sample.description);
    }
    
    /**
     * Helper method to create Exercise from sample data
     * Note: routineId should be set when actually creating the exercise
     */
    public static Exercise createExerciseFromSample(SampleExercise sample, long routineId) {
        return new Exercise(routineId, sample.name, sample.instructions, sample.sets, sample.reps);
    }
    
    /**
     * Helper method to create Equipment from sample data
     * Note: routineId should be set when actually creating the equipment
     */
    public static Equipment createEquipmentFromSample(SampleEquipment sample, long routineId) {
        return new Equipment(routineId, sample.name, sample.category);
    }
}
