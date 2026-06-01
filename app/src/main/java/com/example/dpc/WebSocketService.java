package com.example.dpc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.net.URI;

public class WebSocketService extends Service {
    private static final String CHANNEL_ID = "WebSocketServiceChannel";
    private IConnectionHandler handler;
    private final IBinder binder = new LocalBinder();

    public void onCreate() {
        Log.i("WebSocketService", "onCreate");
        super.onCreate();
        createNotificationChannel();
        startForeground(1, createNotification("Disconnected"));
    }

    public void onDestroy() {
        Log.i("WebSocketService", "onDestroy");

        disconnect();

        stopForeground(true);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("WebSocketService", "onStartCommand");
        String uriString = intent.getStringExtra("uri");
        String passwordString = intent.getStringExtra("password");
        if (uriString != null) {
            connect();
        }
        return START_NOT_STICKY;
    }

    public void setHandler(IConnectionHandler handler) {
        this.handler = handler;
        handler.setListener(new ServiceConnectionListener());
    }

    private void connect() {
        if (handler != null) {
            updateNotification("Connected");
        }
        else {
            updateNotification("Connection failed");
        }
    }

    public class ServiceConnectionListener implements IConnectionListener {
        @Override
        public void onConnected() {
            updateNotification("Connected");
        }

        @Override
        public void onConnectionError() {
            updateNotification("Failed");
        }

        @Override
        public void onDisconnected() {
            updateNotification("Disconnected");
        }

        @Override
        public void onTimeout() {

        }
    }

    private Notification createNotification(String status) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("DPC Connecton")
                .setContentText(status)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setOngoing(true)
                .build();
    }
    private void updateNotification(String status) {
        Notification notification = createNotification(status);
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //manager.notify(1, notification);
        startForeground(1, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "WebSocket Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public class LocalBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void slideLeft() {
        Log.i("WebSocketService", "slideLeft");
        if (handler != null) {
            handler.slideLeft();
        }
    }

    public void slideRight() {
        Log.i("WebSocketService", "slideRight");
        if (handler != null) {
            handler.slideRight();
        }
    }

    public void button1() {
        if (handler != null) {
            handler.button1();
        }
    }

    public void button2() {
        if (handler != null) {
            handler.button2();
        }
    }

    public void button3() {
        if (handler != null) {
            handler.button3();
        }
    }

    public void disconnect() {
        Log.i("WebSocketService", "Disconnect");

        if (handler != null) {
            handler.disconnect();
            handler = null;
        }

        updateNotification("Disconnected");

        stopForeground(true);
        stopSelf();
    }
}
