package com.example.youeye.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.youeye.R;
import com.opencsv.CSVReader;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private List<String> medicationList;
    private Context context;

    public MedicationAdapter(Context context, List<String> medicationList) {
        this.context = context;
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

        // 약품명을 통해 이미지 링크 가져오기
        String imageUrl = findImageLink(medicationName);

        if (imageUrl != null) {
            // Glide로 이미지 로드
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.load)  // 로딩 중에 표시할 이미지
                    .error(R.drawable.error)              // 로드 실패 시 표시할 이미지
                    .into(holder.medicationImage);
        } else {
            holder.medicationImage.setImageResource(R.drawable.error);  // 이미지가 없을 때 표시할 기본 이미지
        }
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    // 약품명으로 이미지 링크를 찾는 메소드
    private String findImageLink(String productName) {
        try {
            // assets 폴더에서 druglink.csv 파일 열기
            InputStream is = context.getAssets().open("druglink.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is, "UTF-8"));
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                // 두 번째 열에 약품명 확인
                if (nextLine[1].equals(productName)) {
                    reader.close();
                    return nextLine[2];  // 세 번째 열에 이미지 링크가 있음
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
