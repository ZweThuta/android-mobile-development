package com.example.fitlife;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.ProgressPhoto;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProgressPhotosActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 300;
    private static final int CAMERA_REQUEST_CODE = 301;
    private static final int GALLERY_REQUEST_CODE = 302;
    
    private RecyclerView rvPhotos;
    private ProgressPhotoAdapter adapter;
    private FitLifeDatabase database;
    private long userId;
    private List<ProgressPhoto> photos;
    private String currentPhotoPath;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_photos);

        database = FitLifeDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        rvPhotos = findViewById(R.id.rvPhotos);
        com.google.android.material.floatingactionbutton.FloatingActionButton fabTakePhoto = findViewById(R.id.fabTakePhoto);

        photos = new ArrayList<>();
        adapter = new ProgressPhotoAdapter(photos, this, database);
        rvPhotos.setLayoutManager(new GridLayoutManager(this, 2));
        rvPhotos.setAdapter(adapter);

        fabTakePhoto.setOnClickListener(v -> showPhotoOptions());

        loadPhotos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPhotos();
    }

    private void loadPhotos() {
        photos.clear();
        photos.addAll(database.progressPhotoDao().getPhotosByUser(userId));
        adapter.notifyDataSetChanged();
    }

    private void showPhotoOptions() {
        new AlertDialog.Builder(this)
            .setTitle("Add Progress Photo")
            .setItems(new String[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
                if (which == 0) {
                    takePhoto();
                } else {
                    chooseFromGallery();
                }
            })
            .show();
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                    "com.example.fitlife.fileprovider",
                    photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                if (currentPhotoPath != null) {
                    showPhotoDetailsDialog(currentPhotoPath);
                }
            } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                Uri selectedImage = data.getData();
                try {
                    String imagePath = copyImageToAppStorage(selectedImage);
                    if (imagePath != null) {
                        showPhotoDetailsDialog(imagePath);
                    }
                } catch (IOException e) {
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String copyImageToAppStorage(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName);
        
        FileOutputStream outputStream = new FileOutputStream(imageFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.close();
        inputStream.close();
        
        return imageFile.getAbsolutePath();
    }

    private void showPhotoDetailsDialog(String photoPath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_take_photo, null);
        builder.setView(view);

        Spinner spinnerType = view.findViewById(R.id.spinnerType);
        TextInputEditText etWeight = view.findViewById(R.id.etWeight);
        TextInputEditText etNotes = view.findViewById(R.id.etNotes);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            new String[]{"Before", "After", "Progress"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String type = spinnerType.getSelectedItem().toString().toLowerCase();
            String weightStr = etWeight.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();

            ProgressPhoto photo = new ProgressPhoto(userId, photoPath, System.currentTimeMillis());
            photo.type = type;
            photo.notes = notes;
            
            if (!weightStr.isEmpty()) {
                try {
                    photo.weight = Double.parseDouble(weightStr);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }

            long id = database.progressPhotoDao().insert(photo);
            if (id > 0) {
                Toast.makeText(this, "Photo saved!", Toast.LENGTH_SHORT).show();
                loadPhotos();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Failed to save photo", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void deletePhoto(ProgressPhoto photo) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Photo")
            .setMessage("Are you sure you want to delete this photo?")
            .setPositiveButton("Delete", (dialog, which) -> {
                // Delete file
                File file = new File(photo.photoPath);
                if (file.exists()) {
                    file.delete();
                }
                database.progressPhotoDao().delete(photo.id);
                loadPhotos();
                Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
