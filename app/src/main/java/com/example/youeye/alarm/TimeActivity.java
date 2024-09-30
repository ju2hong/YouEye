// TimeActivity.java
package com.example.youeye.alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;

import java.util.ArrayList;
import java.util.Calendar;

public class TimeActivity extends AppCompatActivity {

    private AdapterActivity arrayAdapter;
    private ArrayList<Time> alarmList = new ArrayList<>(); // ArrayList로 알람 저장
    private ListView listView;
    private int hour, minute;
    private String am_pm, month, day;

    // 시간 설정을 위한 런처
    private final ActivityResultLauncher<Intent> timePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // 시간 데이터를 받아옴
                    Intent data = result.getData();
                    hour = data.getIntExtra("hour", 0);
                    minute = data.getIntExtra("minute", 0);
                    am_pm = data.getStringExtra("am_pm");
                    month = data.getStringExtra("month");
                    day = data.getStringExtra("day");

                    // 알람 객체를 ArrayList에 추가
                    Time alarmTime = new Time();
                    alarmTime.setHour(hour);
                    alarmTime.setMinute(minute);
                    alarmTime.setAm_pm(am_pm);
                    alarmTime.setMonth(month);
                    alarmTime.setDay(day);

                    alarmList.add(alarmTime);

                    // 리스트뷰 업데이트
                    arrayAdapter.addItem(hour, minute, am_pm, month, day);
                    arrayAdapter.notifyDataSetChanged();

                    // 알람 설정
                    setAlarm(hour, minute);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmsettings);

        // 어댑터 초기화
        arrayAdapter = new AdapterActivity();
        listView = findViewById(R.id.list_view);
        listView.setAdapter(arrayAdapter);

        // 시간 설정 버튼 클릭 이벤트
        ImageButton addBtn = findViewById(R.id.adBtn);
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TimeActivity.this, TimePickerActivity.class);
            timePickerLauncher.launch(intent); // 시간 설정 액티비티 호출
        });

        // 알람 삭제 버튼 클릭 이벤트
        ImageButton rmBtn = findViewById(R.id.rmBtn);
        rmBtn.setOnClickListener(v -> {
            arrayAdapter.removeItem();
            arrayAdapter.notifyDataSetChanged();
        });
    }

    // 알람 설정 메서드
    @SuppressLint("ScheduleExactAlarm")
    private void setAlarm(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                // 알람 시간이 현재 시간보다 이전이면 다음날로 설정
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            Intent intent = new Intent(this, AlarmReceiverActivity.class);

            // FLAG_IMMUTABLE 플래그 추가
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

            Toast.makeText(this, "알람이 " + hour + "시 " + minute + "분에 설정되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "알람 설정에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}

