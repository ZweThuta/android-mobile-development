package com.example.fitlife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.BodyMetrics;
import com.example.fitlife.data.FitLifeDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BodyMetricsAdapter extends RecyclerView.Adapter<BodyMetricsAdapter.ViewHolder> {
    private List<BodyMetrics> metricsList;
    private BodyMetricsActivity activity;
    private FitLifeDatabase database;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public BodyMetricsAdapter(List<BodyMetrics> metricsList, BodyMetricsActivity activity, FitLifeDatabase database) {
        this.metricsList = metricsList;
        this.activity = activity;
        this.database = database;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_body_metrics, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BodyMetrics metrics = metricsList.get(position);
        
        holder.tvDate.setText(dateFormat.format(new Date(metrics.date)));
        
        double bmi = metrics.getBMI();
        if (bmi > 0) {
                holder.tvBMI.setText(String.format(Locale.getDefault(), "BMI: %.1f", bmi));
        } else {
            holder.tvBMI.setText("BMI: --");
        }
        
        holder.tvWeight.setText(String.format(Locale.getDefault(), "Weight: %.1f kg", metrics.weight));
        
        if (metrics.bodyFat > 0) {
            holder.tvBodyFat.setText(String.format(Locale.getDefault(), "Body Fat: %.1f%%", metrics.bodyFat));
        } else {
            holder.tvBodyFat.setText("Body Fat: --");
        }
        
        StringBuilder measurements = new StringBuilder("Measurements: ");
        boolean hasMeasurements = false;
        if (metrics.chest > 0) {
            measurements.append(String.format(Locale.getDefault(), "Chest %.1f", metrics.chest));
            hasMeasurements = true;
        }
        if (metrics.waist > 0) {
            if (hasMeasurements) measurements.append(", ");
            measurements.append(String.format(Locale.getDefault(), "Waist %.1f", metrics.waist));
            hasMeasurements = true;
        }
        if (metrics.hips > 0) {
            if (hasMeasurements) measurements.append(", ");
            measurements.append(String.format(Locale.getDefault(), "Hips %.1f", metrics.hips));
            hasMeasurements = true;
        }
        if (!hasMeasurements) {
            measurements.append("None");
        }
        holder.tvMeasurements.setText(measurements.toString());
        
        holder.btnDelete.setOnClickListener(v -> activity.deleteMetrics(metrics));
    }

    @Override
    public int getItemCount() {
        return metricsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvBMI, tvWeight, tvBodyFat, tvMeasurements;
        android.widget.Button btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvBMI = itemView.findViewById(R.id.tvBMI);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvBodyFat = itemView.findViewById(R.id.tvBodyFat);
            tvMeasurements = itemView.findViewById(R.id.tvMeasurements);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
