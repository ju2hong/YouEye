package com.example.youeye.alram;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;

import java.text.SimpleDateFormat;

public class TimeActivity extends AppCompatActivity {
    public static final int REQUEST_CODE1 = 1000;
    public static final int REQUEST_CODE2 = 1001;
    private AdapterActivity arrayAdapter;
    private Button tpBtn, rmBtn;
    private ListView listView;
    private TextView textView;
    private int hour, minute;
    private String month, day, am_pm;
    private Handler handler;
    private SimpleDateFormat mFormat;
    private int adapterPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmsettings);

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
                startActivityForResult(intent, REQUEST_CODE2);
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

        // TimePicker의 시간 셋팅값을 받기 위한 startActivityForResult()
        ImageButton tpBtn = (ImageButton) findViewById(R.id.adBtn);
        tpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tpIntent = new Intent(TimeActivity.this, TimePickerActivity.class);
                startActivityForResult(tpIntent, REQUEST_CODE1);
            }
        });

        ImageButton rmBtn = (ImageButton) findViewById(R.id.rmBtn);
        rmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayAdapter.removeItem();
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }


    // TimePicker 셋팅값 받아온 결과를 arrayAdapter에 추가
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 시간 리스트 추가
        if (requestCode == REQUEST_CODE1 && resultCode == RESULT_OK && data != null) {
            hour = data.getIntExtra("hour", 1);
            minute = data.getIntExtra("minute", 2);
            am_pm = data.getStringExtra("am_pm");
            month = data.getStringExtra("month");
            day = data.getStringExtra("day");

            arrayAdapter.addItem(hour, minute, am_pm, month, day);
            arrayAdapter.notifyDataSetChanged();
        }

        //시간 리스트 터치 시 변경된 시간값 저장
        if (requestCode == REQUEST_CODE2 && resultCode == RESULT_OK && data != null) {
            hour = data.getIntExtra("hour", 1);
            minute = data.getIntExtra("minute", 2);
            am_pm = data.getStringExtra("am_pm");
            month = data.getStringExtra("month");
            day = data.getStringExtra("day");

            arrayAdapter.addItem(hour, minute, am_pm, month, day);
            arrayAdapter.notifyDataSetChanged();
        }
    }
}

