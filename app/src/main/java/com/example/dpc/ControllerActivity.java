package com.example.dpc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;

import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.StringNavType;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.dpc.databinding.ActivityControllerBinding;

import java.net.URI;
import java.net.URISyntaxException;

public class ControllerActivity extends AppCompatActivity {

    private WebSocketService service;
    private boolean isBound = false;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            WebSocketService.LocalBinder b =
                    (WebSocketService.LocalBinder) binder;

            service = b.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controller);

        //DPC dpc = (DPC) this.getApplication();
        Intent intent = new Intent(this, WebSocketService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Log.d("ControllerActivity", "Back button pressed");

                        if (service != null) {
                            service.disconnect();
                        }

                        if (isBound) {
                            unbindService(connection);
                            isBound = false;
                        }

                        stopService(intent);

                        finish();
                    }
                });

        Button buttonLeft = (Button) findViewById(R.id.buttonLeft);
        buttonLeft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //dpc.globalHandler.slideLeft();
                if (service != null) {
                    service.slideLeft();
                }
                else {
                    Log.e("ControllerActivity", "Service is NULL");
                }
            }
        });
        Button buttonRight = (Button) findViewById(R.id.buttonRight);
        buttonRight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //dpc.globalHandler.slideRight();
                if (service != null) {
                    service.slideRight();
                }
                else {
                    Log.e("ControllerActivity", "Service is NULL");
                }
            }
        });
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //dpc.globalHandler.button1();
                if (service != null) {
                    service.button1();
                }
                else {
                    Log.e("ControllerActivity", "Service is NULL");
                }
            }
        });
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //dpc.globalHandler.button2();
                if (service != null) {
                    service.button2();
                }
                else {
                    Log.e("ControllerActivity", "Service is NULL");
                }
            }
        });
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //dpc.globalHandler.button3();
                if (service != null) {
                    service.button3();
                }
                else {
                    Log.e("ControllerActivity", "Service is NULL");
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}