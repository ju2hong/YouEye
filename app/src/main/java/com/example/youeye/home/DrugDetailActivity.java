package com.example.youeye.home;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Locale;

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

            // TTS로 텍스트 읽기
            tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
        });


        // TTS 버튼 클릭 이벤트 설정
        ttsButton.setOnClickListener(v -> {
            String name = textViewName.getText().toString(); // 약품명
            String additionalInfo = textView15.getText().toString(); // 스크롤뷰 안의 두번째 텍스트뷰 내용

            // 읽어줄 텍스트 구성
            String textToSpeak = name + ", " + additionalInfo;

            // TTS로 텍스트 읽기
            tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
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

        // CSV에서 추가적인 약품 정보 표시
        displayDrugDetailsFromCSV(name);

        // 이미지 로드
        loadMedicineImage(name);
    }

    private void loadMedicineImage(String productName) {
        // 로딩 이미지 보여주기
        ImageView loadingImageView = findViewById(R.id.loadingImageView);
        loadingImageView.setVisibility(View.VISIBLE);

        // 약품 이미지 표시하는 ImageView
        ImageView medicineImageView = findViewById(R.id.medicineImageView);

        String productSerial = findSerialFromDetailDrug(productName);
        if (productSerial != null) {
            String imageUrl = findImageLinkFromDrugLink(productSerial);
            if (imageUrl != null) {
                // Glide를 사용하여 이미지 로딩
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.load)  // 로딩 중에 표시할 이미지
                        .error(R.drawable.error)              // 로드 실패 시 표시할 이미지
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // 이미지 로드 실패 시 로딩 이미지 감추기
                                loadingImageView.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // 이미지 로드 완료 시 로딩 이미지 감추기
                                loadingImageView.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(medicineImageView);
            } else {
                Log.e(TAG, "이미지 링크를 찾을 수 없습니다.");
                loadingImageView.setVisibility(View.GONE);
            }
        } else {
            Log.e(TAG, "제품 일련번호를 찾을 수 없습니다.");
            loadingImageView.setVisibility(View.GONE);
        }
    }

    // detaildrug.csv에서 제품명의 일련번호를 찾는 메서드
    private String findSerialFromDetailDrug(String productName) {
        try {
            InputStream is = getAssets().open("detaildrug.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is, "UTF-8"));
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                // 7열의 제품명 확인
                if (nextLine[0].equals(productName)) {
                    reader.close();
                    return nextLine[6];  // 7열에 일련번호가 있음
                }
            }
            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "CSV 파일을 읽는 중 오류 발생", e);
        }
        return null;
    }

    // druglink.csv에서 일련번호로 이미지 링크를 찾는 메서드
    private String findImageLinkFromDrugLink(String productSerial) {
        try {
            InputStream is = getAssets().open("druglink.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is, "UTF-8"));
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                // 1열의 일련번호가 productSerial과 일치하는지 확인
                if (nextLine[0].equals(productSerial)) {
                    reader.close();
                    return nextLine[2];  // 이미지 링크는 3열에 있음
                }
            }
            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "CSV 파일을 읽는 중 오류 발생", e);
        }
        return null;
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
        isSpeaking = false;
    }

    private void speakBackButtonAndFinish() {
        String buttonText = imageButton7.getContentDescription().toString();
        if (switchManager.getSwitchState()) {
            ttsManager.speak(buttonText);

            int estimatedSpeechTime = buttonText.length() * 100;
            new Handler().postDelayed(this::finishWithAnimation, estimatedSpeechTime);
        } else {
            finishWithAnimation();
        }
    }

    private void finishWithAnimation() {
        Intent intent = new Intent(this, TextSearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        stopTTS();
    }

    private void speakText() {
        String textToSpeak = textViewName.getText().toString() + ", " +
                textView15.getText().toString() + ", " +
                textViewDetail.getText().toString();

        tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
        isSpeaking = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTTS();
    }
}
