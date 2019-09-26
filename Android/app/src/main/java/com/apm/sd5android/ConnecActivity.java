package com.apm.sd5android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class ConnecActivity extends AppCompatActivity {

    private static final int port = 333;

    private Button connect;
    private EditText ipText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        connect = findViewById(R.id.button);
        ipText = findViewById(R.id.editText);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ip = ipText.getText().toString();

                try {
                    Connection.connect(ip, port);

                    Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ConnecActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
