package com.example.fitlife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.WorkoutLocation;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private List<WorkoutLocation> locations;
    private LocationsActivity activity;
    private boolean selectMode;

    public LocationAdapter(List<WorkoutLocation> locations, LocationsActivity activity, boolean selectMode) {
        this.locations = locations;
        this.activity = activity;
        this.selectMode = selectMode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutLocation location = locations.get(position);
        holder.tvLocationName.setText(location.name);
        holder.tvAddress.setText(location.address);
        holder.tvType.setText(location.type);

        if (selectMode) {
            holder.btnViewOnMap.setText("Select");
            holder.btnViewOnMap.setOnClickListener(v -> activity.selectLocation(location));
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnViewOnMap.setOnClickListener(v -> activity.viewOnMap(location));
            holder.btnDelete.setOnClickListener(v -> activity.deleteLocation(location));
        }
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocationName, tvAddress, tvType;
        android.widget.Button btnViewOnMap, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvType = itemView.findViewById(R.id.tvType);
            btnViewOnMap = itemView.findViewById(R.id.btnViewOnMap);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
