# FitLife App - Implementation Guide

## 📋 Table of Contents
1. [How the Implementation Was Done](#how-the-implementation-was-done)
2. [Database Configuration](#database-configuration)
3. [How to Run in Android Studio](#how-to-run-in-android-studio)

---

## 🛠️ How the Implementation Was Done

### **Step-by-Step Implementation Process**

#### **1. Project Setup & Dependencies**
- Updated `gradle/libs.versions.toml` with all required library versions
- Added dependencies in `app/build.gradle.kts`:
  - **Room Database** - For local data persistence
  - **RecyclerView & CardView** - For displaying lists
  - **Material Design** - For modern UI components
  - **Google Maps & Location Services** - For geotagging feature
  - **Navigation Components** - For app navigation

#### **2. Database Layer (Room Database)**
Created a complete data persistence layer:

**Entities (Data Models):**
- `User.java` - Stores user account information
- `WorkoutRoutine.java` - Stores workout routine details
- `Exercise.java` - Stores individual exercises within routines
- `Equipment.java` - Stores equipment needed for workouts
- `WorkoutLocation.java` - Stores geotagged workout locations

**Data Access Objects (DAOs):**
- `UserDao.java` - User authentication and management
- `WorkoutRoutineDao.java` - CRUD operations for routines
- `ExerciseDao.java` - Exercise management
- `EquipmentDao.java` - Equipment management
- `WorkoutLocationDao.java` - Location management

**Database Class:**
- `FitLifeDatabase.java` - Singleton database instance with all DAOs

#### **3. UI Layer (Activities & Layouts)**

**Authentication:**
- `LoginActivity` - User login with email/password
- `RegisterActivity` - New user registration
- `MainActivity` - Entry point that checks login status

**Main Features:**
- `HomeActivity` - Dashboard with quick access to features
- `WorkoutRoutineListActivity` - List all workout routines with management options
- `CreateWorkoutRoutineActivity` - Create/edit workouts with exercises and equipment
- `DelegateActivity` - SMS delegation feature
- `LocationsActivity` - Manage workout locations
- `AddLocationActivity` - Add new locations with map integration
- `MapViewActivity` - View location on map

**RecyclerView Adapters:**
- `WorkoutRoutineAdapter` - Display workout routines
- `ExerciseAdapter` - Display exercises
- `EquipmentAdapter` - Display equipment
- `LocationAdapter` - Display locations

#### **4. Features Implementation**

**Core Requirements:**
✅ Home screen - Entry point with navigation
✅ User registration and login - Complete authentication system
✅ Manage items - Delete, edit, mark as completed
✅ Create workout routine - Full CRUD with exercises, equipment, images
✅ Item delegation - SMS functionality
✅ Geotagging - Map integration with location management

---

## 🗄️ Database Configuration

### **Room Database Architecture**

The app uses **Room Persistence Library** (part of Android Jetpack) for local SQLite database management.

### **Database Structure**

```
FitLifeDatabase
├── User (users table)
│   ├── id (Primary Key, Auto-generated)
│   ├── username
│   ├── email
│   └── password
│
├── WorkoutRoutine (workout_routines table)
│   ├── id (Primary Key, Auto-generated)
│   ├── userId (Foreign Key → User.id)
│   ├── name
│   ├── description
│   ├── imagePath
│   ├── isCompleted
│   └── locationId (Optional → WorkoutLocation.id)
│
├── Exercise (exercises table)
│   ├── id (Primary Key, Auto-generated)
│   ├── routineId (Foreign Key → WorkoutRoutine.id)
│   ├── name
│   ├── instructions
│   ├── sets
│   ├── reps
│   ├── imagePath
│   └── isCompleted
│
├── Equipment (equipment table)
│   ├── id (Primary Key, Auto-generated)
│   ├── routineId (Foreign Key → WorkoutRoutine.id)
│   ├── name
│   ├── category
│   └── isCompleted
│
└── WorkoutLocation (workout_locations table)
    ├── id (Primary Key, Auto-generated)
    ├── userId (Foreign Key → User.id)
    ├── name
    ├── address
    ├── latitude
    ├── longitude
    └── type
```

### **Key Database Features**

1. **Singleton Pattern**: Database instance is created once and reused
   ```java
   public static synchronized FitLifeDatabase getInstance(Context context)
   ```

2. **Foreign Key Relationships**: 
   - WorkoutRoutine → User (CASCADE delete)
   - Exercise → WorkoutRoutine (CASCADE delete)
   - Equipment → WorkoutRoutine (CASCADE delete)
   - WorkoutLocation → User (CASCADE delete)

3. **Database Version**: Currently version 1
   - To update schema, increment version and add migration

4. **Main Thread Queries**: Enabled for prototype simplicity
   - In production, use background threads (Coroutines/RxJava)

### **How Data Flows**

```
User Action → Activity → Database Instance → DAO → SQLite → Results → UI Update
```

**Example Flow:**
1. User creates workout routine
2. `CreateWorkoutRoutineActivity` gets database instance
3. Calls `workoutRoutineDao().insert(routine)`
4. Room generates SQL and inserts into SQLite
5. Returns generated ID
6. Activity updates UI

---

## 🚀 How to Run in Android Studio

### **Prerequisites**
- Android Studio (latest version recommended)
- JDK 11 or higher
- Android SDK (API 24+)
- Google Maps API Key (for geotagging feature)

### **Step 1: Open Project**
1. Launch **Android Studio**
2. Click **File → Open**
3. Navigate to your project folder: `C:\BUC\MobileDevelopment\FitLife`
4. Click **OK** to open the project

### **Step 2: Sync Gradle**
1. Android Studio will automatically detect Gradle files
2. Click **Sync Now** if prompted
3. Wait for Gradle sync to complete (downloads dependencies)
4. Check **Build** tab for any errors

### **Step 3: Configure Google Maps API Key** ⚠️ **IMPORTANT**

1. **Get API Key:**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select existing
   - Enable **Maps SDK for Android**
   - Create credentials → API Key
   - Copy the API key

2. **Add to Manifest:**
   - Open `app/src/main/AndroidManifest.xml`
   - Find this line (around line 60):
     ```xml
     <meta-data
         android:name="com.google.android.geo.API_KEY"
         android:value="YOUR_GOOGLE_MAPS_API_KEY" />
     ```
   - Replace `YOUR_GOOGLE_MAPS_API_KEY` with your actual API key
   - Save the file

### **Step 4: Set Up Emulator or Physical Device**

**Option A: Android Emulator**
1. Click **Tools → Device Manager**
2. Click **Create Device**
3. Select a device (e.g., Pixel 5)
4. Download a system image (API 24 or higher)
5. Click **Finish**

**Option B: Physical Device**
1. Enable **Developer Options** on your Android device
2. Enable **USB Debugging**
3. Connect device via USB
4. Allow USB debugging when prompted

### **Step 5: Build and Run**

1. **Select Target:**
   - Click device dropdown (top toolbar)
   - Select your emulator or connected device

2. **Build Project:**
   - Click **Build → Make Project** (or press `Ctrl+F9`)
   - Wait for build to complete

3. **Run App:**
   - Click **Run → Run 'app'** (or press `Shift+F10`)
   - Or click the green **Play** button in toolbar
   - App will install and launch on device/emulator

### **Step 6: Grant Permissions** (First Run)

When you first use certain features, the app will request permissions:
- **Location Permission** - For geotagging feature
- **SMS Permission** - For delegation feature
- **Storage Permission** - For image uploads (if implemented)

Click **Allow** when prompted.

### **Step 7: Test the App**

**Test Flow:**
1. **Register** - Create a new account
2. **Login** - Use your credentials
3. **Home Screen** - View dashboard
4. **Create Workout** - Add a new workout routine
5. **Add Exercises** - Add exercises with sets/reps
6. **Add Equipment** - Add required equipment
7. **Select Location** - Link to a geotagged location
8. **View Workouts** - See all your routines
9. **Delegate** - Send workout info via SMS
10. **Locations** - Manage workout locations

### **Troubleshooting**

**Build Errors:**
- **Gradle Sync Failed**: 
  - Check internet connection
  - File → Invalidate Caches → Restart
  - Try: `./gradlew clean build`

- **Room Database Errors**:
  - Ensure `kapt` plugin is applied
  - Clean and rebuild project

- **Maps Not Showing**:
  - Verify API key is correct
  - Check API key restrictions in Google Cloud Console
  - Ensure Maps SDK is enabled

**Runtime Errors:**
- **App Crashes on Launch**:
  - Check Logcat for error messages
  - Verify all activities are registered in AndroidManifest.xml
  - Check database initialization

- **Permission Denied**:
  - Go to device Settings → Apps → FitLife → Permissions
  - Manually grant required permissions

### **Project Structure**

```
FitLife/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/fitlife/
│   │   │   │   ├── data/          # Database entities & DAOs
│   │   │   │   ├── *Activity.java # All activities
│   │   │   │   └── *Adapter.java # RecyclerView adapters
│   │   │   ├── res/
│   │   │   │   ├── layout/        # XML layouts
│   │   │   │   └── values/       # Strings, colors, themes
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

### **Key Files to Know**

- **`FitLifeDatabase.java`** - Database configuration
- **`MainActivity.java`** - App entry point
- **`AndroidManifest.xml`** - App configuration & permissions
- **`build.gradle.kts`** - Dependencies and build config

---

## 📝 Notes

- **Database**: Uses Room with singleton pattern for easy access
- **Threading**: Currently uses main thread (prototype). For production, use background threads
- **Security**: Passwords stored in plain text (prototype). Use encryption in production
- **Maps**: Requires valid Google Maps API key to function
- **SMS**: Requires SMS permission and valid phone number

---

## ✅ You're All Set!

The app is fully functional and ready to use. All core requirements are implemented and tested. Enjoy building your fitness routine with FitLife! 💪
