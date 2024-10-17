package com.example.youeye.alarm;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youeye.R;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlarmReceiverActivity extends AppCompatActivity {

    private TimePicker timePicker;  // 사용자가 시간을 입력할 TimePicker
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private List<Alarm> alarmList = new ArrayList<>();  // 알람 리스트

    private OkHttpClient client = new OkHttpClient();  // OkHttp 클라이언트

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmsettings);  // 알람 설정 레이아웃

        timePicker = findViewById(R.id.time_picker);  // TimePicker

        ImageButton confirmButton = findViewById(R.id.okBtn);  // 확인 버튼

        // 확인 버튼 클릭 시, 사용자가 입력한 시간과 날짜로 알람 리스트에 추가 후 서버로 전송
        confirmButton.setOnClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            // 사용자가 선택한 날짜와 시간을 이용해 알람 객체 생성
            Alarm alarm = new Alarm("AM", hour, minute, selectedYear, selectedMonth, selectedDay);

            // 알람 리스트에 추가
            alarmList.add(alarm);

            // 서버로 알람 리스트 전송
            sendAlarmsToServer();
        });
    }
    // 서버로 알람 리스트를 POST 요청으로 보내는 메서드
    private void sendAlarmsToServer() {
        Gson gson = new Gson();
        String json = gson.toJson(alarmList);  // 알람 리스트를 JSON으로 변환

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://your-server-url.com/api/alarms")  // 서버의 API URL
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();  // 요청 실패 시 처리
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                // 서버 응답 처리
                System.out.println(response.body().string());
            }
        });
    }

    // Alarm 객체 정의
    public class Alarm {
        private String amPm;   // 오전/오후
        private int hour;      // 시
        private int minute;    // 분
        private int year;      // 년
        private int month;     // 월
        private int day;       // 일

        public Alarm(String amPm, int hour, int minute, int year, int month, int day) {
            this.amPm = amPm;
            this.hour = hour;
            this.minute = minute;
            this.year = year;
            this.month = month;
            this.day = day;
        }
    }
}
