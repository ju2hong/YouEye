package com.example.youeye.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhotoResultActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 100;
    private static final String API_KEY = "YOUR_API_KEY";  // 여기에 실제 API 키를 넣으세요.
    private ImageView photoImageView;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_result);

        photoImageView = findViewById(R.id.photoImageView);
        Button selectImageButton = findViewById(R.id.selectImageButton);  // 선택 버튼

        // 갤러리에서 이미지 선택
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        });
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

                // Vision API로 이미지 텍스트 인식
                if (imageBitmap != null) {
                    String base64Image = bitmapToBase64(imageBitmap);
                    requestTextRecognition(base64Image);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "이미지를 처리하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 비트맵 이미지를 Base64로 인코딩
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Google Vision API로 텍스트 인식 요청
    private void requestTextRecognition(String base64Image) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                String jsonRequest = createJsonRequest(base64Image);

                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonRequest);
                Request request = new Request.Builder()
                        .url("https://vision.googleapis.com/v1/images:annotate?key=" + API_KEY)
                        .post(body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return response.body().string();
                    } else {
                        Log.e("Vision API Request", "API 요청 실패: " + response.message());
                    }
                } catch (IOException e) {
                    Log.e("Vision API Error", "API 요청 중 오류 발생", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    String recognizedText = parseTextFromJson(result);
                    Toast.makeText(PhotoResultActivity.this, "인식된 텍스트: " + recognizedText, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PhotoResultActivity.this, "API 요청 실패", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    // Vision API 요청을 위한 JSON 생성
    private String createJsonRequest(String base64Image) {
        try {
            return "{\n" +
                    "  \"requests\": [\n" +
                    "    {\n" +
                    "      \"image\": {\n" +
                    "        \"content\": \"" + base64Image + "\"\n" +
                    "      },\n" +
                    "      \"features\": [\n" +
                    "        {\n" +
                    "          \"type\": \"TEXT_DETECTION\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Vision API 응답에서 텍스트 추출
    private String parseTextFromJson(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray responses = jsonObject.getJSONArray("responses");
            JSONObject firstResponse = responses.getJSONObject(0);
            JSONObject textAnnotation = firstResponse.getJSONArray("textAnnotations").getJSONObject(0);
            return textAnnotation.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
