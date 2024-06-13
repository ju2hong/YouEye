package com.example.youeye.home;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.youeye.R;

public class DrugDetailActivity extends AppCompatActivity {

    private TextView textViewName, textViewDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugdetail);

        textViewName = findViewById(R.id.textView12);
        textViewDetail = findViewById(R.id.textView14);

        // 인텐트로 전달된 데이터 가져오기
        String name = getIntent().getStringExtra("name");
        String company = getIntent().getStringExtra("company");
        String validity = getIntent().getStringExtra("validity");
        String storage = getIntent().getStringExtra("storage");

        // 데이터 표시
        textViewName.setText(name);
        String details = "회사: " + company + "\n유효기간: " + validity + "\n보관방법: " + storage;
        textViewDetail.setText(details);
    }
}
