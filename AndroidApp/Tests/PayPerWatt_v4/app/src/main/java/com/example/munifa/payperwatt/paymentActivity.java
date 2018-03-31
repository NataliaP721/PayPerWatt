package com.example.munifa.payperwatt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class paymentActivity extends AppCompatActivity {
    // TODO model this activity based on the MonitorPowerConsumptionActivity
    // You will need the Handler, BroadCastReciver and pretty much everything else except for the display_values part
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toast.makeText(paymentActivity.this, "Nothing to do yet", Toast.LENGTH_LONG).show();

    }
}
