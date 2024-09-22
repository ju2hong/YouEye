package com.example.youeye.home;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.youeye.R;
import com.bumptech.glide.Glide;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DrugDetailActivity extends AppCompatActivity {

    private TextView textViewName, textViewDetail, textView15;
    private ImageView medicineImageView;
    private static final String TAG = "DrugDetailActivity";

    // 한글 약품명을 영어로 매핑하는 테이블
    private static final Map<String, String> medicineNameMap = new HashMap<>();

    static {
        medicineNameMap.put("닥터베아제정", "dakteobeajejeong");
        medicineNameMap.put("훼스탈골드정", "hweseutalgoldeujeong");
        medicineNameMap.put("판피린티정", "panpirintijeong");
        // 필요한 만큼 추가
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugdetail);

        textViewName = findViewById(R.id.textView12);
        textViewDetail = findViewById(R.id.textView14);
        textView15 = findViewById(R.id.textView15);
        medicineImageView = findViewById(R.id.medicineImageView);

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

        // 매핑된 이름 가져오기
        String englishName = medicineNameMap.get(name);
        if (englishName != null) {
            // 이미지 로드
            int imageResource = getResources().getIdentifier(englishName, "drawable", getPackageName());
            if (imageResource != 0) {
                Glide.with(this)
                        .load(imageResource)
                        .placeholder(R.drawable.placeholder) // 로딩 중에 보여줄 이미지
                        .error(R.drawable.error) // 로딩 실패 시 보여줄 이미지
                        .into(medicineImageView);
            } else {
                Glide.with(this)
                        .load(R.drawable.error) // 이미지가 없으면 에러 이미지를 표시
                        .into(medicineImageView);
            }
        } else {
            Glide.with(this)
                    .load(R.drawable.error) // 매핑된 이름이 없으면 에러 이미지를 표시
                    .into(medicineImageView);
        }
    }

    private void displayDrugDetailsFromCSV(String drugName) {
        try {
            // assets 폴더에서 detaildrug.csv 파일 열기
            InputStream is = getAssets().open("detaildrug.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is, "UTF-8"));
            String[] nextLine;
            boolean found = false;

            // CSV 파일에서 한 줄씩 읽기
            while ((nextLine = reader.readNext()) != null) {
                // CSV 파일의 첫 번째 열에서 약품명을 찾기
                if (nextLine[0].equals(drugName)) {
                    // 효능, 복용법, 주의사항, 다중복용금지, 부작용을 가져와서 textView15에 표시
                    String efficacy = nextLine[1];    // 효능
                    String usage = nextLine[2];       // 복용법
                    String caution = nextLine[3];     // 주의사항
                    String multipleContra = nextLine[4]; // 다중복용금지
                    String sideEffects = nextLine[5]; // 부작용

                    String drugDetails = "효능: " + efficacy + "\n" +
                            "복용법: " + usage + "\n" +
                            "주의사항: " + caution + "\n" +
                            "다중복용금지: " + multipleContra + "\n" +
                            "부작용: " + sideEffects;

                    // textView15에 정보 출력
                    textView15.setText(drugDetails);
                    found = true;
                    break;
                }
            }

            // 약품을 찾지 못했을 때
            if (!found) {
                textView15.setText("해당 약품 정보를 찾을 수 없습니다.");
            }

            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "CSV 파일을 읽는 중 오류 발생", e);
            textView15.setText("약품 정보를 불러오지 못했습니다.");
        }
    }
}
