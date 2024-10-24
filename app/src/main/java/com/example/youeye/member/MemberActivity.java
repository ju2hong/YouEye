package com.example.youeye.member;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;

public class MemberActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        // 아이디 버튼 참조
        ImageButton idInputButton = findViewById(R.id.idInputButton);

        // 아이디 버튼에 클릭 리스너 추가
        idInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 아이디 버튼을 클릭했을 때 실행될 코드 작성
                // 아이디 입력 화면으로 이동
                Intent intent = new Intent(MemberActivity.this, IDMemberActivity.class);
                startActivity(intent);
            }
        });

        // 비밀번호 버튼 참조
        ImageButton pwInputButton = findViewById(R.id.pwInputButton);

        // 비밀번호 버튼에 클릭 리스너 추가
        pwInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비밀번호 버튼을 클릭했을 때 실행될 코드 작성
                // 비밀번호 입력 화면으로 이동
                Intent intent = new Intent(MemberActivity.this, PWMemberActivity.class);
                startActivity(intent);
            }
        });
    }
}

