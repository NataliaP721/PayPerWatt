package com.example.munifa.payperwatt;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Munifa on 2018-03-11
 * Abstract bluetooth class from which you must inherit if your activity wants to communicate with the
 * Arduino (i.e: if you want to send info to the Arduino or recieve info from the Arduino)
 */

abstract class InheritBluetoothFunctionality extends AppCompatActivity{
    protected static BluetoothDevice mDevice;
    protected static BluetoothService mService;
    protected static MyHandler mHandler;
    protected static ProgressDialog pDialog;
    /**
     * initializes the bluetooth functionality - only call this method once in the activity
     * that starts right after the connect bluetooth activity
     */
    protected void initBluetoothFunctionality(){
        pDialog = new ProgressDialog(getBaseContext(), ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        mHandler = new MyHandler();
        mService = new BluetoothService(mHandler, mDevice);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }
    /**
     * TODO Must call this in your onDestroy method
     * Only call this method once in the onDestroy method for the activity
     * that starts right after the connect bluetooth activity
     */
    protected void destroyBroadCastReceiver(){
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
        }
    }
    /**
     * Defines what you want to do with the message received from the Ardunio.
     * You can leave the implementation of this method blank, if you don't need it
     * @param msg message received from the Arduino
     */
    protected abstract void messageReadAction(Message msg);
    /**
     * Gets the message that you recently wrote to the Arduino
     * You can override this method to get access to the written message
     */
    protected void messageWriteAction(Message msg){
        //This is the message that was written - in case it needs to be retrieved
        byte[] writeBuf = (byte[]) msg.obj;
        String writeMessage = new String(writeBuf);
        //Toast.makeText(getBaseContext(), "Your message has been sent." +writeMessage, Toast.LENGTH_LONG).show();
    }
    /*
     * The following 5 methods determine what action to take for each bluetooth state.
     * These methods can be overwritten if you want
     */
    protected void messageErrorAction(Message msg){
        String s = msg.getData().getString(Constants.CONNECTION_ERROR);
        Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
        finish();
    }
    protected void messageStateConnectedAction(){
        if (pDialog.isShowing()) pDialog.cancel();
        Toast.makeText(getBaseContext(), "Connected", Toast.LENGTH_LONG).show();
    }
    protected void messageStateConnectingAction(){
        pDialog = ProgressDialog.show(this, "Connecting...", "Connecting to " +
                mDevice.getName(), true, true);
    }
    protected void messageStateNoneAction(){
        //Toast.makeText(getBaseContext(), "Not Connected", Toast.LENGTH_LONG).show();
    }
    protected void messageStateErrorAction(){
        //Either the connection failed or the connection was lost
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
                            messageStateConnectedAction();
                            break;
                        case Constants.STATE_CONNECTING:
                            messageStateConnectingAction();
                            break;
                        case Constants.STATE_NONE:
                            messageStateNoneAction();
                            break;
                        case Constants.STATE_ERROR:
                            messageStateErrorAction();
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    messageWriteAction(msg);
                    break;
                case Constants.MESSAGE_READ:
                    Log.i("******", "Message read");
                    messageReadAction(msg);
                    break;
                case Constants.MESSAGE_ERROR:
                    messageErrorAction(msg);
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
                        Toast.makeText(getApplicationContext(), "Bluetooth was turned off. Please try again.", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Bluetooth connection lost. Please try again.", Toast.LENGTH_LONG).show();
                        finish();
                }
            }
        }
    };

}

