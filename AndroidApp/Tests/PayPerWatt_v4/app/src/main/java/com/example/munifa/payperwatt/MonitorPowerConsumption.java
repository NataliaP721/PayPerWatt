package com.example.munifa.payperwatt;
/*
 * Created on 2018-02-21.
 * Parts of the code are borrowed from the reference below
 * Reference: https://github.com/MEnthoven/Android-HC05-App/blob/master/app/src/main/java/com/menthoven/arduinoandroid/BluetoothActivity.java
 */

import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorPowerConsumption extends InheritBluetoothFunctionality {

    @Override
    synchronized protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_power_consumption);

        mDevice = getIntent().getExtras().getParcelable("BTdevice");
        initBluetoothFunctionality();
        mService.connect();
    }

    //TODO for testing only - delete later the entire function
    public void send20(View view){
        String write_test = "$20.00\n";
        mService.write(write_test.getBytes());
        //write_test = "payment_confirmed\n";
        //mService.write(write_test.getBytes());
        Toast.makeText(MonitorPowerConsumption.this,"Sending "+ write_test,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("******", "MonitorPowerConsumption destroyed");
        destroyBroadCastReceiver();
        if (pDialog.isShowing()) pDialog.cancel();
        mService.stop();
    }
    private void display_values(String input){
        TextView textView =  findViewById(R.id.textView_current_power_cost);
        textView.setText(input);
    }
    protected void messageReadAction(Message msg) {
        String readMessage = (String) msg.obj;
        if(readMessage != null) {
            if (readMessage.charAt(0) == '*') {
                //this means the user wants to stop charging and payment needs to be processed
                double cost = Double.valueOf(readMessage.substring(1));
                Toast.makeText(MonitorPowerConsumption.this, String.valueOf(cost), Toast.LENGTH_LONG).show();

                //TODO call payment process method here - the user will be charged the cost value above
                //TODO then show a pDialog if you want
                //TODO then call finish()

            } else { //display values
                display_values(readMessage);



            }
        }
    }

    public void stopCharging(View view){
        String message = "stop_charging\n";
        mService.write(message.getBytes());
        //in response to the stop_charging request, the Arduino will send a payment processing request
        //which is handled in messageReadAction() method above
        //the Arduino will also open the relay
    }


    /*
     * To check if messages can be sent to the Arduino and if the Arduino recives the message
     * //TODO delete later
     */
   // private void testMessageSending(String write_test){
        //Now testing if I can send a message
    //    mService.write(write_test.getBytes());
       // String write_test = "$20.00\n";
//        write_test = "payment_confirmed\n";
//        mService.write(write_test.getBytes());
    //}

}
