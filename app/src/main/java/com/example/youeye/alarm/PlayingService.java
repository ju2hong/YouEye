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
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.youeye.R;

public class PlayingService extends Service {
    private MediaPlayer mediaPlayer;
    private boolean isRunning;
    private Handler handler;
    private static final int ALARM_DURATION = 5000; // 알람 소리 재생 시간 (5초)
    private static final String CHANNEL_ID = "PlayingServiceChannel";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "알람 소리 재생",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("알람이 울립니다")
                    .setContentText("알람 소리가 재생 중입니다.")
                    .setSmallIcon(R.drawable.ring)
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getExtras() == null) {
            return START_NOT_STICKY;
        }

        String action = intent.getStringExtra("action");
        if ("START_ALARM".equals(action)) {
            startAlarm();
        } else if ("STOP_ALARM".equals(action)) {
            stopAlarm();
        }

        return START_NOT_STICKY;
    }

    private void startAlarm() {
        if (!isRunning) {
            mediaPlayer = MediaPlayer.create(this, R.raw.ring); // R.raw.ring에 알람 소리 파일 위치
            mediaPlayer.setLooping(false); // 반복 재생하지 않음
            mediaPlayer.start();
            isRunning = true;

            // 일정 시간 후 알람 중지
            handler.postDelayed(this::stopAlarm, ALARM_DURATION);
        }
    }

    private void stopAlarm() {
        if (isRunning) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            isRunning = false;
            stopForeground(true); // 포그라운드 서비스 중지
            stopSelf(); // 서비스 중지
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAlarm();
    }
}
