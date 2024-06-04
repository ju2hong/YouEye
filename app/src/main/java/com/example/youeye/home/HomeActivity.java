package com.example.youeye.home;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;

import com.example.youeye.R;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // 사진검색 버튼 참조
        ImageButton imageSearch = findViewById(R.id.imageSearch);
        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ImageSearchActivity.class);
                startActivity(intent);
            }
        });
        // 음성검색 버튼 참조
        ImageButton voiceSearch = findViewById(R.id.voiceSearch);
        voiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, VoiceSearchActivity.class);
                startActivity(intent);
            }
        });
        // 글자검색 버튼 참조
        ImageButton textSearch = findViewById(R.id.textSearch);
        textSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, TextSearchActivity.class);
                startActivity(intent);
            }
        });
        // 마이페이지 버튼 참조
        ImageButton MyPage = findViewById(R.id.MyPage);
        MyPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });


    }
}
