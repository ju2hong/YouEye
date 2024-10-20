package com.example.youeye.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youeye.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Show_pill extends AppCompatActivity {

    private RecyclerView medicationRecyclerView;
    private MedicationAdapter medicationAdapter;
    private List<String> medicationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_pill);

        // RecyclerView 초기화
        medicationRecyclerView = findViewById(R.id.medicationRecyclerView);
        medicationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 저장된 약품 리스트 불러오기
        medicationList = loadSearchedMedicines();

        // RecyclerView 어댑터 설정
        medicationAdapter = new MedicationAdapter(medicationList);
        medicationRecyclerView.setAdapter(medicationAdapter);

        // 뒤로가기 버튼 설정
        ImageButton bkButton = findViewById(R.id.bkButton);
        bkButton.setOnClickListener(v -> finish());
    }

    // SharedPreferences에서 저장된 약품명을 불러오는 메소드
    private List<String> loadSearchedMedicines() {
        SharedPreferences sharedPreferences = getSharedPreferences("SearchedMedicines", MODE_PRIVATE);
        Set<String> medicineSet = sharedPreferences.getStringSet("medicineList", new HashSet<>());

        // Set을 List로 변환하여 반환
        return new ArrayList<>(medicineSet);
    }
}
