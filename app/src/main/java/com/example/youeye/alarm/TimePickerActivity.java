package com.example.youeye.alarm;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private Button okBtn, cancelBtn;
    private int hour, minute;
    private String am_pm;
    private Date currentTime;
    private String stMonth, stDay;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_timepicker);

        timePicker = (TimePicker)findViewById(R.id.time_picker);

        currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat day = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat month = new SimpleDateFormat("MM", Locale.getDefault());

        stMonth = month.format(currentTime);
        stDay = day.format(currentTime);

        ImageButton okBtn = (ImageButton) findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                }
                else{
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                }

                am_pm = AM_PM(hour);
                hour = timeSet(hour);

                Intent sendIntent = new Intent(TimePickerActivity.this, TimeActivity.class);

                sendIntent.putExtra("hour",hour);
                sendIntent.putExtra("minute",minute);
                sendIntent.putExtra("am_pm",am_pm);
                sendIntent.putExtra("month",stMonth);
                sendIntent.putExtra("day",stDay);
                setResult(RESULT_OK,sendIntent);

                finish();
            }
        });
        // 취소 버튼 누를 시 TimePickerActivity 종료
        ImageButton cancelBtn = (ImageButton)findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 24시 시간제 바꾸기
    private int timeSet(int hour){
        if(hour > 12){
            hour -= 12;
        }
        return hour;
    }

    // 오전,오후 선택
    private String AM_PM(int hour){
        if(hour >= 12){
            am_pm = "오후";
        }
        else{
            am_pm = "오전";
        }
        return am_pm;
    }
}
