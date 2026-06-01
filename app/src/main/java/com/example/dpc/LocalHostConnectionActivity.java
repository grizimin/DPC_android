package com.example.dpc;

import android.adservices.ondevicepersonalization.WebTriggerOutput;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.ActionBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.net.URI;
import java.net.URISyntaxException;

public class LocalHostConnectionActivity extends AppCompatActivity {

    private boolean isConnecting = false;
    private WebSocketService service;
    private URI uri;
    private String password;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            WebSocketService.LocalBinder b =
                    (WebSocketService.LocalBinder) binder;

            service = b.getService();
            service.setHandler(new LocalHostHandler(uri, password));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_local_host_connection);

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton nextFab = (FloatingActionButton) findViewById(R.id.nextFab);
        TextInputEditText linkInput = (TextInputEditText) findViewById(R.id.linkInput);
        TextInputEditText passwordInput = (TextInputEditText) findViewById(R.id.passwordInput);

        nextFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (isConnecting) {
                    Toast.makeText(LocalHostConnectionActivity.this, "Connection is in progress", Toast.LENGTH_SHORT).show();
                    return;
                }
                isConnecting = true;
                String ip = String.valueOf(linkInput.getText());
                password = String.valueOf(passwordInput.getText());

                try {
                    uri = new URI("ws://" + ip);
                }
                catch (URISyntaxException e) {
                    Toast.makeText(LocalHostConnectionActivity.this, "Wrong adress", Toast.LENGTH_SHORT).show();
                    Log.i("ControllerActivity", "Failed to build URI");
                    return;
                }

                Intent service_intent = new Intent(LocalHostConnectionActivity.this, WebSocketService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(service_intent);
                }
                else {
                    startService(service_intent);
                }

                bindService(service_intent, connection, BIND_AUTO_CREATE);

                isConnecting = false;
                Intent intent = new Intent(LocalHostConnectionActivity.this, ControllerActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

}