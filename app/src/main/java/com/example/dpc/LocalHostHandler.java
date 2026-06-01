package com.example.dpc;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

public class LocalHostHandler implements IConnectionHandler {
    private static final long CONNECTION_TIMEOUT_MS = 5000; // 5 seconds
    private WebSocketClient webSocketClient;
    private boolean isConnected = false;
    private boolean hasTimedOut = false;
    private IConnectionListener listener = null;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Handler timeoutHandler = new Handler(Looper.getMainLooper());
    LocalHostHandler(URI uri, String password, IConnectionListener listener) {
        this.listener = listener;
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                if (hasTimedOut) return;
                webSocketClient.send("AUTH " + password);
            }
            @Override
            public void onMessage(String s) {
                Log.i("Websocket", "Received: " + s);

                if (s.equals("OK")) {
                    isConnected = true;
                    Log.i("LocalHostHandler", "Opened");
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    mainHandler.post(listener::onConnected);
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                if (!isConnected) return;
                isConnected = false;
                Log.i("Websocket", "Closed " + s);
                mainHandler.post(listener::onDisconnected);
            }
            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
                timeoutHandler.removeCallbacks(timeoutRunnable);
                mainHandler.post(listener::onConnectionError);
            }
        };


        webSocketClient.connect();
        timeoutHandler.postDelayed(timeoutRunnable, CONNECTION_TIMEOUT_MS);
    }

    private final Runnable timeoutRunnable = () -> {
        if (!isConnected) {
            hasTimedOut = true;
            if (webSocketClient != null) {
                webSocketClient.close();
            }
            if (listener != null) {
                mainHandler.post(listener::onTimeout);
            }
        }
    };

    private void sendMessage(String s) {
        if (isConnected) {
            webSocketClient.send(s);
        }
    }
    @Override
    public void slideLeft() {
        sendMessage("L");
    }
    @Override
    public void slideRight() {
        sendMessage("R");
    }
    @Override
    public void button1() {
        sendMessage("1");
    }
    @Override
    public void button2() {
        sendMessage("2");
    }
    @Override
    public void button3() {
        sendMessage("3");
    }

    public void disconnect() {
        try {
            webSocketClient.close();
        } catch (Exception e) {
            Log.e("WebSocket", "Disconnect failed");
        }
    }
}
