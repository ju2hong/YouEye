// File: com/example/youeye/login/IDLoginActivity.java
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

public class IDLoginActivity extends AppCompatActivity {

    private List<ImageButton> imageButtons;
    private List<TextView> textViews;
    private int currentIndex = 0;
    private StringBuilder inputNumber;

    private AlertDialog dialog;
    private TTSManager ttsManager;
    private SwitchManager switchManager;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idlogin);

        // TTSManager 초기화
        ttsManager = new TTSManager(this);

        // SwitchManager 초기화
        switchManager = SwitchManager.getInstance(this);

        // 스위치 상태 가져오기
        boolean isSwitchOn = switchManager.getSwitchState();

        // TTS 활성화 여부 설정
        ttsManager.setTTSOn(isSwitchOn);

        // 입력 번호를 저장할 StringBuilder 초기화
        inputNumber = new StringBuilder();

        // ImageButton 리스트 초기화
        imageButtons = new ArrayList<>();
        imageButtons.add(findViewById(R.id.textButton1));
        imageButtons.add(findViewById(R.id.textButton2));
        imageButtons.add(findViewById(R.id.textButton3));
        imageButtons.add(findViewById(R.id.textButton4));

        backButton = findViewById(R.id.backButton);

        // TextView 리스트 초기화
        textViews = new ArrayList<>();
        textViews.add(findViewById(R.id.textkey1));
        textViews.add(findViewById(R.id.textkey2));
        textViews.add(findViewById(R.id.textkey3));
        textViews.add(findViewById(R.id.textkey4));
        textViews.add(findViewById(R.id.textkey5));
        textViews.add(findViewById(R.id.textkey6));
        textViews.add(findViewById(R.id.textkey7));
        textViews.add(findViewById(R.id.textkey8));
        textViews.add(findViewById(R.id.textkey9));
        textViews.add(findViewById(R.id.textkey0));

        backButton.setOnClickListener(v -> speakButtonDescriptionAndFinish());

        // 모든 ImageButton을 초기 상태로 설정
        for (ImageButton imageButton : imageButtons) {
            imageButton.setImageResource(R.drawable.logintext);
            imageButton.setTag(null);
        }

        // TTS On/Off 버튼 초기화 및 클릭 리스너 설정
        ImageButton keyTTSButton = findViewById(R.id.keytts);
        keyTTSButton.setOnClickListener(v -> toggleTTS());
    }

    // 숫자 입력 처리 via layout's onClick
    public void addNumber(View view) {
        String tag = (String) view.getTag();
        if (tag != null) {
            int number = Integer.parseInt(tag);
            handleNumberInput(number);
        }
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

    // 마지막 문자를 삭제하는 메서드 via layout's onClick
    public void deleteLastCharacter(View view) {
        deleteLastCharacterInternal();
    }

    private void deleteLastCharacterInternal() {
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
        String enteredNumber = inputNumber.toString();
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        titleTextView.setText("로그인 아이디 확인");
        String message = "입력한 번호는 " + enteredNumber + "입니다.";
        messageTextView.setText(message);

        dialog = builder.create();
        Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.custom_dialog_background, null);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(background);
        }
        dialog.show();

        if (ttsManager.isTTSOn()) {
            ttsManager.speak("아이디를 확인해주세요");
        }

}

    // Yes 버튼 클릭 시 PWLoginActivity로 전환하면서 ID 전달
    public void onYesButtonClick(View view) {
        ttsManager.speak("예");
        String enteredId = inputNumber.toString();
        Intent intent = new Intent(IDLoginActivity.this, PWLoginActivity.class);
        intent.putExtra("id", enteredId); // 입력된 ID 값을 전달
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }

    // No 버튼 클릭 시 입력된 숫자 초기화
    public void onNoButtonClick(View view) {
        ttsManager.speak("아니오");
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


