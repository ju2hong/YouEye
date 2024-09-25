package com.example.youeye.home;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.youeye.R;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceSearchActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_STT = 1;
    private ImageButton searchVoiceButton;
    private TextView searchVoiceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicesearch);

        // 버튼 및 텍스트 뷰 초기화
        searchVoiceButton = findViewById(R.id.searchVoice);

        // searchVoice 버튼 클릭 시 STT 실행
        searchVoiceButton.setOnClickListener(v -> startVoiceRecognition());
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "음성으로 검색어를 입력하세요");

        try {
            startActivityForResult(intent, REQUEST_CODE_STT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_STT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String recognizedText = result.get(0);  // 인식된 텍스트 가져오기
                moveToTextSearchActivity(recognizedText);  // TextSearchActivity로 이동
            }
        }
    }

    // TextSearchActivity로 텍스트 전달
    private void moveToTextSearchActivity(String recognizedText) {
        Intent intent = new Intent(VoiceSearchActivity.this, TextSearchActivity.class);
        intent.putExtra("recognizedText", recognizedText);  // 텍스트 전달
        startActivity(intent);
    }
}
