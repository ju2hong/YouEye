package com.example.youeye.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;

public class PhotoResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_result);

        ImageView photoImageView = findViewById(R.id.photoImageView);
        Button searchButton = findViewById(R.id.searchButton);

        // 이전 액티비티에서 전달된 사진 Uri 받기
        Uri photoUri = getIntent().getParcelableExtra("photoUri");
        if (photoUri != null) {
            photoImageView.setImageURI(photoUri);
        }

        // 검색하기 버튼 클릭 리스너 설정
        searchButton.setOnClickListener(v -> {
            // 검색 기능을 처리할 코드 추가
        });
    }
}
