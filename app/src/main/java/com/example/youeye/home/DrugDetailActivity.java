package com.example.youeye.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

        textViewName = findViewById(R.id.textView12);
        textViewDetail = findViewById(R.id.textView14);
        textView15 = findViewById(R.id.textView15);
        medicineImageView = findViewById(R.id.medicineImageView);
        ttsButton = findViewById(R.id.imageButton5);
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
            if (isSpeaking) {
                stopTTS();
            } else {
                speakText();
            }
        });
        imageButton7.setOnClickListener(v -> speakBackButtonAndFinish());

        // 인텐트로 전달된 데이터 가져오기
        String name = getIntent().getStringExtra("name");
        String company = getIntent().getStringExtra("company");
        String validity = getIntent().getStringExtra("validity");
        String storage = getIntent().getStringExtra("storage");

        // 데이터 표시
        textViewName.setText(name);
        String details = "회사: " + company + "\n유효기간: " + validity + "\n보관방법: " + storage;
        textViewDetail.setText(details);

        // CSV 파일에서 약품 정보를 읽어서 textView15에 표시
        displayDrugDetailsFromCSV(name);

        // CSV 파일에서 이미지 링크를 가져와 표시
        loadMedicineImage(name);
    }

    // CSV 파일에서 약품의 이미지를 불러와서 medicineImageView에 표시하는 메서드
    private void loadMedicineImage(String productName) {
        String productSerial = findSerialFromDetailDrug(productName);
        if (productSerial != null) {
            String imageUrl = findImageLinkFromDrugLink(productSerial);
            if (imageUrl != null) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder)  // 로딩 중에 표시할 이미지
                        .error(R.drawable.error)             // 로드 실패 시 표시할 이미지
                        .into(medicineImageView);            // 이미지 표시
            } else {
                Log.e(TAG, "이미지 링크를 찾을 수 없습니다.");
            }
        } else {
            Log.e(TAG, "제품 일련번호를 찾을 수 없습니다.");
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
