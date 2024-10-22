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

        for (ImageButton imageButton : imageButtons) {
            imageButton.setImageResource(R.drawable.logintext);
            imageButton.setTag(null);
        }

        ImageButton keyTTSButton = findViewById(R.id.pwkeyTts);
        keyTTSButton.setOnClickListener(v -> toggleTTS());

        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            userId = intent.getStringExtra("id");
        } else {

            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void addNumber(View view) {
        String tag = (String) view.getTag();
        if (tag != null) {
            int number = Integer.parseInt(tag);
            handleNumberInput(number);
        }
    }

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

    private void moveToNextImageButton() {
        currentIndex++;
        if (currentIndex >= imageButtons.size()) {
            showConfirmationDialog();
            currentIndex = 0;
        } else {
            imageButtons.get(currentIndex).requestFocus();
        }
    }

    private void toggleTTS() {
        boolean isCurrentlyOn = ttsManager.isTTSOn();
        boolean newState = !isCurrentlyOn;
        ttsManager.setTTSOn(newState);
        switchManager.setSwitchState(newState); // TTS 상태 저장
    }

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

    public void onYesButtonClick(View view) {
        String enteredPassword = inputNumber.toString();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("로그인 중...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        LoginRequest loginRequest = new LoginRequest(userId, enteredPassword);

        LoginService loginService = LoginApiClient.getClient().create(LoginService.class);

        Call<LoginResponse> call = loginService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null && loginResponse.getToken() != null) {
                        String token = loginResponse.getToken();
                        saveToken(token);

                        Intent homeIntent = new Intent(PWLoginActivity.this, HomeActivity.class);
                        homeIntent.putExtra("switch_state", switchManager.getSwitchState());
                        homeIntent.putExtra("token", token); // 필요 시 토큰 전달
                        startActivity(homeIntent);
                        finish();
                    } else {
                        showLoginFailed("서버 응답이 유효하지 않습니다.");
                    }
                } else {
                    String errorMsg = "로그인 실패: " + response.message();
                    showLoginFailed(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                String errorMsg = "로그인 실패: " + t.getMessage();
                showLoginFailed(errorMsg);
            }
        });

        dialog.dismiss();
    }

    private void saveToken(String token) {
        SharedPreferences sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("auth_token", token);
        editor.apply();
    }

    private void showLoginFailed(String message) {
        new AlertDialog.Builder(this)
                .setTitle("로그인 실패")
                .setMessage(message)
                .setPositiveButton("확인", null)
                .show();
    }

    private void speakButtonDescriptionAndFinish() {
        String buttonText = backButton.getContentDescription().toString();
        if (switchManager.getSwitchState()) {
            ttsManager.speak(buttonText);

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
