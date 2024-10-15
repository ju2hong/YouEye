package com.example.youeye.alarm;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AlarmReceiverActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private int selectedMonth;  // 사용자 지정 월
    private int selectedDay;    // 사용자 지정 일

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmsettings);

        // 시간 선택 TimePicker
        timePicker = findViewById(R.id.time_picker);

        // 날짜 선택 버튼 클릭 시 DatePickerDialog 열기
        findViewById(R.id.datePickerBtn).setOnClickListener(v -> {
            showDatePickerDialog();
        });

        // 확인 버튼 클릭 시, 선택된 날짜와 시간으로 알람 전송
        findViewById(R.id.okBtn).setOnClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            // 선택된 날짜와 시간으로 알람 객체 생성
            Alarm alarm = new Alarm("AM", hour, minute, selectedMonth, selectedDay);
            sendAlarmToServer(alarm);  // 서버로 알람 전송
        });
    }

    // DatePickerDialog를 표시하는 메서드
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // 사용자가 선택한 날짜 저장
                    selectedMonth = month + 1;  // 월은 0부터 시작하므로 1을 더함
                    selectedDay = dayOfMonth;
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();  // DatePickerDialog 표시
    }

    // Alarm 객체 정의 (시간과 날짜를 저장)
    public class Alarm {
        private String amPm;
        private int hour;
        private int minute;
        private int month;
        private int day;

        public Alarm(String amPm, int hour, int minute, int month, int day) {
            this.amPm = amPm;
            this.hour = hour;
            this.minute = minute;
            this.month = month;
            this.day = day;
        }

        public String toJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }

    // 서버로 알람 데이터를 전송하는 메서드
    public void sendAlarmToServer(Alarm alarm) {
        String json = alarm.toJson();
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://your-server-url.com/api/alarm")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                System.out.println(response.body().string());
            }
        });
    }
}
