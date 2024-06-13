package com.example.youeye.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TimeActivity extends AppCompatActivity {

    private AdapterActivity arrayAdapter;
    private ImageButton tpBtn, rmBtn;
    private ListView listView;
    private int hour, minute;
    private String month, day, am_pm;
    private Handler handler;
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

        handler = new Handler();  // Handler 초기화

        arrayAdapter = new AdapterActivity();

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(arrayAdapter);

        // List에 있는 항목들 눌렀을 때 시간변경 가능
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapterPosition = position;
                arrayAdapter.removeItem(position);
                Intent intent = new Intent(TimeActivity.this, TimePickerActivity.class);
                itemEditLauncher.launch(intent);
            }
        });

        class NewRunnable implements Runnable {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                }
            }
        }

        NewRunnable runnable = new NewRunnable();
        Thread thread = new Thread(runnable);
        thread.start();

        // TimePicker의 시간 셋팅값을 받기 위한 startActivityForResult() 대체
        tpBtn = (ImageButton) findViewById(R.id.adBtn);
        rmBtn = (ImageButton) findViewById(R.id.rmBtn);

        if (tpBtn == null || rmBtn == null || listView == null) {
            throw new IllegalArgumentException("필수 뷰 요소를 찾을 수 없습니다.");
        }

        tpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tpIntent = new Intent(TimeActivity.this, TimePickerActivity.class);
                timePickerLauncher.launch(tpIntent);
            }
        });

        rmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayAdapter.removeItem();
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    // 알람 매니저
    private void setAlarm(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 알람이 울릴 시간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // 인텐트를 생성하여 리시버를 호출합니다.
        Intent intent = new Intent(this, AlarmReceiverActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 알람을 설정합니다.
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}

