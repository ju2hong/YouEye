package com.example.youeye;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 인텐트에서 아이디와 비밀번호 값을 받아옵니다.
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String pw = intent.getStringExtra("pw");

        // 받은 값을 로그에 출력합니다.
        Log.d("HomeActivity", "ID: " + id);
        Log.d("HomeActivity", "Password: " + pw);


    }
}
