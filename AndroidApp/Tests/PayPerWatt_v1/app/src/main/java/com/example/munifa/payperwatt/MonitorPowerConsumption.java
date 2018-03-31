package com.example.munifa.payperwatt;
/*
 * Created on 2018-02-21.
 * Parts of the code are borrowed from the reference below
 * Reference: https://github.com/MEnthoven/Android-HC05-App/blob/master/app/src/main/java/com/menthoven/arduinoandroid/BluetoothActivity.java
 */
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorPowerConsumption extends AppCompatActivity {
    private BluetoothDevice mDevice;
    private BluetoothService mService;
    private MyHandler mHandler;
    //TODO Toolbar toolbar; - to implement a nice error display instead of Toasts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_power_consumption);

        mDevice = getIntent().getExtras().getParcelable("BTdevice");
        mHandler = new MyHandler();
        mService = new BluetoothService(mHandler, mDevice);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mService.connect();

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
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
        }
        mService.stop();
    }
    private void display_values(String input){
        TextView textView =  findViewById(R.id.textView_current_power_cost);
        textView.setText(input);
    }

    /**
     * The handler allows you to constantly monitor the state of the connectThread and the connectedThread
     */
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case Constants.STATE_CONNECTED:
//TODO                            toolbar.setSubtitle("Connected"); the messgage below should not be a toast
                            Toast.makeText(MonitorPowerConsumption.this, "Connected", Toast.LENGTH_LONG).show();
                            //testMessageSending();
                            break;
                        case Constants.STATE_CONNECTING:
//TODO                            toolbar.setSubtitle("Connecting..."); the messgage below should not be a toast
                            Toast.makeText(MonitorPowerConsumption.this, "Connecting...", Toast.LENGTH_LONG).show();
                            break;
                        case Constants.STATE_NONE:
//TODO                            toolbar.setSubtitle("Not Connected"); the messgage below should not be a toast
                            //Toast.makeText(MonitorPowerConsumption.this, "Not Connected", Toast.LENGTH_LONG).show();
                            break;
                        case Constants.STATE_ERROR:
                            //Either the connection failed or the connection was lost
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    //This is the message that was written - in case it needs to be retrieved
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    String writeMessage = new String(writeBuf);
//                    Toast.makeText(MonitorPowerConsumption.this, "Your message has been sent." +writeMessage, Toast.LENGTH_LONG).show();
                    break;
                case Constants.MESSAGE_READ:
                    String readMessage = (String) msg.obj;
                    if (readMessage != null) {
                        display_values(readMessage);
                    }
                    break;

                case Constants.MESSAGE_ERROR:
                    String s = msg.getData().getString(Constants.CONNECTION_ERROR);
                    Toast.makeText(MonitorPowerConsumption.this, s, Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }

        }
    }

    /**
     * If anything goes wrong with the bluetooth connection, this activity ends and you return to the previous activity
     * The BroadcastReciever constantly monitors for this
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(MonitorPowerConsumption.this, "Bluetooth was turned off. Please try again.", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    default:
                        Toast.makeText(MonitorPowerConsumption.this, "Bluetooth connection lost. Please try again.", Toast.LENGTH_LONG).show();
                        finish();
                }
            }
        }
    };
    /*
     * To check if messages can be sent to the Arduino and if the Arduino recives the message
     * //TODO delete later
     */
//    private void testMessageSending(){
//        //Now testing if I can send a message
//        String write_test = "Hello World!\n";
//        mService.write(write_test.getBytes());
//        write_test = "payment_confirmed\n";
//        mService.write(write_test.getBytes());
//    }

}
