package com.example.youeye.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;
import com.example.youeye.api.ApiClient;
import com.example.youeye.api.MedicineApiService;
import com.opencsv.CSVReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TextSearchActivity extends AppCompatActivity {
    private TTSManager ttsManager;
    private SwitchManager switchManager;
    private ImageButton backBtn;
    private TextView backtxt;

    private static final String TAG = "TextSearchActivity";
    private static final String API_KEY = "d3eydv/570Cqm8T8No9Hs/Vw0AGDF6tR1kSF2S8cY1IjtvSp/sWS5I5CERN3V8/58vdDWwbnVfdtZlP8KQRLUg=="; // 여기에 실제 API 키를 넣으세요.
    private MedicineApiService apiService;

    private EditText searchEditText;
    private ImageButton searchButton;
    private TextView editTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textsearch);

        // TTSManager 초기화
        ttsManager = new TTSManager(this);
        switchManager = SwitchManager.getInstance(this);

        // View 초기화
        backtxt = findViewById(R.id.backtxt);
        backBtn = findViewById(R.id.backBtn);
        searchButton = findViewById(R.id.search_button);
        editTextSearch = findViewById(R.id.editTextSearch); // TextView 초기화
        searchEditText = findViewById(R.id.editTextSearch);

        // VoiceSearchActivity에서 전달된 텍스트 받기
        Intent intent = getIntent();
        String recognizedText = intent.getStringExtra("recognizedText");

        // 전달받은 텍스트를 EditText에 표시
        if (recognizedText != null && !recognizedText.isEmpty()) {
            searchEditText.setText(recognizedText);
            Log.d(TAG, "Received recognized text: " + recognizedText);
        }

        // 검색창 초기화
        if (intent.getBooleanExtra("clearSearch", false)) {
            searchEditText.setText(""); // 검색창 초기화
        }

        // 뒤로가기 버튼 클릭 이벤트 설정
        backBtn.setOnClickListener(v -> speakButtonDescriptionAndFinish());

        // 검색 버튼 클릭 이벤트 설정
        searchButton.setOnClickListener(v -> {
            speakSearchButtonDescription();
            String query = searchEditText.getText().toString();
            searchMedicine(query);
        });

        apiService = ApiClient.getClient("http://apis.data.go.kr/1471000/SafeStadDrugService/").create(MedicineApiService.class);
    }

    private void searchMedicine(String query) {
        // API 서비스 호출
        apiService.searchMedicine(API_KEY, 1, 10, "xml", query).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String xmlResponse = response.body().string();
                        Log.d(TAG, "응답 문자열: " + xmlResponse);

                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

                        Element root = doc.getDocumentElement();
                        NodeList errMsgList = root.getElementsByTagName("errMsg");

                        if (errMsgList.getLength() > 0 && errMsgList.item(0) != null) {
                            String errorMsg = errMsgList.item(0).getTextContent();
                            Log.e(TAG, "오류 메시지: " + errorMsg);
                            Toast.makeText(TextSearchActivity.this, "오류 발생: " + errorMsg, Toast.LENGTH_SHORT).show();

                            // API에서 정보가 없으면 CSV 파일에서 검색
                            searchFromCSV(query);
                        } else {
                            // API 응답에서 약품 정보 추출 및 DrugDetailActivity로 전달
                            NodeList items = root.getElementsByTagName("item");
                            if (items.getLength() > 0 && items.item(0) != null) {
                                Element item = (Element) items.item(0);

                                String name = getTagValue(item, "PRDLST_NM");
                                String company = getTagValue(item, "BSSH_NM");
                                String validity = getTagValue(item, "VLD_PRD_YMD");
                                String storage = getTagValue(item, "STRG_MTH_CONT");

                                // DrugDetailActivity로 데이터 전달
                                Intent intent = new Intent(TextSearchActivity.this, DrugDetailActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("company", company);
                                intent.putExtra("validity", validity);
                                intent.putExtra("storage", storage);
                                startActivity(intent);
                            } else {
                                // API에 정보가 없는 경우
                                searchFromCSV(query);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "XML 파싱 오류: " + e.getMessage(), e);
                        Toast.makeText(TextSearchActivity.this, "응답을 처리하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        // 오류 발생 시 CSV 파일에서 검색
                        searchFromCSV(query);
                    }
                } else {
                    Log.e(TAG, "응답이 실패했습니다.");
                    Toast.makeText(TextSearchActivity.this, "요청에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    // API 실패 시 CSV 파일에서 검색
                    searchFromCSV(query);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "요청 중 오류가 발생했습니다: " + t.getMessage());
                Toast.makeText(TextSearchActivity.this, "요청 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                // API 실패 시 CSV 파일에서 검색
                searchFromCSV(query);
            }
        });
    }

    // CSV 파일에서 검색어에 해당하는 약품 정보를 찾고 DrugDetailActivity로 전달하는 메소드
    private void searchFromCSV(String query) {
        try {
            // assets 폴더에서 detaildrug.csv 파일 열기
            InputStream is = getAssets().open("detaildrug.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is, "UTF-8"));
            String[] nextLine;
            boolean found = false;

            while ((nextLine = reader.readNext()) != null) {
                // CSV 파일의 첫 번째 열에서 약품명을 찾기
                if (nextLine[0].equals(query)) {
                    String name = nextLine[0];
                    String company = nextLine[1];
                    String validity = nextLine[2];
                    String storage = nextLine[3];

                    // DrugDetailActivity로 데이터 전달
                    Intent intent = new Intent(TextSearchActivity.this, DrugDetailActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("company", company);
                    intent.putExtra("validity", validity);
                    intent.putExtra("storage", storage);
                    startActivity(intent);

                    found = true;
                    break;
                }
            }

            // 약품을 찾지 못했을 때
            if (!found) {
                Toast.makeText(this, "해당 약품 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }

            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "CSV 파일을 읽는 중 오류 발생", e);
            Toast.makeText(this, "CSV 파일을 읽는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTagValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0 && nodeList.item(0) != null) {
            return nodeList.item(0).getTextContent();
        } else {
            return null;
        }
    }

    private void speakSearchButtonDescription() {
        if (switchManager.getSwitchState()) {
            String buttonText = searchButton.getContentDescription().toString();
            ttsManager.speak(buttonText);
        }
    }

    private void speakButtonDescriptionAndFinish() {
        String buttonText = backtxt.getText().toString();
        if (switchManager.getSwitchState()) {
            ttsManager.speak(buttonText);

            // 예상 발화 시간 계산 (약 100ms per character + 여유 시간)
            int estimatedSpeechTime = buttonText.length() * 100;

            // TTS 발화 후 일정 시간 후 뒤로가기 처리
            new Handler().postDelayed(() -> finishWithAnimation(), estimatedSpeechTime);
        } else {
            // TTS 비활성화 시 즉시 뒤로가기 처리
            finishWithAnimation();
        }
    }

    //Activity 종료
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishWithAnimation();
    }

    // 슬라이드 애니메이션과 함께 Activity 종료
    private void finishWithAnimation() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(homeIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);  // 슬라이드 애니메이션 적용
    }

}
