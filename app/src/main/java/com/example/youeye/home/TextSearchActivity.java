package com.example.youeye.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;
import com.example.youeye.api.ApiClient;
import com.example.youeye.api.MedicineApiService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
    private static final String API_KEY = "sqeiVAd6RVpiBOZKO62+f7rbVqGd0E61xsA/QVijhT92Wf808uIpf9fATjE3lUUlM0Wqxh6KflfipYlWmCv8xg=="; // 여기에 실제 API 키를 넣으세요.
    private MedicineApiService apiService;

    private EditText searchEditText;
    private ImageButton searchButton;
    private TextView editTextSearch;  // TextView의 ID와 일치시킵니다.

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
        searchEditText = findViewById(R.id.editTextSearch); // EditText 초기화
        editTextSearch = findViewById(R.id.editTextSearch); // TextView 초기화

        // VoiceSearchActivity에서 전달된 텍스트 받기
        Intent intent = getIntent();
        String recognizedText = intent.getStringExtra("recognizedText");

        // 전달받은 텍스트를 TextView에 표시
        editTextSearch.setText(recognizedText);

        // 검색창 초기화
        if (getIntent().getBooleanExtra("clearSearch", false)) {
            searchEditText.setText("");  // 검색창 초기화
        }

        // 뒤로가기 버튼 클릭 이벤트 설정
        backBtn.setOnClickListener(v -> speakButtonDescriptionAndFinish());

        // 검색 버튼 클릭 이벤트 설정
        searchButton.setOnClickListener(v -> {
            speakSearchButtonDescription();
            new Handler().postDelayed(() -> {
                String query = searchEditText.getText().toString();
                searchMedicine(query);
            }, 50); // TTS 발화 후 대기 시간 (50ms)
        });

        apiService = ApiClient.getClient("http://apis.data.go.kr/1471000/SafeStadDrugService/").create(MedicineApiService.class);
    }

    private void searchMedicine(String query) {
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
                        } else {
                            // XML 파싱 및 데이터 처리
                            NodeList items = root.getElementsByTagName("item");
                            if (items.getLength() > 0 && items.item(0) != null) {
                                Element item = (Element) items.item(0);

                                String name = getTagValue(item, "PRDLST_NM");
                                String company = getTagValue(item, "BSSH_NM");
                                String validity = getTagValue(item, "VLD_PRD_YMD");
                                String storage = getTagValue(item, "STRG_MTH_CONT");
                                String imageUrl = getTagValue(item, "ITEM_IMAGE");

                                // DrugDetailActivity로 데이터 전달
                                Intent intent = new Intent(TextSearchActivity.this, DrugDetailActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("company", company);
                                intent.putExtra("validity", validity);
                                intent.putExtra("storage", storage);
                                startActivity(intent);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "XML 파싱 오류: " + e.getMessage(), e);
                        Toast.makeText(TextSearchActivity.this, "응답을 처리하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "응답이 실패했습니다.");
                    Toast.makeText(TextSearchActivity.this, "요청에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "요청 중 오류가 발생했습니다: " + t.getMessage());
                Toast.makeText(TextSearchActivity.this, "요청 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
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
            // 예상 발화 시간 계산 (대략 100ms per character + 500ms buffer)
            int estimatedSpeechTime = buttonText.length() * 100;

            new Handler().postDelayed(() -> finishWithAnimation(), estimatedSpeechTime);
        } else {
            finishWithAnimation();
        }
    }

    private void finishWithAnimation() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 상태 초기화
        searchEditText.setText(""); // 검색창 초기화
    }

    @Override
    public void onBackPressed() {
        // 현재 activity 종료
        super.onBackPressed();
        finish();
        // 홈 화면으로 이동
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);

        // 애니메이션 적용
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
