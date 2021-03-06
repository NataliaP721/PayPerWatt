package com.stripe.priceselection.bluetooth_and_login;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.stripe.priceselection.R;

/**
 * This activity allows the user to view power, cost and time values sent by the Arduino.
 * It also has a stop charging button that the user can press to stop charging and process
 * the payment.
 * @author Munifa Saeed
 * @version 2.0
 * @since February 21, 2018
 *
 * Parts of the code are borrowed from the reference below
 * Reference: https://github.com/MEnthoven/Android-HC05-App/blob/master/app/src/main/java/com/menthoven/arduinoandroid/BluetoothActivity.java
 */
public class MonitorPowerConsumption extends InheritBluetoothFunctionality {

    @Override
    synchronized protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_power_consumption);

        BluetoothDevice temp = getIntent().getExtras().getParcelable("BTdevice");
        if(temp != null) {
            mDevice = temp;
            initBluetoothFunctionality();
            mService.connect();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
    /**
     * Clean up operations to perform on destroy
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("******", "MonitorPowerConsumption destroyed");
        destroyBroadCastReceiver();
        if (pDialog.isShowing()) pDialog.cancel();
        mService.stop();
    }

    /**
     * Displays the provided input string on the app activity screen
     * @param input
     */
    private void display_values(String input){
        TextView textView = findViewById(R.id.textView_current_power_cost);
        textView.setText(input);
    }

    /**
     * When a message is read from the Arduino, this method decodes the message and
     * acts accordingly.
     * @param msg message received from the Arduino
     */
    public void messageReadAction(Message msg) {
        Log.i("*************", "INSIDE MESSAGE READ ACTION IN MPC");
        String readMessage = (String) msg.obj;
        if (readMessage != null && readMessage != "") {
            if (readMessage.charAt(0) == '*') {
                //this means the user wants to stop charging and payment needs to be processed
                //Step 1: getting the cost from the Arduino
                double cost = Double.valueOf(readMessage.substring(1));
                //Step 2: send the cost to the server
                URLConnect obj = new URLConnect();
                Double max_authorized_cost = Double.parseDouble(obj.GET());
                String response = obj.POST("stop_charging\t" + Double.toString(cost));
                if (!response.equals("true")) { //ask the user to try again
                    Toast.makeText(MonitorPowerConsumption.this, "Error! Please press STOP CHARGING again.", Toast.LENGTH_LONG).show();
                } else {

                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setMessage("Charging stopped. You may disconnect now.\nPayment Summary:\nAuthorized Amount: $" + max_authorized_cost +
                            "\nCharged Amount: $" + cost + "\nRefunded Amount: $" + (max_authorized_cost-cost));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "RETURN TO MAIN PAGE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                           onBackPressed();
                        }
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            } else { //display values
                display_values(readMessage);
            }
        }
    }
    /**
     * Sends a stop charging request to the Arduino.
     * In response to the stop_charging request, the Arduino will send a payment processing request
     * which is handled in messageReadAction() method above.
     * The Arduino will also open the relay.
     * @param view
     */
    public void stopCharging(View view){
        String message = "stop_charging\n";
        mService.write(message.getBytes());
    }


    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }

}
