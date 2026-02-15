package com.example.dpc;

import android.content.Intent;
import android.os.Bundle;
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
        TextView errorTextView = (TextView) findViewById(R.id.errorTextView);

        DPC dpc = (DPC) this.getApplication();
        nextFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String ip = String.valueOf(linkInput.getText());

                if (ip.isEmpty()) {
                    errorTextView.setText("Please, enter the ip");
                    return;
                }

                URI uri;
                try {
                    uri = new URI("ws://" + ip);
                }
                catch (URISyntaxException e) {
                    errorTextView.setText("IP is not valid");
                    Log.i("ControllerActivity", "Failed to build URI");
                    return;
                }
                Intent intent = new Intent(LocalHostConnectionActivity.this, ControllerActivity.class);
                dpc.globalHandler = new LocalHostHandler(uri, new IConnectionListener() {
                    @Override
                    public void onConnected() {
                        startActivity(intent);
                    }

                    @Override
                    public void onConnectionError() {
                    }

                    @Override
                    public void onDisconnected() {
                        Toast.makeText(LocalHostConnectionActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LocalHostConnectionActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
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