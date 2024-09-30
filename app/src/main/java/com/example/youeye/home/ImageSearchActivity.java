package com.example.youeye.home;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageSearchActivity extends AppCompatActivity {

    private TTSManager ttsManager;
    private SwitchManager switchManager;
    private static final int CAMERA_REQUEST_CODE = 101; // 카메라 권한 요청 코드
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private ImageButton imageButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagesearch);

        // TTSManager 초기화
        ttsManager = new TTSManager(this);
        switchManager = SwitchManager.getInstance(this);
        previewView = findViewById(R.id.previewView);


        imageButton4 = findViewById(R.id.imageButton4);
        imageButton4.setOnClickListener(v -> speakButtonDescriptionAndFinish());

        // 카메라 권한을 확인하고 요청하는 함수
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            startCamera(); // 권한이 이미 있으면 카메라 시작
        }

        // 사진 촬영을 위한 Executor
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void speakButtonDescriptionAndFinish() {
        String buttonText = imageButton4.getContentDescription().toString();
        if (switchManager.getSwitchState()) {
            ttsManager.speak(buttonText);

            // 예상 발화 시간 계산 (대략 100ms per character + 500ms buffer)
            int estimatedSpeechTime = buttonText.length() * 100 ;

            new Handler().postDelayed(this::finishWithAnimation, estimatedSpeechTime);
        } else {
            finishWithAnimation();
        }
    }
    private void finishWithAnimation() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        speakButtonDescriptionAndFinish();
    }

    // 권한 요청에 대한 응답 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera(); // 권한이 부여되면 카메라 실행
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview 설정
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // ImageCapture 설정
                imageCapture = new ImageCapture.Builder().build();

                // 후면 카메라 선택
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // 바인딩 해제 후 다시 바인딩
                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", "카메라 시작 실패", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // 사진 촬영 함수
    public void takePhoto(View view) {
        if (imageCapture == null) return;

        // 사진 파일을 저장할 경로 설정
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES); // 갤러리 경로 설정

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                .build();

        // 사진 촬영
        imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = outputFileResults.getSavedUri();

                runOnUiThread(() -> {
                    Toast.makeText(ImageSearchActivity.this, "사진 촬영 성공! 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show();

                    // PhotoResultActivity로 전환하며 사진 Uri 전달
                    Intent intent = new Intent(ImageSearchActivity.this, PhotoResultActivity.class);
                    intent.putExtra("photoUri", savedUri);
                    startActivity(intent);
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("CameraX", "사진 촬영 실패: " + exception.getMessage(), exception);
            }
        });
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown(); // Executor 종료
    }
}
