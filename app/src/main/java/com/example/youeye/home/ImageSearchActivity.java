package com.example.youeye.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;
import com.example.youeye.TTSManager;

public class ImageSearchActivity extends AppCompatActivity {
    private TTSManager ttsManager;
    private ImageButton imageButton4;
    private TextView textView9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagesearch);

        // TTSManager 초기화
        ttsManager = new TTSManager(this);

        // View 초기화
        textView9 = findViewById(R.id.textView9);
        imageButton4 = findViewById(R.id.imageButton4);  // imageButton4 초기화

        // 뒤로가기 버튼 클릭 이벤트 설정
        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttsManager.speakTextViewText(textView9);  // textView9의 텍스트를 읽음
                onBackButtonPressed(v);
            }
        });
    }

    public void onBackButtonPressed(View view) {
        finish(); // 종료하고 이전 액티비티로 돌아감
    }
}
