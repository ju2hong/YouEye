package com.example.youeye.alarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;
import com.example.youeye.SwitchManager;
import com.example.youeye.TTSManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimePickerActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private String am_pm, month, day;
    private ImageButton okBtn,cancelBtn;
    private TTSManager ttsManager;
    private SwitchManager switchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_timepicker);

        timePicker = findViewById(R.id.time_picker);
        setTimePickerTextColor(timePicker, Color.BLACK);

        // TTSManager 초기화
        ttsManager = new TTSManager(this);
        // SwitchManager 초기화
        switchManager = SwitchManager.getInstance(this);
        // 스위치 상태 가져오기
        boolean isSwitchOn = switchManager.getSwitchState();
        // 현재 날짜 가져오기
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        month = monthFormat.format(currentTime);
        day = dayFormat.format(currentTime);

        // SharedPreferences에서 저장된 알람 시간 불러오기
        loadAlarmData();

        // 확인 버튼 클릭 이벤트
        ImageButton okBtn = findViewById(R.id.okBtn);
        okBtn.setOnClickListener(v -> {
            int hour, minute;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hour = timePicker.getHour();
                minute = timePicker.getMinute();
            } else {
                hour = timePicker.getCurrentHour();
                minute = timePicker.getCurrentMinute();
            }

            am_pm = (hour >= 12) ? "오후" : "오전";
            if (hour > 12) hour -= 12;

            // 알람 시간과 날짜를 SharedPreferences에 저장
            saveAlarmData(hour, minute, am_pm, month, day);

            // 결과 전달
            Intent resultIntent = new Intent();
            resultIntent.putExtra("hour", hour);
            resultIntent.putExtra("minute", minute);
            resultIntent.putExtra("am_pm", am_pm);
            resultIntent.putExtra("month", month);
            resultIntent.putExtra("day", day);
            setResult(RESULT_OK, resultIntent);
            // TTS 실행: 버튼의 contentDescription 읽기
            if (switchManager.getSwitchState()) {
                CharSequence description = okBtn.getContentDescription();
                if (description != null) {
                    ttsManager.speak(description.toString());
                }
            }
            finish();  // 액티비티 종료
        });

        // 취소 버튼 클릭 이벤트
        ImageButton cancelBtn = findViewById(R.id.cancelBtn);
        // 취소 버튼 클릭 이벤트
        cancelBtn.setOnClickListener(v -> {
            // TTS 실행: 버튼의 contentDescription 읽기
            if (switchManager.getSwitchState()) {
                CharSequence description = cancelBtn.getContentDescription();
                if (description != null) {
                    ttsManager.speak(description.toString());
                }
            }
            finish();  // 액티비티 종료
        });
    }
    private void setTimePickerTextColor(TimePicker timePicker, int color) {
        for (int i = 0; i < timePicker.getChildCount(); i++) {
            View child = timePicker.getChildAt(i);
            if (child instanceof NumberPicker) {
                NumberPicker numberPicker = (NumberPicker) child;
                setNumberPickerTextColor(numberPicker, color);
            }
        }
    }

    private void setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        try {
            for (int i = 0; i < numberPicker.getChildCount(); i++) {
                View child = numberPicker.getChildAt(i);
                if (child instanceof TextView) {
                    ((TextView) child).setTextColor(color);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SharedPreferences에 알람 시간 저장
    private void saveAlarmData(int hour, int minute, String am_pm, String month, String day) {
        SharedPreferences sharedPreferences = getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("hour", hour);
        editor.putInt("minute", minute);
        editor.putString("am_pm", am_pm);
        editor.putString("month", month);
        editor.putString("day", day);
        editor.apply();  // 저장 실행
    }

    // SharedPreferences에서 알람 시간 불러오기
    private void loadAlarmData() {
        SharedPreferences sharedPreferences = getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE);
        int savedHour = sharedPreferences.getInt("hour", -1);
        int savedMinute = sharedPreferences.getInt("minute", -1);
        String savedAmPm = sharedPreferences.getString("am_pm", "");
        String savedMonth = sharedPreferences.getString("month", "");
        String savedDay = sharedPreferences.getString("day", "");

        if (savedHour != -1 && savedMinute != -1) {
            // 알람이 이미 설정되어 있을 경우, TimePicker에 설정된 값 표시
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(savedHour);
                timePicker.setMinute(savedMinute);
            } else {
                timePicker.setCurrentHour(savedHour);
                timePicker.setCurrentMinute(savedMinute);
            }
        }
    }
}