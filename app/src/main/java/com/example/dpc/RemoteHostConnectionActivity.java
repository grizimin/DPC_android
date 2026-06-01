package com.example.dpc;

import android.os.Bundle;
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
        TextInputEditText sessionInput = (TextInputEditText) findViewById(R.id.sessionInputLayout);

        nextFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String host = String.valueOf(hostInput.getText());
                String session = String.valueOf(sessionInput.getText());

                URI uri;
                try {
                    uri = new URI("ws://" + host);
                }
                catch (URISyntaxException e) {
                    Toast.makeText(RemoteHostConnectionActivity.this, "Wrong adress", Toast.LENGTH_SHORT).show();
                    Log.i("ControllerActivity", "Failed to build URI");
                    return;
                }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}