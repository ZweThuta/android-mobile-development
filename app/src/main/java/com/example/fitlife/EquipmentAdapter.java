package com.example.fitlife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.data.Equipment;

import java.util.List;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder> {
    private List<Equipment> equipmentList;
    private CreateWorkoutRoutineActivity activity;

    public EquipmentAdapter(List<Equipment> equipmentList, CreateWorkoutRoutineActivity activity) {
        this.equipmentList = equipmentList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_equipment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Equipment equipment = equipmentList.get(position);
        holder.tvEquipmentName.setText(equipment.name);
        holder.tvCategory.setText(equipment.category);
        holder.cbCompleted.setChecked(equipment.isCompleted);

        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            activity.markEquipmentAsCompleted(equipment, isChecked);
        });

        holder.btnDelete.setOnClickListener(v -> activity.deleteEquipment(equipment));
    }

    @Override
    public int getItemCount() {
        return equipmentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEquipmentName, tvCategory;
        CheckBox cbCompleted;
        android.widget.Button btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEquipmentName = itemView.findViewById(R.id.tvEquipmentName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
