package com.example.youeye;

import android.content.Context;
import android.content.SharedPreferences;

public class SwitchManager {
    private static final String PREF_NAME = "SwitchStatePrefs";
    private static final String SWITCH_STATE_KEY = "switch_state";
    private static final String INITIALIZED_KEY = "initialized";

    private static SwitchManager instance;
    private final SharedPreferences preferences;

    private SwitchManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SwitchManager getInstance(Context context) {
        if (instance == null) {
            instance = new SwitchManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean getSwitchState() {
        return preferences.getBoolean(SWITCH_STATE_KEY, false); // 기본값은 false로 설정
    }

    public boolean isInitialized() {
        return preferences.getBoolean(INITIALIZED_KEY, false); // 초기화 상태 확인
    }

    public void setSwitchState(boolean state) {
        preferences.edit().putBoolean(SWITCH_STATE_KEY, state).apply();
    }

    public void setInitialized(boolean initialized) {
        preferences.edit().putBoolean(INITIALIZED_KEY, initialized).apply();
    }

}
