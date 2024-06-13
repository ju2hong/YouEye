package com.example.youeye.home;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.youeye.R;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

public class DrugDetailActivity extends AppCompatActivity {

    private TextView textViewName, textViewDetail;
    private ImageView medicineImageView;

    // 한글 약품명을 영어로 매핑하는 테이블
    private static final Map<String, String> medicineNameMap = new HashMap<>();

    static {
        medicineNameMap.put("타이레놀", "tylenol");
        medicineNameMap.put("아스피린", "aspirin");
        medicineNameMap.put("닥터베아제정", "dakteobeajejeong");

        // 필요한 만큼 추가
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugdetail);

        textViewName = findViewById(R.id.textView12);
        textViewDetail = findViewById(R.id.textView14);
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
}
