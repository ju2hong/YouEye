package com.example.youeye;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class TTSManager {
    private TextToSpeech tts;
    private boolean isInitialized = false;
    private boolean isTTSOn = true; // TTS 기본 상태: 활성화

    public TTSManager(Context context) {
        tts = new TextToSpeech(context.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.KOREAN); // 한국어 설정
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    isInitialized = false;
                } else {
                    isInitialized = true;
                }
            }
        });
    }

    // TTS 상태 설정
    public void setTTSOn(boolean ttsOn) {
        isTTSOn = ttsOn;
    }

    // TTS 상태 가져오기
    public boolean isTTSOn() {
        return isTTSOn;
    }

    // 음성 출력
    public void speak(String text) {
        if (isInitialized && tts != null && isTTSOn) {
            int result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            if (result == TextToSpeech.ERROR) {
                Log.e("TTSManager", "음성 출력에 오류가 발생했습니다.");
            }
        } else if (!isInitialized) {
            // 초기화 실패 시 로그 또는 예외 처리
            Log.e("TTSManager", "TTS 초기화에 실패했습니다.");
        }
    }


    // TextView의 텍스트를 음성으로 출력
    public void speakTextViewText(TextView textView) {
        String text = textView.getText().toString();
        speak(text);
    }

    // ImageButton의 contentDescription을 음성으로 출력
    public void speakButtonDescription(ImageButton button) {
        String buttonText = button.getContentDescription().toString();
        speak(buttonText);
    }

    // 음성 정지
    public void stop() {
        if (tts != null) {
            tts.stop();
        }
    }

    // TTS 종료
    public void shutdown() {
        if (tts != null) {
            tts.shutdown();
        }
    }

}
