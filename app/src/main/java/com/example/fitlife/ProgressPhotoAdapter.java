package com.example.fitlife;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.FitLifeDatabase;
import com.example.fitlife.data.ProgressPhoto;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProgressPhotoAdapter extends RecyclerView.Adapter<ProgressPhotoAdapter.ViewHolder> {
    private List<ProgressPhoto> photos;
    private ProgressPhotosActivity activity;
    private FitLifeDatabase database;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public ProgressPhotoAdapter(List<ProgressPhoto> photos, ProgressPhotosActivity activity, FitLifeDatabase database) {
        this.photos = photos;
        this.activity = activity;
        this.database = database;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_progress_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProgressPhoto photo = photos.get(position);
        
        // Load image
        File imgFile = new File(photo.photoPath);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.ivPhoto.setImageBitmap(bitmap);
        }
        
        holder.tvDate.setText(dateFormat.format(new Date(photo.date)));
        holder.tvType.setText(photo.type.toUpperCase());
        
        if (photo.notes != null && !photo.notes.isEmpty()) {
            holder.tvNotes.setText(photo.notes);
            holder.tvNotes.setVisibility(View.VISIBLE);
        } else {
            holder.tvNotes.setVisibility(View.GONE);
        }
        
        holder.btnDelete.setOnClickListener(v -> activity.deletePhoto(photo));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvDate, tvType, tvNotes;
        android.widget.Button btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
