package com.example.youeye.login;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

public class PWLoginActivity extends AppCompatActivity {
    private List<ImageButton> imageButtons;
    private int currentIndex = 0;
    private List<TextView> textViews;

    private StringBuilder inputNumber;
    private AlertDialog dialog;
    private TTSManager ttsManager;
    private SwitchManager switchManager;  // SwitchManager 선언 추가
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwlogin);

        // TTSManager 초기화
        ttsManager = new TTSManager(this);

        // SwitchManager 초기화
        switchManager = SwitchManager.getInstance(this);
        boolean isSwitchOn = switchManager.getSwitchState();

        // TTS 활성화 여부 설정
        ttsManager.setTTSOn(isSwitchOn);

        // 입력 번호를 저장할 StringBuilder 초기화
        inputNumber = new StringBuilder();

        // ImageButton 리스트 초기화
        imageButtons = new ArrayList<>();
        imageButtons.add(findViewById(R.id.textPwButton1));
        imageButtons.add(findViewById(R.id.textPwButton2));
        imageButtons.add(findViewById(R.id.textPwButton3));
        imageButtons.add(findViewById(R.id.textPwButton4));


        // TextView 리스트 초기화
        textViews = new ArrayList<>();
        textViews.add(findViewById(R.id.textpwkey1));
        textViews.add(findViewById(R.id.textpwkey2));
        textViews.add(findViewById(R.id.textpwkey3));
        textViews.add(findViewById(R.id.textpwkey4));
        textViews.add(findViewById(R.id.textpwkey5));
        textViews.add(findViewById(R.id.textpwkey6));
        textViews.add(findViewById(R.id.textpwkey7));
        textViews.add(findViewById(R.id.textpwkey8));
        textViews.add(findViewById(R.id.textpwkey9));
        textViews.add(findViewById(R.id.textpwkey0));

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> speakButtonDescriptionAndFinish());

        // 모든 ImageButton을 초기 상태로 설정
        for (ImageButton imageButton : imageButtons) {
            imageButton.setImageResource(R.drawable.logintext);
            imageButton.setTag(null);
        }

        // TTS On/Off 버튼 초기화 및 클릭 리스너 설정
        ImageButton keyTTSButton = findViewById(R.id.pwkeyTts);
        keyTTSButton.setOnClickListener(v -> toggleTTS());

        // 0부터 9까지 숫자 버튼에 클릭 리스너 설정
        for (int i = 0; i < 10; i++) {
            final int number = i;
            int buttonId = getResources().getIdentifier("textpwkey" + i, "id", getPackageName());
            findViewById(buttonId).setOnClickListener(v -> handleNumberInput(number));
        }

        // 삭제 버튼 클릭 리스너 설정
        findViewById(R.id.deletePwButton).setOnClickListener(v -> deleteLastCharacter());
    }

    // 숫자 입력 처리
    private void handleNumberInput(int number) {
        if (currentIndex < imageButtons.size()) {
            ImageButton currentImageButton = imageButtons.get(currentIndex);
            currentImageButton.setImageResource(R.drawable.textinput);
            currentImageButton.setTag(String.valueOf(number));
            inputNumber.append(number);
            if (ttsManager.isTTSOn()) {
                ttsManager.speak(String.valueOf(number));
            }
            moveToNextImageButton();
        }
    }

    // 마지막 문자를 삭제하는 메서드
    private void deleteLastCharacter() {
        if (currentIndex > 0) {
            currentIndex--;
            ImageButton previousImageButton = imageButtons.get(currentIndex);
            previousImageButton.setImageResource(R.drawable.logintext);
            previousImageButton.setTag(null);
            inputNumber.deleteCharAt(inputNumber.length() - 1);
        }
    }

    // 다음 ImageButton으로 이동하는 메서드
    private void moveToNextImageButton() {
        currentIndex++;
        if (currentIndex >= imageButtons.size()) {
            showConfirmationDialog();
            currentIndex = 0;
        } else {
            imageButtons.get(currentIndex).requestFocus();
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
        titleTextView.setText("비밀번호 확인");
        String message = getString(R.string.confirmation_message, enteredNumber);
        messageTextView.setText(message);

        dialog = builder.create();

        Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.custom_dialog_background, null);
        dialog.getWindow().setBackgroundDrawable(background);
        dialog.show();
    }

    // Yes 버튼 클릭 시 LoginActivity로 전환
    public void onYesButtonClick(View view) {
        String enteredNumber = inputNumber.toString();
        Intent intent = new Intent(PWLoginActivity.this, LoginActivity.class);
        intent.putExtra("pw", enteredNumber);
        startActivity(intent);
    }

    // No 버튼 클릭 시 입력된 숫자 초기화
    public void onNoButtonClick(View view) {
        clearAllImageButtons();
        inputNumber.setLength(0);
        dialog.dismiss();
    }

    // 모든 ImageButton을 초기화하는 메서드
    private void clearAllImageButtons() {
        for (ImageButton imageButton : imageButtons) {
            imageButton.setImageResource(R.drawable.logintext);
            imageButton.setTag(null);
        }
        currentIndex = 0;
    }

    private void speakButtonDescriptionAndFinish() {
        String buttonText = backButton.getContentDescription().toString();
        if (switchManager.getSwitchState()) {
            ttsManager.speak(buttonText);

            // 예상 발화 시간 계산 (대략 100ms per character + 500ms buffer)
            int estimatedSpeechTime = buttonText.length() * 100 ;

            new Handler().postDelayed(this::finishWithAnimation, estimatedSpeechTime);
        } else {
            finishWithAnimation();
        }
    }
    private void finishWithAnimation() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        speakButtonDescriptionAndFinish();
    }
}
