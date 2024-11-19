// File: com/example/youeye/member/PWMemberActivity.java
package com.example.youeye.member;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings; // android_id 가져오기 위해 추가
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;
import com.example.youeye.api.RegisterRequest;
import com.example.youeye.api.RegisterResponse;
import com.example.youeye.api.LoginApiClient; // 클래스명 확인
import com.example.youeye.api.LoginService;
import com.example.youeye.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PWMemberActivity extends AppCompatActivity {
    private List<ImageButton> imageButtons;
    private int currentIndex = 0;
    private StringBuilder inputNumber;
    private AlertDialog dialog;
    private String enteredId;

    private TTSManager ttsManager;
    private SwitchManager switchManager;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwmember);

        // TTSManager 초기화
        ttsManager = new TTSManager(this);

        // SwitchManager 초기화
        switchManager = SwitchManager.getInstance(this);

        // 스위치 상태 가져오기
        boolean isSwitchOn = switchManager.getSwitchState();

        // TTS 활성화 여부 설정
        ttsManager.setTTSOn(isSwitchOn);

        // 입력된 ID 가져오기
        Intent intent = getIntent();
        enteredId = intent.getStringExtra("id");

        inputNumber = new StringBuilder(); // StringBuilder 초기화

        // ImageButton 리스트 초기화
        imageButtons = new ArrayList<>();
        imageButtons.add(findViewById(R.id.textPwButton1));
        imageButtons.add(findViewById(R.id.textPwButton2));
        imageButtons.add(findViewById(R.id.textPwButton3));
        imageButtons.add(findViewById(R.id.textPwButton4));

        // backButton 초기화 및 클릭 리스너 설정
        backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> speakButtonDescriptionAndFinish());
        } else {
            Log.e("PWMemberActivity", "backButton is null - Check activity_pwmember.xml layout file");
            Toast.makeText(this, "뒤로가기 버튼을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        // 모든 ImageButton을 초기 상태로 설정합니다.
        for (ImageButton imageButton : imageButtons) {
            if (imageButton != null) {
                imageButton.setImageResource(R.drawable.logintext);
                imageButton.setTag(null);
            } else {
                Log.e("PWMemberActivity", "One of the imageButtons is null - Check activity_pwmember.xml layout file");
            }
        }

        // TTS On/Off 버튼 초기화 및 클릭 리스너 설정
        ImageButton keyTTSButton = findViewById(R.id.pwkeyStt);
        if (keyTTSButton != null) {
            keyTTSButton.setOnClickListener(v -> toggleTTS());
        } else {
            Log.e("PWMemberActivity", "keyTTSButton is null - Ensure the view exists in the layout");
        }
    }

    // 숫자 입력 처리 via layout's onClick
    public void addNumber(View view) {
        String tag = (String) view.getTag();
        if (tag != null) {
            try {
                int number = Integer.parseInt(tag);
                handleNumberInput(number);
            } catch (NumberFormatException e) {
                Log.e("PWMemberActivity", "Invalid tag value: " + tag);
            }
        }
    }

    // 숫자 입력 처리
    private void handleNumberInput(int number) {
        if (currentIndex < imageButtons.size()) {
            ImageButton currentImageButton = imageButtons.get(currentIndex);
            if (currentImageButton != null) {
                currentImageButton.setImageResource(R.drawable.textinput);
                currentImageButton.setTag(String.valueOf(number));
                inputNumber.append(number);
                if (ttsManager.isTTSOn()) {
                    ttsManager.speak(String.valueOf(number));
                }
                moveToNextImageButton();
            } else {
                Log.e("PWMemberActivity", "Current ImageButton is null at index: " + currentIndex);
            }
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
            if (previousImageButton != null) {
                previousImageButton.setImageResource(R.drawable.logintext);
                previousImageButton.setTag(null);
                if (inputNumber.length() > 0) {
                    inputNumber.deleteCharAt(inputNumber.length() - 1);
                }
            } else {
                Log.e("PWMemberActivity", "Previous ImageButton is null at index: " + currentIndex);
            }
        }
    }

    // 다음 ImageButton으로 이동하는 메서드
    private void moveToNextImageButton() {
        currentIndex++;
        if (currentIndex >= imageButtons.size()) {
            showConfirmationDialog();
        } else {
            ImageButton nextImageButton = imageButtons.get(currentIndex);
            if (nextImageButton != null) {
                nextImageButton.requestFocus();
            } else {
                Log.e("PWMemberActivity", "Next ImageButton is null at index: " + currentIndex);
            }
        }
    }

    // TTS 토글 메서드
    private void toggleTTS() {
        boolean isCurrentlyOn = ttsManager.isTTSOn();
        boolean newState = !isCurrentlyOn;
        ttsManager.setTTSOn(newState);
        switchManager.setSwitchState(newState); // TTS 상태 저장
    }

    // 확인 팝업창을 띄우는 메서드
    private void showConfirmationDialog() {
        String enteredPassword = inputNumber.toString();

        // LayoutInflater를 사용하여 custom_dialog_layout.xml 파일을 inflate합니다.
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

        // AlertDialog.Builder를 사용하여 다이얼로그를 생성합니다.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // 다이얼로그 내의 TextView에 제목과 메시지를 설정합니다.
        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        if (titleTextView != null) {
            titleTextView.setText("회원가입 비밀번호 확인");
        }
        if (messageTextView != null) {
            String message = "입력한 번호 " + enteredPassword + " 입니다.";
            messageTextView.setText(message);
            if (ttsManager.isTTSOn()) {
                ttsManager.speak("비밀번호를 확인해주세요");
            }
        }

        // Yes, No 버튼에 대한 클릭 리스너를 설정합니다.
        View yesButton = dialogView.findViewById(R.id.yesButton);
        View noButton = dialogView.findViewById(R.id.noButton);

        if (yesButton != null) {
            yesButton.setOnClickListener(this::onYesButtonClick);
        } else {
            Log.e("PWMemberActivity", "Yes Button is null in the dialog");
        }

        if (noButton != null) {
            noButton.setOnClickListener(this::onNoButtonClick);
        } else {
            Log.e("PWMemberActivity", "No Button is null in the dialog");
        }

        // AlertDialog를 생성하고 표시합니다.
        dialog = builder.create();
        // 배경 설정
        Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.custom_dialog_background, null);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(background);
        }
        dialog.show();
    }

    // Yes 버튼 클릭 시, 서버에 회원가입 요청을 보냅니다.
    public void onYesButtonClick(View view) {
        String enteredPassword = inputNumber.toString();

        // android_id 가져오기
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // 회원가입 요청 보내기 (android_id 포함)
        RegisterRequest registerRequest = new RegisterRequest(enteredId, enteredPassword, androidId);

        LoginService loginService = LoginApiClient.getClient().create(LoginService.class);
        Call<RegisterResponse> call = loginService.register(registerRequest);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    // 회원가입 성공
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse != null) {
                        Toast.makeText(PWMemberActivity.this, "회원가입 성공: " + registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PWMemberActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                    }

                    // 로그인 화면으로 이동
                    Intent intent = new Intent(PWMemberActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // 회원가입 실패
                    Toast.makeText(PWMemberActivity.this, "회원가입 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    clearAllImageButtons();
                    inputNumber.setLength(0);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                // 통신 실패
                Toast.makeText(PWMemberActivity.this, "통신 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                clearAllImageButtons();
                inputNumber.setLength(0);
            }
        });
    }

    // No 버튼 클릭 시 입력된 숫자를 다시 초기화하는 메서드
    public void onNoButtonClick(View view) {
        clearAllImageButtons(); // 모든 이미지 버튼을 초기화합니다.
        inputNumber.setLength(0);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss(); // 팝업창을 닫습니다
        }
    }

    // 모든 ImageButton을 초기화하는 메서드
    public void clearAllImageButtons() {
        for (ImageButton imageButton : imageButtons) {
            if (imageButton != null) {
                imageButton.setImageResource(R.drawable.logintext);
                imageButton.setTag(null);
            }
        }
        currentIndex = 0; // 현재 인덱스를 초기화하여 처음부터 다시 입력할 수 있도록 합니다.
    }

    private void speakButtonDescriptionAndFinish() {
        if (backButton != null) {
            String buttonText = backButton.getContentDescription() != null ? backButton.getContentDescription().toString() : "뒤로가기";
            if (switchManager.getSwitchState()) {
                ttsManager.speak(buttonText);

                // 예상 발화 시간 계산 (대략 100ms per character + 500ms buffer)
                int estimatedSpeechTime = buttonText.length() * 100 ;

                new Handler().postDelayed(this::finishWithAnimation, estimatedSpeechTime);
            } else {
                finishWithAnimation();
            }
        } else {
            Log.e("PWMemberActivity", "backButton is null when attempting to speak description");
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
