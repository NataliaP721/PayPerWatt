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

/**
 * This activity represents the intial activity in the payment process after connecting to Bluetooth, it displays average full charging prices and
 * contains a button that leads to an activity to enter card information to create a token.
 * @author Aysha Panatch
 * @since March 26, 2018
 * References: https://github.com/stripe/stripe-payments-demo
 */
public class LauncherActivity extends InheritBluetoothFunctionality {


    /**
     * Field String PUBLISHABLE_KEY is our personal testing key, set to default for this public code.
     */
    private static final String PUBLISHABLE_KEY =
            "put your key here";

    /**
     * The LauncherActivity is initially setup in this method, creating a button with a listener that leads to the enter card information page.
     * @param savedInstanceState
     */
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

    /**
     * Bluetooth communication method inherited as a method of an abstract class, not used in the acitvity.
     * @param msg
     */
    protected void messageReadAction(Message msg) {
        //do nothing here unless you want to read something from Arduino via bluetooth
        //DO NOT DELETE this method - necessary to define all unimplemented methods of an abstract class
        Log.i("*************", "INSIDE MESSAGE READ ACTION IN LAUNCHER");
//        MonitorPowerConsumption mpc = new MonitorPowerConsumption();
//        mpc.messageReadAction(msg);
    }

    /**
     * This deinitialises the bluetooth/application communication when the activity is destroyed for precautionary measures.
     */
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

    /**
     * This method allows the phyical back button on the Android device to navigate to where the back button on the application would lead.
     */
    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }


}
