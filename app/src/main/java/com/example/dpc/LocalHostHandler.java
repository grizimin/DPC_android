package com.example.dpc;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

public class LocalHostHandler implements IConnectionHandler {
    private final WebSocketClient webSocketClient;
    private boolean isConnected = false;
    LocalHostHandler(URI uri) {
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                isConnected = true;
                Log.i("LocalHostHandler", "Opened");
                webSocketClient.send("Hello!");
            }
            @Override
            public void onMessage(String s) {
                Log.i("Websocket", "Received: " + s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                isConnected = false;
                Log.i("Websocket", "Closed " + s);
            }
            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        webSocketClient.connect();
    }
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
}
