package com.example.youeye;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.youeye.login.LoginActivity;
import com.example.youeye.member.MemberActivity;

public class MainActivity extends AppCompatActivity {
    private TTSManager ttsManager;
    private Switch switch1;
    private SwitchManager switchManager;
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private TextView textView3;
    private TextView textView1;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        imageButton1 = findViewById(R.id.imageButton);
        imageButton2 = findViewById(R.id.imageButton2);
        switch1 = findViewById(R.id.switch1);
        textView3 = findViewById(R.id.textView3);

        // TTSManager 초기화
        ttsManager = new TTSManager(this);

        // SwitchManager 초기화
        switchManager = SwitchManager.getInstance(this);

        // 스위치 초기 상태 설정
        if (!switchManager.isInitialized()) {
            textView3.setText("ON/OFF");
            switch1.setChecked(false);
            switchManager.setInitialized(true);
        } else {
            boolean switchState = switchManager.getSwitchState();
            textView3.setText(switchState ? "On" : "Off");
            switch1.setChecked(switchState);
        }

        // ImageButton 클릭 이벤트 설정
        setButtonClickListener(imageButton1, textView1, LoginActivity.class);
        setButtonClickListener(imageButton2, textView2, MemberActivity.class);

        // 스위치 상태 변화 감지
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String statusText = isChecked ? "On" : "Off";
            textView3.setText(statusText);
            ttsManager.setTTSOn(isChecked);
            ttsManager.speak(statusText);
            switchManager.setSwitchState(isChecked);
            if (isChecked) {
                switch1.getTrackDrawable().setColorFilter(
                        ContextCompat.getColor(MainActivity.this, R.color.switchbar_on_color),
                        PorterDuff.Mode.MULTIPLY
                );
            } else {
                switch1.getTrackDrawable().clearColorFilter();
            }
        });

        // 초기 상태 설정
        textView3.setText("ON/OFF");
    }

    private void setButtonClickListener(ImageButton button, TextView textView, Class<?> activityClass) {
        button.setOnClickListener(v -> {
            if (switch1.isChecked()) {
                String text = textView.getText().toString();
                ttsManager.speak(text);
            }
            Intent intent = new Intent(MainActivity.this, activityClass);
            intent.putExtra("switch_state", switch1.isChecked()); // 스위치 상태를 전달
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면이 다시 보여질 때 TTS 상태를 복원
        ttsManager.setTTSOn(switch1.isChecked());
    }

    @Override
    protected void onPause() {
        // 화면이 가려질 때 TTS 상태를 저장
        ttsManager.setTTSOn(switch1.isChecked());
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
