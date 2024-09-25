package com.example.youeye.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceSearchActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_STT = 1;
    private ImageButton searchVoiceButton;
    private ImageButton backButton;
    private TTSManager ttsManager;
    private SwitchManager switchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicesearch);

        // TTSManager와 SwitchManager 초기화
        ttsManager = new TTSManager(this);
        switchManager = SwitchManager.getInstance(this);

        // 버튼 초기화
        searchVoiceButton = findViewById(R.id.searchVoice);
        backButton = findViewById(R.id.backButton);

        // 음성 검색 버튼 클릭 시 음성 인식 시작
        searchVoiceButton.setOnClickListener(v -> startVoiceRecognition());

        // 뒤로가기 버튼 클릭 시 버튼 설명을 음성으로 출력하고 액티비티 종료
        backButton.setOnClickListener(v -> speakButtonDescriptionAndFinish(backButton));
    }

    // 버튼의 설명을 음성으로 출력한 후 액티비티 종료
    private void speakButtonDescriptionAndFinish(ImageButton button) {
        String buttonText = button.getContentDescription().toString();
        if (switchManager.getSwitchState()) {
            ttsManager.speak(buttonText);

            // 예상 발화 시간 계산 (대략 100ms per character + 100ms buffer)
            int estimatedSpeechTime = buttonText.length() * 100;

            // 발화 시간이 지난 후에 액티비티 종료
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finishWithAnimation();
                }
            }, estimatedSpeechTime);
        } else {
            // TTS가 비활성화된 경우 바로 액티비티 종료
            finishWithAnimation();
        }
    }

    // 액티비티 종료와 애니메이션 적용
    private void finishWithAnimation() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // 음성 인식 시작
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

        // 음성 인식 결과 처리
        if (requestCode == REQUEST_CODE_STT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String recognizedText = result.get(0);
                moveToTextSearchActivity(recognizedText);
            }
        }
    }

    // TextSearchActivity로 인식된 텍스트 전달
    private void moveToTextSearchActivity(String recognizedText) {
        Intent intent = new Intent(VoiceSearchActivity.this, TextSearchActivity.class);
        intent.putExtra("recognizedText", recognizedText);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 액티비티가 다시 보여질 때 TTS 상태 복원
        ttsManager.setTTSOn(switchManager.getSwitchState());
    }

    @Override
    protected void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼을 눌렀을 때의 기본 동작을 호출
        super.onBackPressed();
        // 애니메이션 적용
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
