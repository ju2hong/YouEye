package com.example.youeye.home;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;

import java.util.ArrayList;
import java.util.List;

public class ChangePasswordActivity extends AppCompatActivity {
    private List<ImageButton> imageButtons;
    private int currentIndex = 0;

    private AlertDialog dialog;
    private TTSManager ttsManager;
    private SwitchManager switchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepw);

        // TTSManager 초기화
        ttsManager = new TTSManager(this);

        // SwitchManager 초기화
        switchManager = SwitchManager.getInstance(this);
        boolean isSwitchOn = switchManager.getSwitchState();

        // TTS 활성화 여부 설정
        ttsManager.setTTSOn(isSwitchOn);

        // ImageButton 리스트 초기화
        imageButtons = new ArrayList<>();
        imageButtons.add(findViewById(R.id.chpwButton1));
        imageButtons.add(findViewById(R.id.chpwButton2));
        imageButtons.add(findViewById(R.id.chpwButton3));
        imageButtons.add(findViewById(R.id.chpwButton4));

        // 모든 ImageButton을 초기 상태로 설정
        for (ImageButton imageButton : imageButtons) {
            imageButton.setImageResource(R.drawable.logintext);
            imageButton.setTag(null);
        }

        // TTS On/Off 버튼 초기화 및 클릭 리스너 설정
        ImageButton keyTTSButton = findViewById(R.id.chpwkeyTts);
        keyTTSButton.setOnClickListener(v -> toggleTTS());

        // 0부터 9까지 숫자 버튼에 클릭 리스너 설정
        for (int i = 0; i < 10; i++) {
            final int number = i;
            int buttonId = getResources().getIdentifier("textchpwkey" + i, "id", getPackageName());
            findViewById(buttonId).setOnClickListener(v -> handleNumberInput(number));
        }

        // 삭제 버튼 클릭 리스너 설정
        findViewById(R.id.deleteChpwButton).setOnClickListener(v -> deleteLastCharacter());
    }

    // 숫자 입력 처리
    private void handleNumberInput(int number) {
        if (currentIndex < imageButtons.size()) {
            ImageButton currentImageButton = imageButtons.get(currentIndex);
            // 이미지를 textinput으로 변경
            currentImageButton.setImageResource(R.drawable.textinput);
            currentImageButton.setTag(String.valueOf(number));
            if (ttsManager.isTTSOn()) {
                ttsManager.speak(String.valueOf(number));
            }
            currentIndex++;
            if (currentIndex >= imageButtons.size()) {
                showConfirmationDialog();
                currentIndex = 0;
            }
        }
    }

    // 마지막 문자를 삭제하는 메서드
    private void deleteLastCharacter() {
        if (currentIndex > 0) {
            currentIndex--;
            ImageButton previousImageButton = imageButtons.get(currentIndex);
            previousImageButton.setImageResource(R.drawable.logintext);
            previousImageButton.setTag(null);
        }
    }

    // TTS 토글 메서드
    private void toggleTTS() {
        boolean isCurrentlyOn = ttsManager.isTTSOn();
        boolean newState = !isCurrentlyOn;
        ttsManager.setTTSOn(newState);
        switchManager.setSwitchState(newState); // TTS 상태 저장
    }

    // 확인 다이얼로그를 표시하는 메서드
    private void showConfirmationDialog() {
        StringBuilder enteredNumber = new StringBuilder();
        for (ImageButton imageButton : imageButtons) {
            String number = (String) imageButton.getTag();
            if (number != null) {
                enteredNumber.append(number);
            }
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        titleTextView.setText("비밀번호 변경 확인");
        String message = getString(R.string.confirmation_message, enteredNumber);
        messageTextView.setText(message);

        dialog = builder.create();

        Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.custom_dialog_background, null);
        dialog.getWindow().setBackgroundDrawable(background);
        dialog.show();
    }

    // Yes 버튼 클릭 시 SettingsActivity로 전환
    public void onYesButtonClick(View view) {
        Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    // No 버튼 클릭 시 입력된 숫자 초기화
    public void onNoButtonClick(View view) {
        clearAllImageButtons();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    // 모든 ImageButton을 초기화하는 메서드
    private void clearAllImageButtons() {
        for (ImageButton imageButton : imageButtons) {
            imageButton.setImageResource(R.drawable.logintext);
            imageButton.setTag(null);
        }
        currentIndex = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTSManager 리소스 해제
        ttsManager.shutdown();
    }
}
