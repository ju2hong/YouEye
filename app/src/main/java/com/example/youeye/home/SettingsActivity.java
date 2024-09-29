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

public class SettingsActivity extends AppCompatActivity {

    private ImageButton imageButton14;
    private TTSManager ttsManager;
    private SwitchManager switchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // TTSManager와 SwitchManager 초기화
        ttsManager = new TTSManager(this);
        switchManager = SwitchManager.getInstance(this);

        imageButton14 = findViewById(R.id.imageButton14);
        imageButton14.setOnClickListener(v -> speakButtonDescriptionAndFinish());

        // 비밀번호 변경 버튼 참조 및 클릭 이벤트 처리
        ImageButton changePasswordButton = findViewById(R.id.imageButton12);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        // 알람 버튼 참조
        ImageButton alButton = findViewById(R.id.imageButton13);
        alButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, TimeActivity.class);
                startActivity(intent);
            }
        });

        // 회원탈퇴 버튼 참조 및 클릭 이벤트 처리
        ImageButton withdrawButton = findViewById(R.id.imageButton13);
        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWithdrawDialog("회원탈퇴", "정말 회원탈퇴 \n 하시겠습니까?");
            }
        });
    }

    // 커스텀 다이얼로그 표시
    private void showWithdrawDialog(String title, String message) {
        // 커스텀 다이얼로그 생성
        Dialog dialog = new Dialog(SettingsActivity.this);
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
                // 회원탈퇴 로직
                Toast.makeText(SettingsActivity.this, "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // 아니오 버튼 클릭 이벤트 처리
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이얼로그 닫기
                dialog.dismiss();
            }
        });

        // 다이얼로그 표시
        dialog.show();
    }
    private void speakButtonDescriptionAndFinish() {
        String buttonText = imageButton14.getContentDescription().toString();
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
