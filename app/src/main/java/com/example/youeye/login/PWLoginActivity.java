// File: com/example/youeye/login/PWLoginActivity.java
package com.example.youeye.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.youeye.api.LoginApiClient;
import com.example.youeye.api.LoginRequest;
import com.example.youeye.api.LoginResponse;
import com.example.youeye.api.LoginService;
import com.example.youeye.home.HomeActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private String userId; // 사용자 ID를 받아올 변수

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

        // 삭제 버튼은 레이아웃의 onClick 속성을 통해 처리하므로 여기서는 설정하지 않습니다.

        // 인텐트에서 사용자 ID를 받아옴
        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            userId = intent.getStringExtra("id");
        } else {
            // 사용자 ID가 없는 경우 처리
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish(); // 액티비티 종료
        }
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
        String message = "입력한 비밀번호는 " + enteredNumber + "입니다. 맞습니까?"; // 또는 문자열 리소스를 사용하세요.
        messageTextView.setText(message);

        dialog = builder.create();

        Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.custom_dialog_background, null);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(background);
        }
        dialog.show();
    }

    // Yes 버튼 클릭 시 로그인 API 호출
    public void onYesButtonClick(View view) {
        String enteredPassword = inputNumber.toString();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // 프로그레스 다이얼로그 표시 (옵션)
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("로그인 중...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // LoginRequest 객체 생성
        LoginRequest loginRequest = new LoginRequest(userId, enteredPassword);

        // Retrofit 인스턴스와 LoginService 생성
        LoginService loginService = LoginApiClient.getClient().create(LoginService.class);

        // API 호출
        Call<LoginResponse> call = loginService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null && loginResponse.getToken() != null) {
                        String token = loginResponse.getToken();
                        // 토큰 저장 (예: SharedPreferences)
                        saveToken(token);

                        // HomeActivity로 전환
                        Intent homeIntent = new Intent(PWLoginActivity.this, HomeActivity.class);
                        homeIntent.putExtra("switch_state", switchManager.getSwitchState());
                        homeIntent.putExtra("token", token); // 필요 시 토큰 전달
                        startActivity(homeIntent);
                        finish(); // 현재 액티비티 종료
                    } else {
                        // 응답에 토큰이 없는 경우 처리
                        showLoginFailed("서버 응답이 유효하지 않습니다.");
                    }
                } else {
                    // 응답이 실패한 경우 처리
                    String errorMsg = "로그인 실패: " + response.message();
                    showLoginFailed(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                // 네트워크 오류 등 처리
                String errorMsg = "로그인 실패: " + t.getMessage();
                showLoginFailed(errorMsg);
            }
        });

        // 다이얼로그 닫기
        dialog.dismiss();
    }

    // 토큰을 SharedPreferences에 저장하는 메서드
    private void saveToken(String token) {
        SharedPreferences sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("auth_token", token);
        editor.apply();
    }

    // 로그인 실패 시 메시지를 표시하는 메서드
    private void showLoginFailed(String message) {
        new AlertDialog.Builder(this)
                .setTitle("로그인 실패")
                .setMessage(message)
                .setPositiveButton("확인", null)
                .show();
    }

    // 뒤로가기 버튼 설명 및 종료 처리 메서드
    private void speakButtonDescriptionAndFinish() {
        String buttonText = backButton.getContentDescription().toString();
        if (switchManager.getSwitchState()) {
            ttsManager.speak(buttonText);

            // 예상 발화 시간 계산 (대략 100ms per character + 500ms buffer)
            int estimatedSpeechTime = buttonText.length() * 100;

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
