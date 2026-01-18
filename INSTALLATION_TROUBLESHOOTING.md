# Installation Troubleshooting Guide

## Error: "Can't find service: package" - Solution Steps

This error typically means Android Studio can't communicate with your device/emulator. Follow these steps:

---

## 🔧 Quick Fixes (Try in Order)

### **Step 1: Check Device Connection**

**For Physical Device:**
1. Unplug and replug your USB cable
2. On your phone, check if you see "Allow USB debugging?" notification
3. Click **"Allow"** or **"Always allow from this computer"**
4. Make sure USB debugging is enabled:
   - Settings → About Phone → Tap "Build Number" 7 times
   - Settings → Developer Options → Enable "USB Debugging"

**For Emulator:**
1. Make sure emulator is fully started (wait for home screen)
2. Check if emulator appears in device list (top toolbar dropdown)

---

### **Step 2: Restart ADB (Android Debug Bridge)**

1. In Android Studio, open **Terminal** (bottom panel)
2. Run these commands one by one:
   ```bash
   adb kill-server
   adb start-server
   adb devices
   ```
3. You should see your device listed
4. If device shows "unauthorized", check your phone for permission prompt

---

### **Step 3: Restart Android Studio**

1. **File → Invalidate Caches → Invalidate and Restart**
2. Wait for Android Studio to restart
3. Try running again

---

### **Step 4: Check Device Storage**

1. On your device, go to **Settings → Storage**
2. Make sure you have at least **500MB free space**
3. If low on storage, free up space

---

### **Step 5: Clean and Rebuild**

1. **Build → Clean Project**
2. Wait for clean to complete
3. **Build → Rebuild Project**
4. Wait for rebuild
5. Try running again

---

### **Step 6: Check USB Connection Mode**

**For Physical Device:**
1. When you connect via USB, check notification on phone
2. Tap the USB notification
3. Select **"File Transfer"** or **"MTP"** mode (not "Charging only")

---

### **Step 7: Use Emulator Instead**

If physical device keeps having issues:

1. **Tools → Device Manager**
2. Click **"Create Device"** (if you don't have one)
3. Select a device (e.g., Pixel 5)
4. Download a system image (API 24+)
5. Click **"Finish"**
6. Click **Play** button next to emulator to start it
7. Wait for emulator to fully boot
8. Try running app again

---

### **Step 8: Check Android SDK**

1. **Tools → SDK Manager**
2. Go to **"SDK Tools"** tab
3. Make sure these are installed:
   - ✅ Android SDK Platform-Tools
   - ✅ Android SDK Build-Tools
   - ✅ Google USB Driver (for physical devices on Windows)
4. Click **"Apply"** if any are missing

---

### **Step 9: Manual ADB Installation Check**

1. Open **Command Prompt** (as Administrator on Windows)
2. Navigate to SDK platform-tools:
   ```cmd
   cd %LOCALAPPDATA%\Android\Sdk\platform-tools
   ```
3. Run:
   ```cmd
   adb devices
   ```
4. If you see "adb is not recognized", add to PATH or use full path

---

### **Step 10: Check Device Compatibility**

Make sure your device/emulator meets requirements:
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- Device should be running Android 7.0 or higher

---

## 🎯 Most Common Solutions

**90% of the time, one of these works:**

1. ✅ **Restart ADB** (Step 2) - Most common fix
2. ✅ **Restart Android Studio** (Step 3)
3. ✅ **Use Emulator** (Step 7) - Most reliable
4. ✅ **Check USB Debugging** (Step 1)

---

## 📱 Alternative: Install APK Manually

If nothing works, you can install manually:

1. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Wait for build to complete
3. Find APK: `app/build/outputs/apk/debug/app-debug.apk`
4. Copy APK to your phone
5. On phone: **Settings → Security → Allow installation from unknown sources**
6. Tap the APK file on your phone to install

---

## 🔍 Still Not Working?

**Check these:**
- Is your device/emulator actually running?
- Do you see it in the device dropdown (top toolbar)?
- Try a different USB cable/port
- Try a different emulator
- Check Android Studio's **Logcat** for more error details

---

## ✅ Success Indicators

When it's working, you should see:
- Device name in top toolbar dropdown
- "adb devices" shows your device
- App installs and launches automatically

Good luck! 🚀
