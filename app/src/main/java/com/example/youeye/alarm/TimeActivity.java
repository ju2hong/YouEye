package com.example.youeye.alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

                    // 알람 목록을 SharedPreferences에 저장
                    saveAlarmList();

                    // 알람 설정
                    setAlarm(hour, minute);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmsettings);

        // 어댑터 초기화 시 alarmList를 전달
        arrayAdapter = new AdapterActivity(alarmList);
        listView = findViewById(R.id.list_view);
        listView.setAdapter(arrayAdapter);

        // 저장된 알람 목록 불러오기
        loadAlarmList();

        // 시간 설정 버튼 클릭 이벤트
        ImageButton addBtn = findViewById(R.id.adBtn);
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TimeActivity.this, TimePickerActivity.class);
            timePickerLauncher.launch(intent); // 시간 설정 액티비티 호출
        });

        // 알람 삭제 버튼 클릭 이벤트
        ImageButton rmBtn = findViewById(R.id.rmBtn);
        rmBtn.setOnClickListener(v -> {
            if (!alarmList.isEmpty()) {
                // 마지막 아이템의 위치
                int lastIndex = alarmList.size() - 1;
                // 삭제할 알람 객체 가져오기
                Time removedAlarm = alarmList.get(lastIndex);
                int alarmId = removedAlarm.getId();

                // AlarmManager에서 알람 해제
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, AlarmReceiverActivity.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                }

                // 어댑터에서 아이템 삭제
                arrayAdapter.removeItem(lastIndex);
                // alarmList에서도 아이템 삭제
                alarmList.remove(lastIndex);
                // SharedPreferences에 변경사항 저장
                saveAlarmList();

                Toast.makeText(this, "알람이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "삭제할 알람이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 알람 목록을 SharedPreferences에 저장하는 메서드
    private void saveAlarmList() {
        SharedPreferences sharedPreferences = getSharedPreferences("AlarmPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmList); // 알람 리스트를 JSON으로 변환
        editor.putString("alarmList", json);
        editor.apply(); // 저장 실행

        // JSON 저장 확인을 위해 로그 출력
        Log.d("AlarmListSave", "Saved Alarm List JSON: " + json);
    }

    // 저장된 알람 목록을 불러오는 메서드
    private void loadAlarmList() {
        SharedPreferences sharedPreferences = getSharedPreferences("AlarmPreferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("alarmList", null);
        Type type = new TypeToken<ArrayList<Time>>() {}.getType();
        alarmList = gson.fromJson(json, type);

        // 불러온 JSON 데이터 확인 로그
        Log.d("AlarmListLoad", "Loaded Alarm List JSON: " + json);

        if (alarmList == null) {
            alarmList = new ArrayList<>();
        } else {
            // 불러온 알람 목록을 리스트뷰에 적용
            for (Time alarmTime : alarmList) {
                arrayAdapter.addItem(alarmTime.getHour(), alarmTime.getMinute(), alarmTime.getAm_pm(), alarmTime.getMonth(), alarmTime.getDay());
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }

    // 알람 설정 메서드
    @SuppressLint("ScheduleExactAlarm")
    private void setAlarm(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Android 12(API 31) 이상에서 권한이 필요한 경우 처리
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // 사용자가 직접 설정에서 권한을 부여하도록 안내
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;  // 권한을 요청한 후에는 알람 설정을 중단
            }
        }

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
