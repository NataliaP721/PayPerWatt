package com.example.munifa.payperwatt;

import java.util.UUID;
/**
 * Modified on 2018-02-20.
 * Created by da Ent on 1-11-2015.
 * Reference: https://github.com/MEnthoven/Android-HC05-App/blob/master/app/src/main/java/com/menthoven/arduinoandroid/Constants.java
 */

public interface Constants {
    int REQUEST_ENABLE_BT = 1;
    UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    // message types sent from the BluetoothService Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_ERROR = 4;

    // Constants that indicate the current connection state
    int STATE_NONE = 0;       // we're doing nothing
    int STATE_ERROR = 1;
    int STATE_CONNECTING = 2; // now initiating an outgoing connection
    int STATE_CONNECTED = 3;  // now connected to a remote device

    String TAG = "Arduino - Android";
    String CONNECTION_ERROR = "error";
}
