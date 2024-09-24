package com.example.youeye.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class PhotoResultActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView photoImageView;
    private Button searchButton;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_result);

        photoImageView = findViewById(R.id.photoImageView);
        searchButton = findViewById(R.id.searchButton);

        // 사진 촬영 인텐트 실행
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

        // 검색 버튼 클릭 시 텍스트 추출
        searchButton.setOnClickListener(v -> {
            if (imageBitmap != null) {
                extractTextFromImage(imageBitmap);
            } else {
                Toast.makeText(this, "사진이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            photoImageView.setImageBitmap(imageBitmap);
        }
    }


    // ML Kit을 사용하여 텍스트를 추출하는 메소드
    private void extractTextFromImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        // TextRecognizer 인스턴스를 옵션과 함께 초기화
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS); // 수정된 부분

        recognizer.process(image)
                .addOnSuccessListener(result -> {
                    // 성공적으로 텍스트를 인식한 경우
                    String recognizedText = result.getText();
                    if (!recognizedText.isEmpty()) {
                        // 인식된 텍스트를 처리
                        Toast.makeText(this, "추출된 텍스트: " + recognizedText, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "텍스트를 인식할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // 인식 실패 처리
                    Toast.makeText(this, "텍스트 인식 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}