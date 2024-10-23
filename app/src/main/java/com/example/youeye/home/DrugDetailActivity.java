package com.example.youeye.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DrugDetailActivity extends AppCompatActivity {

    private TextView textViewName, textViewDetail, textView15;
    private ImageView medicineImageView;
    private static final String TAG = "DrugDetailActivity";
    private TextToSpeech tts;
    private ImageButton ttsButton, imageButton7;
    private TTSManager ttsManager;
    private SwitchManager switchManager;
    private boolean isSpeaking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugdetail);

        textViewName = findViewById(R.id.textView12); // 약품명 텍스트뷰
        textViewDetail = findViewById(R.id.textView14); // 스크롤뷰 안의 첫번째 텍스트뷰
        textView15 = findViewById(R.id.textView15); // 스크롤뷰 안의 두번째 텍스트뷰 (주의사항 및 부작용 등)
        medicineImageView = findViewById(R.id.medicineImageView);
        ttsButton = findViewById(R.id.imageButton5); // 스피커 버튼
        imageButton7 = findViewById(R.id.imageButton7);

        // TTSManager 초기화
        ttsManager = new TTSManager(this);
        switchManager = SwitchManager.getInstance(this);
        imageButton7.setOnClickListener(v -> speakButtonDescriptionAndFinish());

        // TextToSpeech 초기화
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.KOREAN); // 한국어 설정
            }
        });

        // TTS 버튼 클릭 이벤트 설정
        ttsButton.setOnClickListener(v -> {
            String name = textViewName.getText().toString(); // 약품명
            String additionalInfo = textView15.getText().toString(); // 스크롤뷰 안의 두번째 텍스트뷰 (주의사항 및 부작용 등)

            // 읽어줄 텍스트 구성 (약품명 -> 주의사항 및 부작용 -> 추가적인 정보)
            String textToSpeak = name + ", " + additionalInfo;

            // TTS 상태 확인
            if (isSpeaking) {
                // TTS가 현재 말하고 있다면 멈추기
                stopTTS(); // TTS 멈추기
            } else {
                // TTS가 현재 말하고 있지 않다면 읽기 시작
                tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                isSpeaking = true; // Speaking 상태 업데이트
            }
        });


        // 인텐트로 전달된 데이터 가져오기
        String name = getIntent().getStringExtra("name");
        String company = getIntent().getStringExtra("company");
        String validity = getIntent().getStringExtra("validity");
        String storage = getIntent().getStringExtra("storage");

        // showDetails 플래그를 확인하여 textViewDetail의 가시성 설정
        boolean showDetails = getIntent().getBooleanExtra("showDetails", false);
        if (showDetails) {
            String details = "회사: " + company + "\n유효기간: " + validity + "\n보관방법: " + storage;
            textViewDetail.setText(details);
            textViewDetail.setVisibility(View.VISIBLE);
        } else {
            textViewDetail.setVisibility(View.GONE); // CSV 데이터만 있을 때는 숨기기
        }

        textViewName.setText(name);

        // 약품리스트 저장 (SharedPreferences에 약품명 저장)
        saveSearchedMedicine(name);

        // CSV에서 추가적인 약품 정보 표시
        displayDrugDetailsFromCSV(name);

        // 이미지 로드
        loadMedicineImageFromCSV(name);  // 수정된 이미지 로드 방식
    }

    // 약품리스트 저장: 약품명을 SharedPreferences에 저장하는 메소드
    private void saveSearchedMedicine(String medicineName) {
        // SharedPreferences 객체 생성
        SharedPreferences sharedPreferences = getSharedPreferences("SearchedMedicines", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 기존에 저장된 약품명 리스트 가져오기 (Immutable Set이므로 새롭게 복사하여 수정해야 함)
        Set<String> medicineSet = sharedPreferences.getStringSet("medicineList", new HashSet<>());
        Set<String> updatedMedicineSet = new HashSet<>(medicineSet);  // 기존 데이터를 새로운 Set으로 복사

        // 새로운 약품명을 리스트에 추가
        updatedMedicineSet.add(medicineName);

        // 업데이트된 리스트를 SharedPreferences에 저장
        editor.putStringSet("medicineList", updatedMedicineSet);
        editor.apply();  // 비동기로 저장
        Toast.makeText(this, "약품명이 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private void loadMedicineImageFromCSV(String productName) {
        try {
            InputStream is = getAssets().open("druglink.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is, "UTF-8"));
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                String csvMedicineName = nextLine[1]; // 2번째 열의 약품명
                String imageUrl = nextLine[2];        // 3번째 열의 이미지 링크

                if (csvMedicineName.equals(productName)) {
                    Log.d(TAG, "이미지 링크: " + imageUrl);

                    // 이미지 로딩
                    Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.load)  // 로딩 중에 표시할 이미지
                            .error(R.drawable.error)       // 로드 실패 시 표시할 이미지
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Log.e(TAG, "이미지 로드 실패", e);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(medicineImageView);
                    break;
                }
            }
            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "CSV 파일 읽기 오류", e);
            Toast.makeText(this, "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // CSV에서 약품 정보를 찾아서 textView15에 표시하는 메소드
    private void displayDrugDetailsFromCSV(String drugName) {
        try {
            InputStream is = getAssets().open("detaildrug.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is, "UTF-8"));
            String[] nextLine;
            boolean found = false;

            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[0].equals(drugName)) {
                    String efficacy = nextLine[1];
                    String usage = nextLine[2];
                    String caution = nextLine[3];
                    String multipleContra = nextLine[4];
                    String sideEffects = nextLine[5];

                    String drugDetails = "효능: " + efficacy + "\n" +
                            "복용법: " + usage + "\n" +
                            "주의사항: " + caution + "\n" +
                            "다중복용금지: " + multipleContra + "\n" +
                            "부작용: " + sideEffects;

                    textView15.setText(drugDetails);
                    found = true;
                    break;
                }
            }

            if (!found) {
                textView15.setText("해당 약품 정보를 찾을 수 없습니다.");
            }

            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "CSV 파일을 읽는 중 오류 발생", e);
            textView15.setText("약품 정보를 불러오지 못했습니다.");
        }
    }

    // TTS 멈추는 메소드
    private void stopTTS() {
        if (tts.isSpeaking()) {
            tts.stop();
        }
        isSpeaking = false; // Speaking 상태 업데이트
    }

    private void speakButtonDescriptionAndFinish() {
        String buttonText = imageButton7.getContentDescription().toString();
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
        stopTTS(); // 페이지를 나갈 때 TTS 종료
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTTS(); // 페이지를 나갈 때 TTS 종료
        speakButtonDescriptionAndFinish();
    }

}
