package com.example.youeye.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;

public class Show_pill extends AppCompatActivity {
    private TTSManager ttsManager;
    private SwitchManager switchManager;
    private ImageButton bkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_pill);

        // TTSManager 및 SwitchManager 초기화
        ttsManager = new TTSManager(this);
        switchManager = SwitchManager.getInstance(this);

        // 뒤로가기 버튼 초기화
        bkButton = findViewById(R.id.bkButton);
        bkButton.setOnClickListener(v -> speakButtonDescriptionAndFinish());
    }

    private void speakButtonDescriptionAndFinish() {
        String buttonText = bkButton.getContentDescription().toString();
        if (switchManager.getSwitchState()) {
            ttsManager.speak(buttonText);

            // 예상 발화 시간 계산 (대략 100ms per character)
            int estimatedSpeechTime = buttonText.length() * 100;

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

    @Override
    protected void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }
}