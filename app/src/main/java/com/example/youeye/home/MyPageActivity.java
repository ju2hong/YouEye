package com.example.youeye.home;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;
import com.example.youeye.TTSManager;
import com.example.youeye.alarm.TimeActivity;
import com.example.youeye.login.LoginActivity;

public class MyPageActivity extends AppCompatActivity {
    private TTSManager ttsManager;
    private ImageButton logoutButton, alButton, settingsButton, backButton;
    private TextView textView9, logoutText, alText, settingsText, myPillText, backText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // TTSManager 초기화
        ttsManager = new TTSManager(this);

        // View 참조
        textView9 = findViewById(R.id.textView9);
        logoutButton = findViewById(R.id.logoutBtton);
        alButton = findViewById(R.id.alBtton);
        settingsButton = findViewById(R.id.imageButton8);
        backButton = findViewById(R.id.imageButton10);

        logoutText = findViewById(R.id.logouttext);
        alText = findViewById(R.id.altext);
        settingsText = findViewById(R.id.textView16);
        myPillText = findViewById(R.id.textView17);
        backText = findViewById(R.id.textView18);

        // TextView의 텍스트를 TTS로 출력
        ttsManager.speakTextViewText(textView9);

        // 알람 버튼 클릭 이벤트 설정
        setButtonClickListener(alButton, alText, TimeActivity.class);

        // 설정 버튼 클릭 이벤트 설정
        setButtonClickListener(settingsButton, settingsText, SettingsActivity.class);

        // 로그아웃 버튼 클릭 이벤트 설정
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttsManager.speakTextViewText(logoutText);
                showLogoutDialog("로그아웃", "정말 로그아웃 하시겠습니까?");
            }
        });

        // 뒤로가기 버튼 클릭 이벤트 설정
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttsManager.speakTextViewText(backText);
                onBackButtonPressed(v);
            }
        });
    }

    private void setButtonClickListener(ImageButton button, TextView textView, Class<?> activityClass) {
        button.setOnClickListener(v -> {
            ttsManager.speakTextViewText(textView);
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
                ttsManager.speak("예");
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
                ttsManager.speak("아니오");
                // 다이얼로그 닫기
                dialog.dismiss();
            }
        });

        // 다이얼로그 표시
        dialog.show();
    }

    public void onBackButtonPressed(View view) {
        finish(); // 종료하고 이전 액티비티로 돌아감
    }

    @Override
    protected void onResume() {
        super.onResume();
        ttsManager.setTTSOn(true); // 화면이 다시 보여질 때 TTS 상태를 복원
    }

    @Override
    protected void onPause() {
        ttsManager.setTTSOn(false); // 화면이 가려질 때 TTS 상태를 저장
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }
}
