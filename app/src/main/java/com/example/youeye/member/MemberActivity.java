package com.example.youeye.member;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;

public class MemberActivity extends AppCompatActivity {
    private boolean switchState; // 스위치 상태를 저장할 변수
    private TTSManager ttsManager; // TTSManager 추가
    private SwitchManager switchManager; // SwitchManager 추가
    private ImageButton idInputButton;
    private ImageButton pwInputButton;
    private Switch switchMember; // MemberActivity 화면의 스위치
    private TextView textViewSwitchState; // 스위치 상태를 표시하는 텍스트뷰
    private TextView idInputTextView;
    private TextView pwInputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        // TTSManager 및 SwitchManager 초기화
        ttsManager = new TTSManager(this);
        switchManager = SwitchManager.getInstance(this);

        // 스위치 상태 가져오기
        switchState = switchManager.getSwitchState();

        // 스위치와 버튼 초기화
        switchMember = findViewById(R.id.switch1);
        textViewSwitchState = findViewById(R.id.textView3);
        idInputButton = findViewById(R.id.idInputButton);
        pwInputButton = findViewById(R.id.pwInputButton);
        idInputTextView = findViewById(R.id.loginView);
        pwInputTextView = findViewById(R.id.pwView);

        // 스위치 초기 상태 설정
        switchMember.setChecked(switchState);
        textViewSwitchState.setText(switchState ? "ON" : "OFF");

        // 버튼 클릭 리스너 설정
        setButtonClickListener(idInputButton, idInputTextView, IDMemberActivity.class);
        setButtonClickListener(pwInputButton, pwInputTextView, PWMemberActivity.class);

        // 스위치 상태 변화 리스너 설정
        switchMember.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String statusText = isChecked ? "ON" : "OFF";
            textViewSwitchState.setText(statusText);
            ttsManager.setTTSOn(isChecked);
            ttsManager.speak(statusText);
            switchState = isChecked;
            switchManager.setSwitchState(isChecked);
            if (isChecked) {
                switchMember.getTrackDrawable().setColorFilter(
                        ContextCompat.getColor(MemberActivity.this, R.color.switchbar_on_color),
                        PorterDuff.Mode.MULTIPLY
                );
            } else {
                switchMember.getTrackDrawable().clearColorFilter();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면이 다시 보여질 때 TTS 상태를 복원
        switchState = switchManager.getSwitchState();
        ttsManager.setTTSOn(switchState);
        switchMember.setChecked(switchState);
        textViewSwitchState.setText(switchState ? "ON" : "OFF");
    }

    @Override
    protected void onPause() {
        // 화면이 가려질 때 TTS 상태를 저장
        switchState = ttsManager.isTTSOn();
        switchManager.setSwitchState(switchState);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }

    private void setButtonClickListener(ImageButton button, TextView textView, Class<?> activityClass) {
        button.setOnClickListener(v -> {
            // TTS 읽기
            if (switchState && textView != null) {
                String text = textView.getText().toString();
                ttsManager.speak(text);
            }

            // 화면 이동
            Intent intent = new Intent(MemberActivity.this, activityClass);
            intent.putExtra("switch_state", switchState); // 스위치 상태를 전달
            startActivity(intent);
        });
    }
}
