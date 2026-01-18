# How to Get a Free Google Maps API Key

## 🗺️ Quick Guide

The app now works **WITHOUT** a Google Maps API key! You can:

- ✅ Enter coordinates manually (latitude/longitude)
- ✅ Use GPS to get current location
- ✅ Save and view locations
- ⚠️ Map visualization requires API key (optional)

---

## 📝 Option 1: Use App Without API Key (Recommended for Testing)

**The app is fully functional without an API key!**

You can:

1. **Add Locations** - Enter coordinates manually or use GPS
2. **View Locations** - See all your saved locations
3. **Link to Workouts** - Connect workouts to locations

**Only the visual map display requires an API key.**

---

## 🔑 Option 2: Get Free Google Maps API Key (For Map Visualization)

If you want to see locations on a map, follow these steps:

### **Step 1: Create Google Cloud Account**

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Sign in with your Google account (or create one)
3. Click **"Get Started"** or **"Create Project"**

### **Step 2: Create a Project**

1. Click **"Select a project"** → **"New Project"**
2. Enter project name: `FitLife App`
3. Click **"Create"**
4. Wait for project creation (takes a few seconds)

### **Step 3: Enable Maps SDK**

1. In the left menu, go to **"APIs & Services"** → **"Library"**
2. Search for **"Maps SDK for Android"**
3. Click on it and press **"Enable"**
4. Wait for it to enable

### **Step 4: Create API Key**

1. Go to **"APIs & Services"** → **"Credentials"**
2. Click **"+ CREATE CREDENTIALS"** → **"API key"**
3. A popup will show your API key - **COPY IT!**
4. Click **"Restrict key"** (recommended for security)

### **Step 5: Restrict API Key (Security)**

1. Under **"API restrictions"**, select **"Restrict key"**
2. Choose **"Maps SDK for Android"**
3. Under **"Application restrictions"**, select **"Android apps"**
4. Click **"Add an item"**
   - **Package name**: `com.example.fitlife`
   - **SHA-1 certificate fingerprint**: (Get from Android Studio)
     - In Android Studio: **Build → Generate Signed Bundle/APK**
     - Or run: `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android`
5. Click **"Save"**

### **Step 6: Add API Key to App**

1. Open `app/src/main/AndroidManifest.xml`
2. Find this line (around line 63):
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_GOOGLE_MAPS_API_KEY" />
   ```
3. Replace `YOUR_GOOGLE_MAPS_API_KEY` with your actual key
4. Save the file
5. **Sync Gradle** in Android Studio

### **Step 7: Billing Setup (Free Tier)**

Google Maps has a **generous free tier**:

- **$200 free credit per month**
- For most apps, this is **completely free**
- You only pay if you exceed the free tier

**To enable billing (required even for free tier):**

1. Go to **"Billing"** in Google Cloud Console
2. Click **"Link a billing account"**
3. Add a payment method (won't be charged unless you exceed free tier)
4. The free $200 credit covers most usage

---

## 🎯 Quick Reference

**Package Name**: `com.example.fitlife`

**API to Enable**: Maps SDK for Android

**Where to Add Key**: `app/src/main/AndroidManifest.xml` (line ~65)

---

## ⚠️ Important Notes

1. **Free Tier**: $200/month free credit (usually enough for testing)
2. **Security**: Always restrict your API key to your app
3. **Testing**: You can test without API key using manual coordinates
4. **Production**: For production apps, use proper key restrictions

---

## 🚀 Alternative: Use Without API Key

The app is designed to work without an API key:

- Enter coordinates manually (e.g., `40.7128, -74.0060` for New York)
- Use GPS button to get current location
- All features work except visual map display

**Example coordinates:**

- New York: `40.7128, -74.0060`
- London: `51.5074, -0.1278`
- Tokyo: `35.6762, 139.6503`

---

## ✅ You're Done!

Once you add the API key, the map visualization will work. Until then, you can still use all location features with manual coordinate entry!
