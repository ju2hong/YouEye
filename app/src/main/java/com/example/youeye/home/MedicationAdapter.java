package com.example.youeye.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youeye.R;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private List<String> medicationList;

    public MedicationAdapter(List<String> medicationList) {
        this.medicationList = medicationList;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_item, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        String medicationName = medicationList.get(position);
        holder.medicationName.setText(medicationName);

        // 임의의 약품 이미지 로드 (약품 이미지가 저장된 링크를 사용)
        holder.medicationImage.setImageResource(R.drawable.load);  // 기본 이미지 사용
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {
        public ImageView medicationImage;
        public TextView medicationName;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            medicationImage = itemView.findViewById(R.id.imageView1);
            medicationName = itemView.findViewById(R.id.name);
        }
    }
}
