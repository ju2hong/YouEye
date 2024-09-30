package com.example.youeye;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.youeye.api.BApiClient;
import com.example.youeye.api.BApiInterface;
import com.example.youeye.api.BMedicineResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BMainActivity extends AppCompatActivity {
    private static final String API_KEY = "d5d47b81539e4a2a93bf";  // 실제 API 키로 변경
    private static final String SERVICE_ID = "I2570";             // 해당 서비스 ID 입력
    private static final String DATA_TYPE = "json";               // 필요시 xml로 변경 가능
    private static final String START_IDX = "1";
    private static final String END_IDX = "1000";                 // 필요한 만큼 조절 가능

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // API 호출
        BApiInterface apiService = BApiClient.getClient().create(BApiInterface.class);
        Call<BMedicineResponse> call = apiService.getAllMedicineData(API_KEY, SERVICE_ID, DATA_TYPE, START_IDX, END_IDX);
        call.enqueue(new Callback<BMedicineResponse>() {
            @Override
            public void onResponse(Call<BMedicineResponse> call, Response<BMedicineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BMedicineResponse.Medicine> medicines = response.body().medicines;
                    saveDataToCsv(medicines);
                } else {
                    Log.e("API_ERROR", "Failed to retrieve data");
                }
            }

            @Override
            public void onFailure(Call<BMedicineResponse> call, Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
            }
        });
    }

    // CSV 파일로 저장하는 메소드
    private void saveDataToCsv(List<BMedicineResponse.Medicine> medicines) {
        String fileName = "C:/medicine_data.csv"; // C 드라이브에 파일 저장
        try (FileWriter writer = new FileWriter(fileName)) {
            // CSV 헤더 작성
            writer.append("Barcode Number,Product Name,Product Report Number,Company Name,Last Update Date,Small Category,Medium Category,Large Category\n");

            // 데이터 작성
            for (BMedicineResponse.Medicine medicine : medicines) {
                writer.append(medicine.barcodeNumber != null ? medicine.barcodeNumber : "")
                        .append(',')
                        .append(medicine.productName != null ? medicine.productName : "")
                        .append(',')
                        .append(medicine.productReportNumber != null ? medicine.productReportNumber : "")
                        .append(',')
                        .append(medicine.companyName != null ? medicine.companyName : "")
                        .append(',')
                        .append(medicine.lastUpdateDate != null ? medicine.lastUpdateDate : "")
                        .append(',')
                        .append(medicine.productSmallCategory != null ? medicine.productSmallCategory : "")
                        .append(',')
                        .append(medicine.productMediumCategory != null ? medicine.productMediumCategory : "")
                        .append(',')
                        .append(medicine.productLargeCategory != null ? medicine.productLargeCategory : "")
                        .append('\n');
            }
            Log.d("CSV", "CSV file saved successfully at: " + fileName);
        } catch (IOException e) {
            Log.e("CSV_ERROR", "Error writing CSV file: " + e.getMessage());
        }
    }
}
