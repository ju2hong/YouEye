package com.example.youeye;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VisionAPIRequest {

    private static final String TAG = "VisionAPIRequest";
    private static final String API_KEY = "AIzaSyBz4QrGBEV_pLZ8sTYS8WbteK5thMzw8tU";  // Google Cloud API 키를 입력하세요.
    private static final String VISION_API_URL = "https://vision.googleapis.com/v1/images:annotate?key=" + API_KEY;

    // Google Cloud Vision API로 텍스트 인식 요청
    public static void requestTextRecognition(String base64Image, VisionResponseListener listener) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                String jsonRequest = createJsonRequest(base64Image);

                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonRequest);
                Request request = new Request.Builder()
                        .url(VISION_API_URL)
                        .post(body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return response.body().string();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error making Vision API request", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    listener.onResponseReceived(parseTextFromJson(result));
                } else {
                    listener.onError("API 요청 실패");
                }
            }
        }.execute();
    }

    // Google Cloud Vision API 요청을 위한 JSON 생성
    private static String createJsonRequest(String base64Image) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray requests = new JSONArray();
            JSONObject image = new JSONObject();
            JSONObject content = new JSONObject();

            content.put("content", base64Image);
            image.put("image", content);

            JSONObject feature = new JSONObject();
            feature.put("type", "TEXT_DETECTION");

            JSONObject features = new JSONObject();
            features.put("features", new JSONArray().put(feature));

            requests.put(image.put("features", features));
            jsonObject.put("requests", requests);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Vision API 응답에서 텍스트 추출
    private static String parseTextFromJson(String jsonResponse) {
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

    public interface VisionResponseListener {
        void onResponseReceived(String text);
        void onError(String error);
    }
}
