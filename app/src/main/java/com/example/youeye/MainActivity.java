package com.example.youeye;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
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

        // ImageButton 클릭 이벤트 통합
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch1.isChecked()) {
                    TextView textView = (v.getId() == R.id.imageButton) ? textView1 : textView2;
                    if (textView != null) {
                        String text = textView.getText().toString();
                        ttsManager.speak(text);
                    }
                }
                if (v.getId() == R.id.imageButton) {
                    // 로그인 페이지로 이동
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else if (v.getId() == R.id.imageButton2) {
                    // 회원가입 페이지로 이동
                    startActivity(new Intent(MainActivity.this, MemberActivity.class));
                }
            }
        };

        imageButton1.setOnClickListener(onClickListener);
        imageButton2.setOnClickListener(onClickListener);

        // 스위치 상태 변화 감지
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String statusText = isChecked ? "On" : "Off";
                textView3.setText(statusText);
                ttsManager.speak(statusText);
                if (isChecked) {
                    switch1.getTrackDrawable().setColorFilter(
                            ContextCompat.getColor(MainActivity.this, R.color.switchbar_on_color),
                            PorterDuff.Mode.MULTIPLY
                    );
                } else {
                    // 스위치가 꺼진 경우에는 트랙의 색상 필터를 제거하여 원래의 상태로 되돌립니다.
                    switch1.getTrackDrawable().clearColorFilter();
                }
            }
        });

        // 초기 상태 설정
        textView3.setText("ON/OFF");
    }

    @Override
    protected void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }
}