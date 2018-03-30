package com.example.android.urlconnectiontest;

import java.net.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

@SuppressLint("NewApi")

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        URLConnect obj = new URLConnect();
        CharSequence getResponse =  obj.GET();
        Context context_g = getApplicationContext();
        int duration_g = Toast.LENGTH_LONG;
        Toast toast_g = Toast.makeText(context_g, getResponse, duration_g);
        toast_g.show();
        CharSequence postResponse =  obj.POST("token\t123458uerhgjehrgekgejsgh");
        Context context_p = getApplicationContext();
        int duration_p = Toast.LENGTH_LONG;
        Toast toast_p = Toast.makeText(context_p, postResponse, duration_p);
        toast_p.show();
    }
}
