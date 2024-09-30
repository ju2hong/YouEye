// TimeActivity.java
package com.example.youeye.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TimePicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;

import java.util.Calendar;

public class TimeActivity extends AppCompatActivity {

    private AdapterActivity arrayAdapter;
    private ImageButton tpBtn, rmBtn;
    private ListView listView;
    private int hour, minute;
    private String month, day, am_pm;
    private int adapterPosition;

    private final ActivityResultLauncher<Intent> timePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    hour = data.getIntExtra("hour", 1);
                    minute = data.getIntExtra("minute", 2);
                    am_pm = data.getStringExtra("am_pm");
                    month = data.getStringExtra("month");
                    day = data.getStringExtra("day");

                    arrayAdapter.addItem(hour, minute, am_pm, month, day);
                    arrayAdapter.notifyDataSetChanged();

                    // 알람 토스트 설정
                    setAlarm(hour, minute);
                }
            }
    );

    private final ActivityResultLauncher<Intent> itemEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    hour = data.getIntExtra("hour", 1);
                    minute = data.getIntExtra("minute", 2);
                    am_pm = data.getStringExtra("am_pm");
                    month = data.getStringExtra("month");
                    day = data.getStringExtra("day");

                    arrayAdapter.addItem(hour, minute, am_pm, month, day);
                    arrayAdapter.notifyDataSetChanged();

                    // 알람 토스트 설정2
                    setAlarm(hour, minute);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmsettings);

        arrayAdapter = new AdapterActivity();
        listView = findViewById(R.id.list_view);
        listView.setAdapter(arrayAdapter);

        tpBtn = findViewById(R.id.adBtn);
        rmBtn = findViewById(R.id.rmBtn);

        if (tpBtn == null || rmBtn == null || listView == null) {
            throw new IllegalArgumentException("필수 뷰 요소를 찾을 수 없습니다.");
        }

        // 알람 설정 버튼 클릭 시 권한 확인
        tpBtn.setOnClickListener(v -> {
            checkExactAlarmPermission();
        });

        // 알람 삭제 버튼
        rmBtn.setOnClickListener(v -> {
            arrayAdapter.removeItem();
            arrayAdapter.notifyDataSetChanged();
        });

        // ListView 항목 클릭 시 알람 수정
        listView.setOnItemClickListener((parent, view, position, id) -> {
            adapterPosition = position;
            arrayAdapter.removeItem(position);
            Intent intent = new Intent(TimeActivity.this, TimePickerActivity.class);
            itemEditLauncher.launch(intent);
        });
    }

    // Activity Result Launcher for exact alarm permission
    private final ActivityResultLauncher<Intent> exactAlarmPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 이상
                    if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                        // 권한이 허용된 경우 시간 선택 다이얼로그 표시
                        showTimePickerDialog();
                    } else {
                        Toast.makeText(this, "정확한 알람 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 이상
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                // 사용자에게 권한 요청을 안내하는 다이얼로그 표시
                new AlertDialog.Builder(this)
                        .setTitle("정확한 알람 권한 필요")
                        .setMessage("정확한 알람을 설정하기 위해 권한을 허용해주세요.")
                        .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 사용자에게 설정 화면으로 이동하도록 안내
                                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                                exactAlarmPermissionLauncher.launch(intent);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
            } else {
                // 권한이 이미 있는 경우 시간 선택 다이얼로그 표시
                showTimePickerDialog();
            }
        } else {
            // Android 12 미만에서는 별도의 권한 없이 시간 선택 다이얼로그 표시
            showTimePickerDialog();
        }
    }

    private void showTimePickerDialog() {
        // 현재 시간을 기준으로 TimePickerDialog 초기화
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // TimePickerDialog 생성 및 표시
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    setAlarm(selectedHour, selectedMinute);
                },
                currentHour,
                currentMinute,
                true // 24시간 형식
        );
        timePickerDialog.show();
    }

    private void setAlarm(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 알람이 울릴 시간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 이미 지난 시간이라면 다음 날로 설정
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        long triggerAtMillis = calendar.getTimeInMillis();

        // 인텐트를 생성하여 리시버를 호출합니다.
        Intent intent = new Intent(this, AlarmReceiverActivity.class);

        // PendingIntent 생성 시 플래그 설정
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flags);

        // 알람을 설정합니다.
        if (alarmManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                }
                Toast.makeText(this, String.format("알람이 %02d:%02d으로 설정되었습니다.", hour, minute), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "알람 설정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "AlarmManager를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackButtonPressed(View view) {
        finish(); // 종료하고 이전 액티비티로 돌아감
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 사용자가 설정 화면에서 돌아왔을 때 권한이 부여되었는지 다시 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 이상
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                // 권한이 부여된 경우 사용자에게 알림 (선택 사항)
                Toast.makeText(this, "정확한 알람 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
