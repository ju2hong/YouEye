package com.example.youeye.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoResultActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 100;
    private ImageView photoImageView;
    private Bitmap imageBitmap;
    private TTSManager ttsManager;
    private SwitchManager switchManager;
    // 표준품목코드와 제품명을 저장할 Map
    private Map<String, String> drugDataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_result);
        // SwitchManager와 TTSManager 초기화
        switchManager = new SwitchManager(this);
        ttsManager = new TTSManager(this);
        photoImageView = findViewById(R.id.photoImageView);
        ImageButton selectImageButton = findViewById(R.id.selectImageButton);  // 이미지 선택 버튼

        // 갤러리에서 이미지 선택
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        });
    }

    // CSV 파일에서 약품 데이터 로드
    private void loadDrugDataFromCSV() {
        drugDataMap = new HashMap<>();
        InputStream inputStream = null;
        BufferedReader reader = null;

        try {
            // CSV 파일을 assets 폴더에서 불러오기
            inputStream = getAssets().open("Barcode.csv");  // 파일 경로가 assets/Barcode.csv에 있어야 함
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // CSV 파일의 각 줄을 읽어서 ,(콤마)로 분리
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // ,를 구분자로 사용하되 큰따옴표 내부의 ,는 제외
                if (tokens.length >= 2) {  // 최소 2개의 열이 있는지 확인
                    String productName = tokens[0].trim();  // 첫 번째 열: 제품명
                    String productCodes = tokens[1].trim();  // 두 번째 열: 표준품목코드

                    // 표준품목코드가 여러 개인 경우 ,로 분리
                    productCodes = productCodes.replace("\"", "");  // 큰따옴표 제거
                    String[] codes = productCodes.split(",");

                    // 각 표준품목코드에 대해 제품명 매핑
                    for (String code : codes) {
                        drugDataMap.put(code.trim(), productName);
                    }
                }
            }
            Toast.makeText(this, "CSV 파일을 성공적으로 불러왔습니다.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "CSV 파일을 읽는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        } finally {
            // 스트림을 닫아줌
            try {
                if (reader != null) reader.close();
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                // 선택된 이미지를 비트맵으로 변환
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                // 이미지뷰에 선택된 이미지 표시
                photoImageView.setImageBitmap(imageBitmap);

                // CSV 파일 로드
                loadDrugDataFromCSV();

                // 이미지 바코드 인식
                if (imageBitmap != null) {
                    recognizeBarcodeFromImage(imageBitmap);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "이미지를 처리하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 비트맵 이미지를 사용하여 바코드 인식
    private void recognizeBarcodeFromImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        // 바코드 스캐너 옵션 설정 (모든 형식의 바코드를 스캔)
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();

        // 바코드 스캐너 생성
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        // 이미지에서 바코드 인식
        scanner.process(image)
                .addOnSuccessListener(barcodes -> showBarcodeResultDialog(barcodes))
                .addOnFailureListener(e -> Toast.makeText(PhotoResultActivity.this, "바코드 인식에 실패했습니다.", Toast.LENGTH_SHORT).show());

    }

    // 바코드 인식 결과를 다이얼로그로 표시하고 "예", "아니오" 버튼 추가
    private void showBarcodeResultDialog(List<Barcode> barcodes) {
        if (barcodes.isEmpty()) {
            Toast.makeText(this, "바코드를 인식하지 못했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder recognizedBarcodeNumbers = new StringBuilder();
        final String[] recognizedProductName = {null};  // 제품명을 배열로 선언하여 사실상 final로 사용

        for (Barcode barcode : barcodes) {
            // 바코드의 RawValue를 가져와서 표준품목코드와 비교
            String barcodeValue = barcode.getRawValue();
            if (barcodeValue != null && drugDataMap.containsKey(barcodeValue)) {
                recognizedProductName[0] = drugDataMap.get(barcodeValue); // 제품명 추출
                recognizedBarcodeNumbers.append(barcodeValue).append("\n");
                break; // 하나의 바코드만 찾으면 종료
            }
        }

        if (recognizedProductName[0] != null) {
            // 다이얼로그에 인식된 바코드 번호와 제품명 표시
            View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);

            TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
            TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
            titleTextView.setText("인식된 약품 결과");
            String message = recognizedProductName[0] + "\n 맞습니까?";
            messageTextView.setText(message);

            AlertDialog dialog = builder.create();
            dialog.show();
            Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.custom_dialog_background, null);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(background);
            }

            dialogView.findViewById(R.id.yesButton).setOnClickListener(v -> {
                // Stop TTS when "Yes" is clicked
                ttsManager.stop();

                Intent intent = new Intent(PhotoResultActivity.this, TextSearchActivity.class);
                intent.putExtra("recognizedText", recognizedProductName[0]);
                startActivity(intent);
                dialog.dismiss();
            });

            dialogView.findViewById(R.id.noButton).setOnClickListener(v -> {
                // Stop TTS when "No" is clicked
                ttsManager.stop();

                photoImageView.setImageBitmap(null); // Clear the displayed image
                Toast.makeText(PhotoResultActivity.this, "새로운 이미지를 선택하세요.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            // TTS로 다이얼로그 내용 읽기 (필요한 경우)
            if (switchManager.getSwitchState()) {
                String ttsText = "인식된 제품명은 " + recognizedProductName[0] + " 입니다. 맞으면 예, 틀리면 아니오를 선택해주세요.";
                ttsManager.speak(ttsText);
            }
        } else {
            Toast.makeText(this, "인식된 바코드와 일치하는 제품명을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }
}
