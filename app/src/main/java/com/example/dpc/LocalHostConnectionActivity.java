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

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            WebSocketService.LocalBinder b =
                    (WebSocketService.LocalBinder) binder;

            service = b.getService();
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
        TextInputLayout ipInput = (TextInputLayout) findViewById(R.id.ipInput);
        TextInputEditText linkInput = (TextInputEditText) findViewById(R.id.linkInput);
        TextInputLayout passwordInputLayout = (TextInputLayout) findViewById(R.id.passwordInputLayout);
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
                String password = String.valueOf(passwordInput.getText());

                URI uri;
                try {
                    uri = new URI("ws://" + ip);
                }
                catch (URISyntaxException e) {
                    Toast.makeText(LocalHostConnectionActivity.this, "Wrong adress", Toast.LENGTH_SHORT).show();
                    Log.i("ControllerActivity", "Failed to build URI");
                    return;
                }

                Intent service_intent = new Intent(LocalHostConnectionActivity.this, WebSocketService.class);
                service_intent.putExtra("uri", uri.toString());
                service_intent.putExtra("password", password);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(service_intent);
                }
                else {
                    startService(service_intent);
                }

                isConnecting = false;
                Intent intent = new Intent(LocalHostConnectionActivity.this, ControllerActivity.class);
                startActivity(intent);
                /*
                dpc.globalHandler = new LocalHostHandler(uri, new IConnectionListener() {
                    @Override
                    public void onConnected() {
                        isConnecting = false;
                        startActivity(intent);
                    }

                    @Override
                    public void onConnectionError() {
                        isConnecting = false;
                        Toast.makeText(LocalHostConnectionActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDisconnected() {
                        isConnecting = false;
                        Toast.makeText(LocalHostConnectionActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LocalHostConnectionActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    public void onTimeout() {
                        isConnecting = false;
                        Toast.makeText(LocalHostConnectionActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                    }
                });

                 */
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