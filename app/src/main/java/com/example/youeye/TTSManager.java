package com.example.youeye;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class TTSManager {
    private TextToSpeech tts;
    private boolean isInitialized = false;
    private int isTTSOn = 1; // 스위치가 켜진 상태를 1로 나타냄

    public TTSManager(Context context) {
        tts = new TextToSpeech(context.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.KOREAN); // 한국어 설정
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 언어 데이터가 없거나 지원되지 않는 경우
                    isInitialized = false;
                } else {
                    isInitialized = true;
                }
            }
        });
    }

    // 음성 출력 여부 설정
    public void setTTSOn(boolean ttsOn) {
        isTTSOn = ttsOn ? 1 : 0; // 스위치 상태에 따라 1 또는 0으로 설정
    }

    // 음성 출력 여부 가져오기
    public int getTTSOn() {
        return isTTSOn;
    }

    // 음성 출력
    public void speak(String text) {
        if (isInitialized && tts != null && isTTSOn == 1) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
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