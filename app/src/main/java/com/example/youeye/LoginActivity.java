package com.example.youeye;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 아이디 버튼 참조
        ImageButton idInputButton = findViewById(R.id.idInputButton);
        // 비밀번호 버튼 참조
        ImageButton pwInputButton = findViewById(R.id.pwInputButton);

        // 아이디 버튼에 클릭 리스너 추가
        idInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 아이디 버튼을 클릭했을 때 실행될 코드 작성
                // 아이디 입력 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, IDLoginActivity.class);
                startActivity(intent);
            }
        });

        // 비밀번호 버튼에 클릭 리스너 추가
        pwInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비밀번호 버튼을 클릭했을 때 실행될 코드 작성
                // 비밀번호 입력 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, PWLoginActivity.class);
                startActivity(intent);
            }
        });

        // 인텐트에서 아이디와 비밀번호 값을 받아옵니다.
        Intent intent = getIntent();
        if(intent.hasExtra("id")) {
            String id = intent.getStringExtra("id");
            Log.d("LoginActivity", "Received ID: " + id);
        }
        if(intent.hasExtra("pw")) {
            String pw = intent.getStringExtra("pw");
            Log.d("LoginActivity", "Received Password: " + pw);
            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(homeIntent);
        }

    }
}