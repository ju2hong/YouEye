package com.example.youeye.alarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;

import java.text.SimpleDateFormat;

public class TimeActivity extends AppCompatActivity {
    private AdapterActivity arrayAdapter;
    private ImageButton tpBtn, rmBtn;
    private ListView listView;
    private TextView textView;
    private int hour, minute;
    private String month, day, am_pm;
    private Handler handler;
    private SimpleDateFormat mFormat;
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
}
