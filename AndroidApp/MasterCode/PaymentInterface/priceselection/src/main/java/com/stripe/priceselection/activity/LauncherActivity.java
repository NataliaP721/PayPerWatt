package com.stripe.priceselection.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.stripe.android.PaymentConfiguration;
import com.stripe.priceselection.R;
import com.stripe.priceselection.bluetooth_and_login.InheritBluetoothFunctionality;
import com.stripe.priceselection.bluetooth_and_login.MonitorPowerConsumption;
import com.stripe.priceselection.bluetooth_and_login.controlFlags;

public class LauncherActivity extends InheritBluetoothFunctionality {


    private static final String PUBLISHABLE_KEY =
            "add key here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        PaymentConfiguration.init(PUBLISHABLE_KEY);

        Button multilineButton = findViewById(R.id.btn_make_card_sources);
        multilineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LauncherActivity.this, PaymentMultilineActivity.class);
                //intent.putExtra()
                startActivity(intent);
            }
        });

        //Adding bluetooth functionality in background
        mDevice = getIntent().getExtras().getParcelable("BTdevice");
        initBluetoothFunctionality();
        mService.connect();

    }

    protected void messageReadAction(Message msg) {
        //do nothing here unless you want to read something from Arduino via bluetooth
        //DO NOT DELETE this method - necessary to define all unimplemented methods of an abstract class
        Log.i("*************", "INSIDE MESSAGE READ ACTION IN LAUNCHER");
//        MonitorPowerConsumption mpc = new MonitorPowerConsumption();
//        mpc.messageReadAction(msg);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("******", "Launcher Activity destroyed");
        destroyBroadCastReceiver();
        if (pDialog.isShowing()) pDialog.cancel();
        mService.stop();
        if(!controlFlags.goingToLogin && !controlFlags.connectionFailed){
           onBackPressed();
        }
    }

    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }


}
