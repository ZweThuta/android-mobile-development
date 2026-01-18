# Quick Guide: Adding Images to FitLife App

## 📁 Storage Locations

### **For Logos & Static Images:**

```
app/src/main/res/drawable/
```

**Add your logo here!**

### **For User Photos:**

Already handled automatically in:

```
Android/data/com.example.fitlife/files/Pictures/
```

---

## 🚀 Quick Steps to Add Logo

### **1. Add Image File**

- Copy your logo/image file
- Navigate to: `app/src/main/res/drawable/`
- Paste the file (e.g., `logo.png`)

### **2. Reference in Code**

**In XML Layout:**

```xml
<ImageView
    android:src="@drawable/logo"
    android:layout_width="200dp"
    android:layout_height="200dp" />
```

**In Java:**

```java
ImageView logoView = findViewById(R.id.ivLogo);
logoView.setImageResource(R.drawable.logo);
```

**Note:** Use filename WITHOUT extension:

- File: `logo.png` → Reference: `@drawable/logo`
- File: `app_logo.jpg` → Reference: `@drawable/app_logo`

---

## 📝 Example: Add Logo to Home Screen

After adding `logo.png` to `drawable/`, you can add it to home screen:

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

---

## ✅ File Requirements

- **Format:** PNG (recommended), JPG, WebP
- **Naming:** lowercase, underscores (e.g., `app_logo.png`)
- **Size:** Any size (Android will scale)
- **Location:** `app/src/main/res/drawable/`

---

## 🎯 Summary

**To add logo:**

1. Copy image to `app/src/main/res/drawable/`
2. Use `@drawable/filename` in XML
3. Done! ✅
