package com.example.dpc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.ActionBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.dpc.databinding.ActivityControllerBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        FloatingActionButton nextFab = (FloatingActionButton) findViewById(R.id.nextFab);
        RadioGroup group = (RadioGroup) findViewById(R.id.selectionGroup);
        nextFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int id = group.getCheckedRadioButtonId();
                Intent intent = null;
                if (id == 1) {
                    // TODO: Realize remote host option
                }
                else if (id == 2) {
                    intent = new Intent(MainActivity.this, LocalHostConnectionActivity.class);
                }
                if (intent != null) {
                    startActivity(intent);
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