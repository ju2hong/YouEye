package com.example.youeye;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // activity_login 페이지로 넘어감
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 아이디 버튼 참조
        ImageButton idInputButton = findViewById(R.id.idInputButton);

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
    }
}
