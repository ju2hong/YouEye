package com.example.youeye.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiverActivity extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 토스트 메시지 표시
        Log.d("AlarmReceiver", "알람이 울렸습니다!");
        Toast.makeText(context, "알람 시간입니다!", Toast.LENGTH_LONG).show();
    }
}
