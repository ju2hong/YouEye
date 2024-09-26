// PlayingService.java
package com.example.youeye.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.youeye.R;

public class PlayingService extends Service {
    private MediaPlayer mediaPlayer;
    private boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "PlayingServiceChannel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Playing Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("알람시작")
                    .setContentText("알람음이 재생됩니다.")
                    .build();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getExtras() == null) {
            return START_NOT_STICKY;
        }

        String action = intent.getExtras().getString("action", "");

        switch (action) {
            case "START_ALARM":
                startAlarm();
                break;
            case "STOP_ALARM":
                stopAlarm();
                break;
            default:
                // 알 수 없는 액션
                break;
        }

        return START_NOT_STICKY;
    }

    private void startAlarm() {
        if (!isRunning) {
            mediaPlayer = MediaPlayer.create(this, R.raw.ring);
            mediaPlayer.setLooping(true); // 반복 재생 설정
            mediaPlayer.start();
            isRunning = true;
            Log.d("PlayingService", "알람 시작됨");
        }
    }

    private void stopAlarm() {
        if (isRunning) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            isRunning = false;
            Log.d("PlayingService", "알람 정지됨");
            stopSelf(); // 서비스 종료
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRunning) {
            stopAlarm();
        }
        Log.d("PlayingService", "서비스 파괴됨");
    }
}
