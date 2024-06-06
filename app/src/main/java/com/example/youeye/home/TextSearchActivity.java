package com.example.youeye.home;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;
import com.example.youeye.api.ApiClient;
import com.example.youeye.api.MedicineApiService;
import com.example.youeye.api.MedicineResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TextSearchActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private ImageButton textBtn, backBtn;
    private MedicineApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textsearch);

        editTextSearch = findViewById(R.id.editTextSearch);
        textBtn = findViewById(R.id.textBtn);
        backBtn = findViewById(R.id.backBtn);

        // Retrofit API 서비스 초기화
        apiService = ApiClient.getClient().create(MedicineApiService.class);

        textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(editTextSearch, InputMethodManager.SHOW_IMPLICIT);
                }
                String searchQuery = editTextSearch.getText().toString().trim();
                if (TextUtils.isEmpty(searchQuery)) {
                    Toast.makeText(TextSearchActivity.this, "검색할 약품 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    searchMedicine(searchQuery);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void searchMedicine(String query) {
        apiService.searchMedicine(query).enqueue(new Callback<MedicineResponse>() {
            @Override
            public void onResponse(Call<MedicineResponse> call, Response<MedicineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MedicineResponse medicineResponse = response.body();
                    if (medicineResponse.getMedicines() != null && !medicineResponse.getMedicines().isEmpty()) {
                        // 검색된 의약품 정보 처리
                        Toast.makeText(TextSearchActivity.this, "약품 정보: " + medicineResponse.getMedicines().get(0).getName(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(TextSearchActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TextSearchActivity.this, "검색 실패: 서버 오류", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MedicineResponse> call, Throwable t) {
                Toast.makeText(TextSearchActivity.this, "검색 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
