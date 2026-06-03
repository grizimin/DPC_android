package com.example.dpc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.net.URI;
import java.net.URISyntaxException;

public class RemoteHostConnectionActivity extends AppCompatActivity {

    private URI uri;
    private String session;
    private WebSocketService service;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            WebSocketService.LocalBinder b =
                    (WebSocketService.LocalBinder) binder;

            service = b.getService();
            service.setHandler(new RemoteHostHandler(uri, session));
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
        setContentView(R.layout.activity_remote_host_connection);

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton nextFab = (FloatingActionButton) findViewById(R.id.nextFab);
        TextInputEditText hostInput = (TextInputEditText) findViewById(R.id.hostInput);
        TextInputEditText sessionInput = (TextInputEditText) findViewById(R.id.sessionInput);

        nextFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String host = String.valueOf(hostInput.getText());
                session = String.valueOf(sessionInput.getText());

                try {
                    uri = new URI("ws://" + host);
                }
                catch (URISyntaxException e) {
                    Toast.makeText(RemoteHostConnectionActivity.this, "Wrong adress", Toast.LENGTH_SHORT).show();
                    Log.i("ControllerActivity", "Failed to build URI");
                    return;
                }


                Intent service_intent = new Intent(RemoteHostConnectionActivity.this, WebSocketService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(service_intent);
                }
                else {
                    startService(service_intent);
                }

                bindService(service_intent, connection, BIND_AUTO_CREATE);

                Intent intent = new Intent(RemoteHostConnectionActivity.this, ControllerActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}