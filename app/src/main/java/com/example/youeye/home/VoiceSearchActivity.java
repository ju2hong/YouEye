package com.example.youeye.home;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;

public class VoiceSearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicesearch);
    }
    public void onBackButtonPressed(View view) {

        finish(); // 종료하고 이전 액티비티로 돌아감
    }

}
