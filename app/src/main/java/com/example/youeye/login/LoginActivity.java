package com.example.youeye.login;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.youeye.DatabaseHelper;
import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;
import com.example.youeye.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {
    private boolean switchState; // 스위치 상태를 저장할 변수
    private TTSManager ttsManager; // TTSManager 추가
    private SwitchManager switchManager; // SwitchManager 추가
    private ImageButton idInputButton;
    private ImageButton pwInputButton;
    private Switch switchLogin; // 로그인 화면의 스위치
    private TextView textViewLogin; // 스위치 상태를 표시하는 텍스트뷰
    private TextView loginView;
    private TextView pwView;
    private DatabaseHelper dbHelper; // DatabaseHelper 인스턴스 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TTSManager 및 SwitchManager 초기화
        ttsManager = new TTSManager(this);
        switchManager = SwitchManager.getInstance(this);
        dbHelper = new DatabaseHelper(this); // DatabaseHelper 인스턴스 초기화

        // 인텐트에서 스위치 상태를 받아옵니다.
        Intent intent = getIntent();
        if (intent.hasExtra("switch_state")) {
            switchState = intent.getBooleanExtra("switch_state", switchManager.getSwitchState());
            ttsManager.setTTSOn(switchState); // TTSManager에 스위치 상태 설정
            switchManager.setSwitchState(switchState); // SwitchManager에 스위치 상태 설정
        } else {
            // 인텐트가 없으면 SwitchManager의 상태를 사용
            switchState = switchManager.getSwitchState();
        }

        // 스위치와 텍스트뷰 참조
        switchLogin = findViewById(R.id.switch1);
        textViewLogin = findViewById(R.id.textView3);

        // ImageButton 참조
        idInputButton = findViewById(R.id.idInputButton);
        pwInputButton = findViewById(R.id.pwInputButton);

        // TextView 참조
        loginView = findViewById(R.id.loginView);
        pwView = findViewById(R.id.pwView);

        // 스위치 초기 상태 설정
        switchLogin.setChecked(switchState); // 저장된 스위치 상태 가져오기
        textViewLogin.setText(switchState ? "On" : "Off");

        // ImageButton 클릭 이벤트 설정
        setButtonClickListener(idInputButton, loginView, IDLoginActivity.class);
        setButtonClickListener(pwInputButton, pwView, PWLoginActivity.class);

        // 스위치 상태 변화 감지
        switchLogin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String statusText = isChecked ? "On" : "Off";
            textViewLogin.setText(statusText);
            ttsManager.setTTSOn(isChecked);
            ttsManager.speak(statusText);
            switchState = isChecked; // 스위치 상태 업데이트
            switchManager.setSwitchState(isChecked); // SwitchManager에 스위치 상태 저장
            if (isChecked) {
                switchLogin.getTrackDrawable().setColorFilter(
                        ContextCompat.getColor(LoginActivity.this, R.color.switchbar_on_color),
                        PorterDuff.Mode.MULTIPLY
                );
            } else {
                switchLogin.getTrackDrawable().clearColorFilter();
            }
        });

        // 인텐트에서 아이디와 비밀번호 값을 받아옵니다.
        if (intent.hasExtra("id")) {
            String id = intent.getStringExtra("id");
            Log.d("LoginActivity", "Received ID: " + id);
        }

        if (intent.hasExtra("pw")) {
            String pw = intent.getStringExtra("pw");
            Log.d("LoginActivity", "Received Password: " + pw);

            // HomeActivity로 전환하는 인텐트 생성
            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
            // 스위치 상태를 HomeActivity로 전달
            homeIntent.putExtra("switch_state", switchState);
            // HomeActivity 시작
            startActivity(homeIntent);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 화면이 다시 보여질 때 TTS 상태를 복원
        switchState = switchManager.getSwitchState();
        ttsManager.setTTSOn(switchState);
        switchLogin.setChecked(switchState);
        textViewLogin.setText(switchState ? "On" : "Off");
    }

    @Override
    protected void onPause() {
        // 화면이 가려질 때 TTS 상태를 저장
        switchState = ttsManager.isTTSOn();
        switchManager.setSwitchState(switchState);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }
    private boolean authenticateUser(String id, String pw) {
        // 데이터베이스 헬퍼를 사용하여 읽기 가능한 데이터베이스를 가져옵니다.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 데이터베이스에서 입력된 아이디를 사용하여 사용자 정보를 조회합니다.
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_PW},
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{id},
                null,
                null,
                null
        );

        // 커서가 null이 아니고 결과가 있으면 컬럼 인덱스를 가져옵니다.
        boolean result = false;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PW); // 비밀번호 열 이름을 "pw"에서 "password"로 변경
            if (columnIndex != -1) {
                String storedPW = cursor.getString(columnIndex);
                // 입력된 비밀번호와 데이터베이스에 저장된 비밀번호를 비교합니다.
                result = pw.equals(storedPW);
            }
            cursor.close(); // 커서를 닫습니다.
        }
        return result;
    }
    private void setButtonClickListener(ImageButton button, TextView textView, Class<?> activityClass) {
        button.setOnClickListener(v -> {
            // TTS 읽기
            if (switchState && textView != null) {
                String text = textView.getText().toString();
                ttsManager.speak(text);
            }

            // 화면 이동
            Intent intent = new Intent(LoginActivity.this, activityClass);
            intent.putExtra("switch_state", switchState); // 스위치 상태를 전달
            startActivity(intent);
        });
    }
}

