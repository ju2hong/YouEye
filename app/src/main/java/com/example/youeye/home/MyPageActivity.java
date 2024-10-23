package com.example.youeye.home;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;
import com.example.youeye.alarm.TimeActivity;
import com.example.youeye.login.LoginActivity;

public class MyPageActivity extends AppCompatActivity {
    private TTSManager ttsManager;
    private SwitchManager switchManager;

    private ImageButton logoutButton, alButton, settingsButton, imageButton10,imageButton9;
    private TextView textView9, logoutText, alText, settingsText, myPillText, backText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // TTSManager 초기화
        ttsManager = new TTSManager(this);
        switchManager = SwitchManager.getInstance(this);

        // View 참조
        textView9 = findViewById(R.id.textView9);
        logoutButton = findViewById(R.id.logoutBtton);
        alButton = findViewById(R.id.alBtton);
        imageButton9 = findViewById(R.id.imageButton9);
        settingsButton = findViewById(R.id.imageButton8);

        logoutText = findViewById(R.id.logouttext);
        alText = findViewById(R.id.altext);
        settingsText = findViewById(R.id.textView16);
        myPillText = findViewById(R.id.textView17);
        backText = findViewById(R.id.textView18);

        // TextView의 텍스트를 TTS로 출력
        ttsManager.speakTextViewText(textView9);

        imageButton10 = findViewById(R.id.imageButton10);
        imageButton10.setOnClickListener(v -> speakButtonDescriptionAndFinish());

// 알람 버튼 클릭 이벤트 설정
        setButtonClickListener(alButton, TimeActivity.class);

// 나의 복용약 버튼 클릭 이벤트 설정
        setButtonClickListener(imageButton9, Show_pill.class);
// 설정 버튼 클릭 이벤트 설정
        setButtonClickListener(settingsButton, SettingsActivity.class);


// 로그아웃 버튼 클릭 이벤트 설정
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼의 contentDescription 읽기
                CharSequence buttonDescription = logoutButton.getContentDescription();

                // TTS로 contentDescription 읽기
                if (buttonDescription != null && switchManager.getSwitchState()) {
                    ttsManager.speak(buttonDescription.toString());
                }

                showLogoutDialog("로그아웃", "정말 로그아웃 하시겠습니까?");
            }
        });

    }
    // tts 로 버튼 위 데코 읽기
    private void setButtonClickListener(ImageButton button, Class<?> activityClass) {
        button.setOnClickListener(v -> {
            // 버튼의 contentDescription 읽기
            CharSequence buttonDescription = button.getContentDescription();

            // TTS 스위치 상태 확인 후 발화
            if (buttonDescription != null && switchManager.getSwitchState()) {
                ttsManager.speak(buttonDescription.toString());
            }

            // 버튼 클릭 시 해당 액티비티로 이동
            Intent intent = new Intent(MyPageActivity.this, activityClass);
            startActivity(intent);
        });
    }

    // 로그아웃 다이얼로그 표시
    private void showLogoutDialog(String title, String message) {
        // 커스텀 다이얼로그 생성
        Dialog dialog = new Dialog(MyPageActivity.this);
        dialog.setContentView(R.layout.custom_dialog_layout);

        // 다이얼로그의 뷰 참조
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        ImageButton yesButton = dialog.findViewById(R.id.yesButton);
        ImageButton noButton = dialog.findViewById(R.id.noButton);

        // 다이얼로그 타이틀 및 메시지 설정
        dialogTitle.setText(title);
        dialogMessage.setText(message);
        // 예 버튼 클릭 이벤트 처리
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TTS 스위치 상태 확인 후 발화
                if (switchManager.getSwitchState()) {
                    ttsManager.speak("예");
                }
                // 로그아웃 로직
                Toast.makeText(MyPageActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                // 로그인 페이지로 이동
                Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // 아니오 버튼 클릭 이벤트 처리
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TTS 스위치 상태 확인 후 발화
                if (switchManager.getSwitchState()) {
                    ttsManager.speak("아니오");
                }
                // 다이얼로그 닫기
                dialog.dismiss();
            }
        });

        // 다이얼로그 표시
        dialog.show();
    }
    private void speakButtonDescriptionAndFinish() {
        String buttonText = imageButton10.getContentDescription().toString();
        // TTS 스위치 상태 확인 후 발화
        if (switchManager.getSwitchState()) {
            ttsManager.speak(buttonText);

            // 예상 발화 시간 계산 (대략 100ms per character)
            int estimatedSpeechTime = buttonText.length() * 100;

            new Handler().postDelayed(this::finishWithAnimation, estimatedSpeechTime);
        } else {
            // TTS가 꺼져 있으면 즉시 액티비티 종료
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