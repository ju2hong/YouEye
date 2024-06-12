package com.example.youeye.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;
import com.example.youeye.alarm.TimeActivity;

public class MyPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // 알람 버튼 참조
        ImageButton alButton = findViewById(R.id.alBtton);
        alButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, TimeActivity.class);
                startActivity(intent);
            }
        });

        // 설정 버튼 참조
        ImageButton settingsButton = findViewById(R.id.imageButton8);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
