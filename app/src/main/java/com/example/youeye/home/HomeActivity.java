package com.example.youeye.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;

public class HomeActivity extends AppCompatActivity {
    private TTSManager ttsManager;
    private SwitchManager switchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // TTSManager 및 SwitchManager 초기화
        ttsManager = new TTSManager(this);
        switchManager = SwitchManager.getInstance(this);

        // 사진검색 버튼 참조
        ImageButton imageSearch = findViewById(R.id.imageSearch);
        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakButtonDescription(imageSearch);
                Intent intent = new Intent(HomeActivity.this, ImageSearchActivity.class);
                startActivity(intent);
            }
        });

        // 음성검색 버튼 참조
        ImageButton voiceSearch = findViewById(R.id.voiceSearch);
        voiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakButtonDescription(voiceSearch);
                Intent intent = new Intent(HomeActivity.this, VoiceSearchActivity.class);
                startActivity(intent);
            }
        });

        // 글자검색 버튼 참조
        ImageButton textSearch = findViewById(R.id.textSearch);
        textSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakButtonDescription(textSearch);
                Intent intent = new Intent(HomeActivity.this, TextSearchActivity.class);
                startActivity(intent);
            }
        });

        // 마이페이지 버튼 참조
        ImageButton myPage = findViewById(R.id.MyPage);
        myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakButtonDescription(myPage);
                Intent intent = new Intent(HomeActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });
    }

    // ImageButton의 contentDescription을 음성으로 출력하는 메서드
    private void speakButtonDescription(ImageButton button) {
        String buttonText = button.getContentDescription().toString();
        // 스위치가 활성화되어 있을 때만 음성 출력
        if (switchManager.getSwitchState()) {
            ttsManager.speak(buttonText);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면이 다시 보여질 때 TTS 상태를 복원
        ttsManager.setTTSOn(switchManager.getSwitchState());
    }

    @Override
    protected void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }
}
