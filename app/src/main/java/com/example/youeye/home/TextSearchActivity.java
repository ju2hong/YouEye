package com.example.youeye.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View;

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
    private static final String API_KEY = "sqeiVAd6RVpiBOZKO62%2Bf7rbVqGd0E61xsA%2FQVijhT92Wf808uIpf9fATjE3lUUlM0Wqxh6KflfipYlWmCv8xg%3D%3D"; // 실제 API 키로 변경하세요

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textsearch);

        editTextSearch = findViewById(R.id.editTextSearch);
        textBtn = findViewById(R.id.textBtn);
        backBtn = findViewById(R.id.backBtn);

        // Retrofit API 서비스 초기화
        apiService = ApiClient.getClient().create(MedicineApiService.class);

        // 검색 버튼 클릭 시 처리
        textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 키보드를 띄우기 위해 EditText에 포커스 설정
                editTextSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(editTextSearch, InputMethodManager.SHOW_IMPLICIT);
                }

                // 검색어 가져오기
                String searchQuery = editTextSearch.getText().toString().trim();
                if (TextUtils.isEmpty(searchQuery)) {
                    Toast.makeText(TextSearchActivity.this, "검색할 약품 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 의약품 검색 메서드 호출
                    searchMedicine(searchQuery);
                }
            }
        });

        // 뒤로 가기 버튼 클릭 시 처리
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 액티비티 종료
            }
        });
    }

    // 의약품 검색 메서드
    private void searchMedicine(String query) {
        apiService.searchMedicine(API_KEY, 1, 10, "json", query).enqueue(new Callback<MedicineResponse>() {
            @Override
            public void onResponse(Call<MedicineResponse> call, Response<MedicineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MedicineResponse medicineResponse = response.body();
                    if (medicineResponse.getBody().getItems() != null && !medicineResponse.getBody().getItems().isEmpty()) {
                        // 검색된 의약품 정보 처리
                        Medicine medicine = medicineResponse.getBody().getItems().get(0);

                        // DrugDetailActivity로 데이터 전달
                        Intent intent = new Intent(TextSearchActivity.this, DrugDetailActivity.class);
                        intent.putExtra("name", medicine.getName());
                        intent.putExtra("company", medicine.getCompany());
                        intent.putExtra("validity", medicine.getValidity());
                        intent.putExtra("storage", medicine.getStorage());
                        intent.putExtra("imageUrl", medicine.getImageUrl()); // 이미지 URL 전달
                        startActivity(intent);
                    } else {
                        Toast.makeText(TextSearchActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TextSearchActivity.this, "검색 실패: 서버 오류", Toast.LENGTH_SHORT).show();
                    // 응답 로그 출력
                    System.out.println("Response: " + response.raw().toString());
                    System.out.println("Response Body: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<MedicineResponse> call, Throwable t) {
                Toast.makeText(TextSearchActivity.this, "검색 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // 오류 로그 출력
                t.printStackTrace();
            }
        });
    }
    public void onBackButtonPressed(View view) {

        finish(); // 종료하고 이전 액티비티로 돌아감
    }

}
