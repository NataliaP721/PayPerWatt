package com.example.munifa.payperwatt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controlFlags.startCharging = false;
        controlFlags.monitorConsumption = false;
    }
    /**
     * Called when the user taps the Monitor Power Consumption
     */
    public void monitorConsumption(View view) {
        Intent intent = new Intent(this, ConnectBluetooth.class);
        controlFlags.monitorConsumption = true;
        startActivity(intent);
    }
    /**
     * Called when the user taps on the Start Charging Button
     */
    public void startCharging(View view){
        Intent intent = new Intent(this, ConnectBluetooth.class);
        controlFlags.startCharging = true;
        startActivity(intent);
    }
}