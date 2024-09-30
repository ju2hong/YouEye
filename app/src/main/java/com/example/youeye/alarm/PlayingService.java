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

    // 알람을 중지할 시간 (밀리초 단위, 예: 5000ms = 4초)
    private static final int ALARM_DURATION = 4000;

    // Notification Channel ID
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

        // Notification Channel 설정 (Android Oreo 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Playing Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("채널 설명: 알람 소리 재생 서비스");

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
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
                break;
        }

        return START_NOT_STICKY;
    }

    private void startAlarm() {
        if (!isRunning) {
            // Notification 빌드
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("알람 시작")
                    .setContentText("알람음이 재생됩니다.")
                    .setSmallIcon(R.drawable.ring)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOngoing(true);

            Notification notification = builder.build();

            // 포그라운드 서비스로 시작
            startForeground(1, notification);

            mediaPlayer = MediaPlayer.create(this, R.raw.ring);
            mediaPlayer.setLooping(false); // 반복 재생 비활성화
            mediaPlayer.start();
            isRunning = true;
            Log.d("PlayingService", "알람 시작됨");

            // 알람 중지
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopAlarm();
                }
            }, ALARM_DURATION);
        }
    }

    private void stopAlarm() {
        if (isRunning && mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            isRunning = false;
            Log.d("PlayingService", "알람 정지됨");

            // 포그라운드 서비스 중지 및 서비스 종료
            stopForeground(true);
            stopSelf();
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
