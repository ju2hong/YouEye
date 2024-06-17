package com.example.youeye.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;
import com.example.youeye.alarm.TimeActivity;
import com.example.youeye.login.LoginActivity;

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

        // 로그아웃 버튼 참조 및 클릭 이벤트 처리
        ImageButton logoutButton = findViewById(R.id.logoutBtton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog("로그아웃", "정말 로그아웃 하시겠습니까?");
            }
        });
    }

        // 로그아웃 다이얼로그 표시
        private void showLogoutDialog(String title, String message) {
            // 커스텀 다이얼로그 생성
            Dialog dialog = new Dialog(MyPageActivity.this);
            dialog.setContentView(R.layout.custom_dialog_layout);

            // 다이얼로그의 뷰 참조
            TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
            TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
            ImageButton yesButton = dialog.findViewById(R.id.yesButton);
            ImageButton noButton = dialog.findViewById(R.id.noButton);

            // 다이얼로그 타이틀 및 메시지 설정
            dialogTitle.setText(title);
            dialogMessage.setText(message);

            // 예 버튼 클릭 이벤트 처리
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 로그아웃 로직
                    Toast.makeText(MyPageActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    // 로그인 페이지로 이동
                    Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });

            // 아니오 버튼 클릭 이벤트 처리
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 다이얼로그 닫기
                    dialog.dismiss();
                }
            });

            // 다이얼로그 표시
            dialog.show();
        }
    public void onBackButtonPressed(View view) {

        finish(); // 종료하고 이전 액티비티로 돌아감
    }
    }
