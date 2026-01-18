# Image Storage Guide for FitLife App

## 📁 Where to Store Images in Android

### **1. App Logos & UI Graphics** → `res/drawable/`

**Best for:**

- App logos
- Icons
- UI graphics
- Static images used in layouts
- Vector graphics

**Location:**

```
app/src/main/res/drawable/
```

**How to Use:**

```xml
<!-- In XML layouts -->
<ImageView
    android:src="@drawable/logo"
    ... />

<!-- In Java code -->
imageView.setImageResource(R.drawable.logo);
```

**Supported Formats:**

- PNG (recommended)
- JPG/JPEG
- Vector Drawable (XML)
- WebP

---

### **2. App Icons** → `res/mipmap/`

**Best for:**

- App launcher icons (already exists)
- Different density versions

**Location:**

```
app/src/main/res/mipmap-*/
```

**Note:** Already configured for app icons. Use `drawable/` for other graphics.

---

### **3. User Photos (Progress Photos)** → External Storage

**Best for:**

- User-generated photos
- Photos taken with camera
- Photos selected from gallery

**Location:**

```
/storage/emulated/0/Android/data/com.example.fitlife/files/Pictures/
```

**How it Works:**

- Photos are stored in app's private external storage
- Path is saved in database (`photoPath` field)
- Already implemented in `ProgressPhotosActivity`

---

### **4. Assets (Optional)** → `assets/`

**Best for:**

- Large image files
- Files accessed as streams
- Template images

**Location:**

```
app/src/main/assets/
```

**How to Use:**

```java
InputStream is = getAssets().open("logo.png");
Bitmap bitmap = BitmapFactory.decodeStream(is);
```

---

## 🎯 Recommended Structure

### **For Your App:**

```
app/src/main/res/
├── drawable/
│   ├── logo.png              ← App logo
│   ├── logo_white.png        ← White version
│   ├── workout_icon.png      ← Workout icon
│   ├── exercise_placeholder.png
│   └── ic_*.xml              ← Vector icons (optional)
│
├── mipmap-*/
│   └── ic_launcher.*         ← App icon (already exists)
│
└── assets/                    ← Create if needed
    └── templates/            ← Optional: template images
```

---

## 📝 Step-by-Step: Adding Your Logo

### **Step 1: Prepare Your Images**

1. **Logo Requirements:**

   - PNG format (transparent background recommended)
   - Sizes: 512x512px (for high quality)
   - Or use vector drawable (SVG converted to XML)

2. **Naming:**
   - Use lowercase
   - Use underscores: `app_logo.png`
   - No spaces or special characters

### **Step 2: Add to Drawable Folder**

1. **In Android Studio:**

   - Right-click `app/src/main/res/drawable/`
   - Select **New → Image Asset** (for icons)
   - Or **New → Vector Asset** (for vectors)
   - Or manually copy PNG files to the folder

2. **Manual Method:**
   - Copy your logo file
   - Navigate to: `app/src/main/res/drawable/`
   - Paste the file (e.g., `logo.png`)

### **Step 3: Use in Your App**

**In Layout XML:**

```xml
<ImageView
    android:layout_width="200dp"
    android:layout_height="200dp"
    android:src="@drawable/logo"
    android:contentDescription="FitLife Logo" />
```

**In Java Code:**

```java
ImageView logoView = findViewById(R.id.ivLogo);
logoView.setImageResource(R.drawable.logo);
```

---

## 🖼️ Example: Adding Logo to Home Screen

### **Option 1: Replace Welcome Text**

```xml
<ImageView
    android:id="@+id/ivLogo"
    android:layout_width="120dp"
    android:layout_height="120dp"
    android:src="@drawable/logo"
    android:contentDescription="FitLife Logo"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    android:layout_marginTop="32dp" />
```

### **Option 2: Add Above Welcome Text**

```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:gravity="center"
    android:layout_marginTop="32dp">

    <ImageView
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:src="@drawable/logo"
        android:contentDescription="FitLife Logo" />

    <TextView
        android:id="@+id/tvWelcome"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Welcome to FitLife!"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginTop="16dp" />
</LinearLayout>
```

---

## 📸 For Progress Photos (Already Implemented)

**Storage Location:**

- Automatically stored in: `Android/data/com.example.fitlife/files/Pictures/`
- Path saved in database
- No manual management needed

**To View Stored Photos:**

- Use Android Studio's Device File Explorer
- Path: `/storage/emulated/0/Android/data/com.example.fitlife/files/Pictures/`

---

## 🎨 Image Best Practices

### **1. Image Sizes:**

| Use Case     | Recommended Size  | Format     |
| ------------ | ----------------- | ---------- |
| Logo         | 512x512px         | PNG        |
| Icons        | 24dp, 32dp, 48dp  | PNG/Vector |
| Placeholders | 200x200px         | PNG        |
| Backgrounds  | Match screen size | PNG/JPG    |

### **2. Density Versions (Optional):**

For better quality on different screens:

```
res/
├── drawable-mdpi/logo.png    (1x)
├── drawable-hdpi/logo.png    (1.5x)
├── drawable-xhdpi/logo.png   (2x)
├── drawable-xxhdpi/logo.png  (3x)
└── drawable-xxxhdpi/logo.png (4x)
```

**Note:** Android will auto-scale, so one version in `drawable/` is usually enough.

### **3. Vector Drawables (Best Practice):**

**Advantages:**

- Scalable (no pixelation)
- Smaller file size
- One file for all densities

**Create Vector:**

1. Right-click `drawable/` → **New → Vector Asset**
2. Choose **Local file** and select your SVG
3. Or use **Clip Art** for built-in icons

---

## 🔧 Quick Reference

### **Add Image to Drawable:**

1. Copy image file
2. Paste in `app/src/main/res/drawable/`
3. Reference as `@drawable/filename` (without extension)

### **Reference in Code:**

- **XML:** `@drawable/logo`
- **Java:** `R.drawable.logo`
- **Kotlin:** `R.drawable.logo`

### **Check if Image is Added:**

- Look in `app/src/main/res/drawable/` folder
- Should appear in Android Studio's resource browser
- Can reference in XML without errors

---

## ✅ Summary

**For Logos & Graphics:**
👉 **Store in:** `app/src/main/res/drawable/`
👉 **Reference as:** `@drawable/your_image_name`

**For User Photos:**
👉 **Already handled** - stored automatically in external storage
👉 **Path saved in database**

**Quick Steps:**

1. Copy your logo/image to `app/src/main/res/drawable/`
2. Use `@drawable/logo` in XML layouts
3. Use `R.drawable.logo` in Java code

That's it! Your images will be bundled with the app. 📦
