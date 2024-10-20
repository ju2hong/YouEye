package com.example.youeye.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.youeye.R;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
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

        // 이미지 로드
        String imageUrl = findImageLink(medicationName); // 이미지 링크를 가져오는 함수
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.load)
                .error(R.drawable.error)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("MedicationAdapter", "이미지 로드 실패: " + e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.medicationImage);

        // 리스트 항목 클릭 시 DrugDetailActivity로 이동
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DrugDetailActivity.class);
            intent.putExtra("name", medicationName);  // 약품명 전달
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    // 약품명으로 이미지 링크를 찾는 메소드
    private String findImageLink(String productName) {
        try {
            InputStream is = context.getAssets().open("druglink.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is, "UTF-8"));
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                String csvMedicineName = nextLine[1]; // 2번째 열의 약품명
                String imageUrl = nextLine[2];        // 3번째 열의 이미지 링크

                if (csvMedicineName.equals(productName)) {
                    return imageUrl;  // 이미지 링크를 반환
                }
            }
            reader.close();
        } catch (Exception e) {
            Log.e("MedicationAdapter", "CSV 파일 읽기 오류", e);
        }
        return null;  // 이미지 링크를 찾지 못하면 null 반환
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
