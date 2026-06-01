package com.example.dpc;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class RemoteHostHandler implements IConnectionHandler {

    private static final long CONNECTION_TIMEOUT_MS = 5000;

    private WebSocketClient webSocketClient;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Handler timeoutHandler = new Handler(Looper.getMainLooper());

    private final IConnectionListener listener;
    private final String password;

    private boolean isConnected = false;
    private boolean hasTimedOut = false;
    private boolean sessionCreated = false;

    private String sessionId;

    public RemoteHostHandler(URI uri, String password, IConnectionListener listener) {
        this.listener = listener;
        this.password = password;

        webSocketClient = new WebSocketClient(uri) {

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                if (hasTimedOut) return;

                Log.i("RemoteHostHandler", "WebSocket opened");

                // Step 1: authenticate
                send("AUTH " + password);

                // Step 2: create session after auth
                send("CREATE");
            }

            @Override
            public void onMessage(String message) {
                Log.i("RemoteHostHandler", "Received: " + message);

                if (message.startsWith("OK")) {
                    isConnected = true;
                    timeoutHandler.removeCallbacks(timeoutRunnable);

                    mainHandler.post(listener::onConnected);
                }

                // Expected: SESSION ABC123
                else if (message.startsWith("SESSION ")) {
                    sessionId = message.substring(8).trim();
                    sessionCreated = true;

                    Log.i("RemoteHostHandler", "Session created: " + sessionId);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                isConnected = false;
                Log.i("RemoteHostHandler", "Closed: " + reason);
                mainHandler.post(listener::onDisconnected);
            }

            @Override
            public void onError(Exception ex) {
                Log.e("RemoteHostHandler", "Error: " + ex.getMessage());

                timeoutHandler.removeCallbacks(timeoutRunnable);
                mainHandler.post(listener::onConnectionError);
            }
        };

        webSocketClient.connect();
        timeoutHandler.postDelayed(timeoutRunnable, CONNECTION_TIMEOUT_MS);
    }

    private final Runnable timeoutRunnable = () -> {
        if (!isConnected && !sessionCreated) {
            hasTimedOut = true;

            try {
                webSocketClient.close();
            } catch (Exception ignored) {}

            //mainHandler.post(listener::onTimeout);
        }
    };

    private void sendMessage(String msg) {
        if (isConnected) {
            webSocketClient.send(msg);
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

    @Override
    public void disconnect() {
        try {
            webSocketClient.close();
        } catch (Exception e) {
            Log.e("RemoteHostHandler", "Disconnect failed");
        }
    }

    @Override
    public void setListener(IConnectionListener listener) {

    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isSessionReady() {
        return sessionCreated;
    }
}
