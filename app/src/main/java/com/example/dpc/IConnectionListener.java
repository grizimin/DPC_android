package com.example.dpc;

public interface IConnectionListener {
    void onConnected();
    void onConnectionError();
    void onDisconnected();
    void onTimeout();
}
