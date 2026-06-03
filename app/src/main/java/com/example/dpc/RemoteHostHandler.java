package com.example.dpc;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class RemoteHostHandler implements IConnectionHandler {
    private static final long CONNECTION_TIMEOUT_MS = 5000;

    private final WebSocketClient webSocketClient;

    private boolean isConnected = false;
    private boolean hasTimedOut = false;

    private IConnectionListener listener = null;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Handler timeoutHandler = new Handler(Looper.getMainLooper());
    private final Runnable timeoutRunnable;
    public RemoteHostHandler(URI uri, String sessionId) {
        webSocketClient = new WebSocketClient(uri) {

            @Override
            public void onOpen(ServerHandshake handshake) {
                if (hasTimedOut) {
                    return;
                }

                webSocketClient.send("JOIN " + sessionId);
            }

            @Override
            public void onMessage(String message) {
                Log.i("RemoteHostHandler", "Received: " + message);

                if ("OK".equals(message)) {
                    isConnected = true;

                    timeoutHandler.removeCallbacks(timeoutRunnable);

                    if (listener != null) {
                        mainHandler.post(listener::onConnected);
                    }
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (!isConnected) {
                    return;
                }

                isConnected = false;

                Log.i("RemoteHostHandler", "Closed: " + reason);

                if (listener != null) {
                    mainHandler.post(listener::onDisconnected);
                }
            }

            @Override
            public void onError(Exception ex) {
                Log.e("RemoteHostHandler", "Error", ex);

                timeoutHandler.removeCallbacks(timeoutRunnable);

                if (listener != null) {
                    mainHandler.post(listener::onConnectionError);
                }
            }
        };

        webSocketClient.connect();
        timeoutRunnable = () -> {
            if (!isConnected) {
                hasTimedOut = true;

                try {
                    webSocketClient.close();
                } catch (Exception ignored) {
                }

                if (listener != null) {
                    mainHandler.post(listener::onTimeout);
                }
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, CONNECTION_TIMEOUT_MS);
    }

    public void setListener(IConnectionListener listener) {
        this.listener = listener;
    }

    private void sendMessage(String message) {
        if (isConnected) {
            webSocketClient.send(message);
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
            Log.e("RemoteHostHandler", "Disconnect failed", e);
        }
    }
}