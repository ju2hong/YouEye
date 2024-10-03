// TimePickerActivity.java
package com.example.youeye.alarm;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimePickerActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private String am_pm, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_timepicker); // timepicker 레이아웃 사용

        timePicker = findViewById(R.id.time_picker);

        // 현재 날짜 가져오기
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        month = monthFormat.format(currentTime);
        day = dayFormat.format(currentTime);

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

            // 결과 전달
            Intent resultIntent = new Intent();
            resultIntent.putExtra("hour", hour);
            resultIntent.putExtra("minute", minute);
            resultIntent.putExtra("am_pm", am_pm);
            resultIntent.putExtra("month", month);
            resultIntent.putExtra("day", day);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // 취소 버튼 클릭 이벤트
        ImageButton cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> finish());
    }
}
