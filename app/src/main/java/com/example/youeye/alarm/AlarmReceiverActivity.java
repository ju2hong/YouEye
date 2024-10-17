package com.example.youeye.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.youeye.R;

public class AlarmReceiverActivity extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 토스트 메시지 표시
        Log.d("AlarmReceiver", "알람이 울렸습니다!");
        Toast.makeText(context, "알람 시간입니다!", Toast.LENGTH_LONG).show();

        // PlayingService 실행
        Intent serviceIntent = new Intent(context, PlayingService.class);
        serviceIntent.putExtra("action","START_ALARM");

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)

        {
            // Android 8.0 이상에서 Foreground Service 사용
            context.startForegroundService(serviceIntent);
        } else

        {
            context.startService(serviceIntent);
        }
    }
}

