// File: com/example/youeye/member/IDMemberActivity.java
package com.example.youeye.member;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.youeye.api.CheckIdRequest;
import com.example.youeye.api.CheckIdResponse;
import com.example.youeye.api.LoginApiClient;
import com.example.youeye.api.LoginService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IDMemberActivity extends AppCompatActivity {

    private List<ImageButton> imageButtons;
    private int currentIndex = 0;
    private StringBuilder inputNumber;

    private TTSManager ttsManager;
    private SwitchManager switchManager;
    private ImageButton backButton;

    // 다이얼로그 객체를 클래스 변수로 선언
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idmember);

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

        // backButton 초기화
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> speakButtonDescriptionAndFinish());

        // 모든 ImageButton을 초기 상태로 설정
        for (ImageButton imageButton : imageButtons) {
            imageButton.setImageResource(R.drawable.logintext);
            imageButton.setTag(null);
        }

        // TTS On/Off 버튼 초기화 및 클릭 리스너 설정
        ImageButton keyTTSButton = findViewById(R.id.keyStt);
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
            // 입력이 완료되면 ID 중복 확인 실행
            checkIdDuplicate();
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

    // ID 중복 확인 메서드
    private void checkIdDuplicate() {
        String enteredId = inputNumber.toString();

        // 서버에 요청 보낼 객체 생성
        CheckIdRequest checkIdRequest = new CheckIdRequest(enteredId);

        // Retrofit 서비스 생성
        LoginService loginService = LoginApiClient.getClient().create(LoginService.class);
        Call<CheckIdResponse> call = loginService.checkid(checkIdRequest);

        // 비동기 요청
        call.enqueue(new Callback<CheckIdResponse>() {
            @Override
            public void onResponse(Call<CheckIdResponse> call, Response<CheckIdResponse> response) {
                if (response.isSuccessful()) {
                    CheckIdResponse checkIdResponse = response.body();
                    if (checkIdResponse != null) {
                        if (checkIdResponse.isExists()) {
                            // ID가 이미 존재하는 경우
                            Toast.makeText(IDMemberActivity.this, "아이디가 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                            if (ttsManager.isTTSOn()) {
                                ttsManager.speak("아이디가 이미 존재합니다.");
                            }
                            clearAllImageButtons();
                            inputNumber.setLength(0);
                            currentIndex = 0;
                        } else {
                            // ID를 사용할 수 있는 경우
                            Toast.makeText(IDMemberActivity.this, "아이디를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                            if (ttsManager.isTTSOn()) {
                                ttsManager.speak("아이디를 사용할 수 있습니다.");
                            }
                            // 다음 단계로 진행
                            showConfirmationDialog();
                        }
                    }
                } else {
                    // 서버 오류 등의 상황 처리
                    Toast.makeText(IDMemberActivity.this, "ID 중복 확인 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                    if (ttsManager.isTTSOn()) {
                        ttsManager.speak("ID 중복 확인에 실패했습니다.");
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckIdResponse> call, Throwable t) {
                // 통신 오류 처리
                Toast.makeText(IDMemberActivity.this, "통신 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                if (ttsManager.isTTSOn()) {
                    ttsManager.speak("통신 오류가 발생했습니다.");
                }
            }
        });
    }

    // 확인 다이얼로그를 표시하는 메서드
    private void showConfirmationDialog() {
        String enteredId = inputNumber.toString();

        // 다이얼로그 레이아웃을 인플레이트합니다.
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

        // AlertDialog.Builder를 사용하여 다이얼로그를 생성합니다.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // 다이얼로그 내의 TextView에 제목과 메시지를 설정합니다.
        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        titleTextView.setText("회원가입 아이디 확인");
        String message = "입력한 번호는 " + enteredId + " 입니다.";
        messageTextView.setText(message);

        // Yes, No 버튼에 대한 클릭 리스너를 설정합니다.
        ImageButton yesButton = dialogView.findViewById(R.id.yesButton);
        ImageButton noButton = dialogView.findViewById(R.id.noButton);

        yesButton.setOnClickListener(this::onYesButtonClick);
        noButton.setOnClickListener(this::onNoButtonClick);

        // AlertDialog를 생성하고 표시합니다.
        dialog = builder.create();
        // 배경 설정
        Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.custom_dialog_background, null);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(background);
        }
        dialog.show();
        if (ttsManager.isTTSOn()) {
            ttsManager.speak("비밀번호를 확인해주세요");
        }
    }

    // Yes 버튼 클릭 시 PWMemberActivity로 전환하면서 ID 전달
    public void onYesButtonClick(View view) {
        ttsManager.speak("예");
        String enteredId = inputNumber.toString();
        Intent intent = new Intent(IDMemberActivity.this, PWMemberActivity.class);
        intent.putExtra("id", enteredId); // 입력된 ID 값을 전달
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }

    // No 버튼 클릭 시 입력된 숫자 초기화
    public void onNoButtonClick(View view) {
        ttsManager.speak("아니오");
        clearAllImageButtons();
        inputNumber.setLength(0);
        currentIndex = 0;
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

    private void speakButtonDescriptionAndFinish() {
        String buttonText = backButton.getContentDescription().toString();
        if (switchManager.getSwitchState()) {
            ttsManager.speak(buttonText);

            // 예상 발화 시간 계산 (대략 100ms per character + 500ms buffer)
            int estimatedSpeechTime = buttonText.length() * 100  ;

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
