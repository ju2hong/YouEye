package com.example.youeye.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Show_pill extends AppCompatActivity {

    private RecyclerView medicationRecyclerView;
    private MedicationAdapter medicationAdapter;
    private List<String> medicationList;
    private ImageButton bkButton;
    private TTSManager ttsManager;
    private SwitchManager switchManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_pill);

        ImageButton clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(v -> {
            clearSearchedMedicines(); // 저장된 약품 내역 삭제
            medicationList.clear(); // RecyclerView의 데이터 리스트도 비워줌
            medicationAdapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
        });

        // RecyclerView 초기화
        medicationRecyclerView = findViewById(R.id.medicationRecyclerView);
        medicationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 저장된 약품 리스트 불러오기
        medicationList = loadSearchedMedicines();

        // RecyclerView 어댑터 설정 (Context 추가)
        medicationAdapter = new MedicationAdapter(this, medicationList);  // 'this'로 Context 전달
        medicationRecyclerView.setAdapter(medicationAdapter);
        // TTSManager 초기화
        ttsManager = new TTSManager(this);
        switchManager = SwitchManager.getInstance(this);
        // 뒤로가기 버튼 설정
        bkButton = findViewById(R.id.bkButton);
        bkButton.setOnClickListener(v -> speakButtonDescriptionAndFinish());
    }
    // 삭제
    private void clearSearchedMedicines() {
        SharedPreferences sharedPreferences = getSharedPreferences("SearchedMedicines", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("medicineList"); // 저장된 약품 리스트 제거
        editor.apply(); // 변경사항 적용
    }

    // SharedPreferences에서 저장된 약품명을 불러오는 메소드
    private List<String> loadSearchedMedicines() {
        SharedPreferences sharedPreferences = getSharedPreferences("SearchedMedicines", MODE_PRIVATE);
        Set<String> medicineSet = sharedPreferences.getStringSet("medicineList", new HashSet<>());

        // Set을 List로 변환하여 반환
        return new ArrayList<>(medicineSet);
    }
    private void speakButtonDescriptionAndFinish() {
        String buttonText = bkButton.getContentDescription().toString();
        if (switchManager.getSwitchState()) {
            ttsManager.speak(buttonText);

            // 예상 발화 시간 계산 (대략 100ms per character + 500ms buffer)
            int estimatedSpeechTime = buttonText.length() * 100 ;

            new Handler().postDelayed(this::finishWithAnimation, estimatedSpeechTime);
        } else {
            finishWithAnimation();
        }
    }
    private void finishWithAnimation() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        speakButtonDescriptionAndFinish();
    }
}
