package com.example.munifa.payperwatt;
//TODO remove toolbar
/*
 * Created by Munifa on 2018-02-20
 * References: https://developer.android.com/guide/topics/connectivity/bluetooth.html
 */

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import static com.example.munifa.payperwatt.Constants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.munifa.payperwatt.Constants.REQUEST_ENABLE_BT;

public class ConnectBluetooth extends AppCompatActivity {
    Button toolBarBtn;
    private ListView listView1;
    public ArrayAdapter<String> listView1_adapter;
    private boolean recieverRegistered = false;
    private ArrayList<BluetoothDevice> mBT_DeviceList = new ArrayList<>();
    private ArrayList<String> mDeviceList = new ArrayList<>();
    BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice lastClickedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_bluetooth);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Your device doesn't support Bluetooth
            // Display error message and return to main activity
            Toast.makeText(ConnectBluetooth.this, "Your device does not support Bluetooth.", Toast.LENGTH_LONG).show();
            finish();
        }

        if (!mBluetoothAdapter.isEnabled()) {
           enableBluetooth();
        } else if (mBluetoothAdapter.isEnabled()) {
            startQuerying();
        }
        scanAgainButton();
    }
    /**
     * starts an intent to enable bluetooth
     */
    private void enableBluetooth(){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }
    /**
     * Called when the activity returns from a request to enable bluetooth
     * If the user selected for the bluetooth to be turned on, a message is displayed and we start looking for a list of available devices
     * Otherwise the activity finishes with an error message
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                //The user agreed to turn on bluetooth and bluetooth was successfully turned on
                Toast.makeText(ConnectBluetooth.this, "Bluetooth turned on", Toast.LENGTH_LONG).show();
                refreshDeviceList();
            } else {
                //The user refused to turn on bluetooth-or some error occurred
                //Displays error message and returns to main activity
                Toast.makeText(ConnectBluetooth.this, "Bluetooth is off. Please allow bluetooth to turn on.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override public void onRequestPermissionsResult(int requestCode, String [] permissions, int [] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    String s = "Location access denied. Unable to scan for new devices.";
                    Toast.makeText(ConnectBluetooth.this, s, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    /**
     * Creates the scanAgain button and defines what should happen when it is clicked
     */
    private void scanAgainButton(){
        toolBarBtn = findViewById(R.id.toolbarbtn);
        toolBarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshDeviceList();
            }
        });
    }
    /**
     * Refreshes the device list by scanning again
     */
    private void refreshDeviceList(){
        if(!mBT_DeviceList.isEmpty())
            mBT_DeviceList.clear();
        if(!mDeviceList.isEmpty()) {
            mDeviceList.clear();
            listView1_adapter.notifyDataSetChanged();
        }
        startQuerying();
    }
    /**
     * Looks for paired bluetooth devices and puts them in the list
     */
    private void startQuerying(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                mBT_DeviceList.add(device);
                mDeviceList.add(device.getName() + "\n" + device.getAddress() + "\n" + "Status: Paired");
                Log.i("BT", device.getName() + "\n" + device.getAddress());
            }
        }
        startSearching();
    }
    /**
     * looks for available bluetooth devices (NOT PAIRED ONES) by registering a broadcast receiver
     * also creates Intent filters to monitor if the state of the Bluetooth Adapter has changed of if the pairing status
     * of any device has changed
     * when the broadcast receiver is done (takes about 12 seconds) then the list is displayed
     */
    private void startSearching(){
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        requestLocationPermission();

        if(mBluetoothAdapter.startDiscovery()) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mReceiver, filter);
            recieverRegistered = true;
        }
        display_list_view();
    }
    private void requestLocationPermission(){
        //Added to fix unpaired device error on android 5.0 and above
        ActivityCompat.requestPermissions(ConnectBluetooth.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
    /**
     * Displays the list of available bluetooth devices (both previously paired ones and newly available ones)
     */
    private void display_list_view(){
        listView1 = findViewById(R.id.listView1);
        listView1_adapter = new ArrayAdapter<>(ConnectBluetooth.this,
                android.R.layout.simple_list_item_1, mDeviceList);
        listView1.setAdapter(listView1_adapter);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                lastClickedDevice = mBT_DeviceList.get(position);

                if (lastClickedDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Toast.makeText(ConnectBluetooth.this, "Sending request to pair...", Toast.LENGTH_SHORT).show();
                    pairDevice(lastClickedDevice);
                }else
                    launch_intent(lastClickedDevice);

                listView1_adapter.notifyDataSetChanged();
            }
        });
    }
    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Decides which intent to launch
     */
    private void launch_intent (BluetoothDevice mBT){
        if (controlFlags.monitorConsumption){
            launch_intent_to_monitor_consumption(mBT);
        }
        else if (controlFlags.startCharging){
            launch_intent_to_startCharging(mBT);
        }
    }
    /**
     * Launches the startCharging activity and sends mBT to it
     * @param mBT
     */
    private void launch_intent_to_startCharging(BluetoothDevice mBT){
        Intent intent = new Intent(ConnectBluetooth.this, paymentActivity.class);
        intent.putExtra("BTdevice", mBT);
        startActivity(intent);
    }
    /**
     * Launches the MonitorPowerConsumption activity and sends mBT to it
     * @param mBT
     */
    private void launch_intent_to_monitor_consumption(BluetoothDevice mBT){
        Intent display_data = new Intent(ConnectBluetooth.this, MonitorPowerConsumption.class);
        display_data.putExtra("BTdevice", mBT);
        startActivity(display_data);
    }
    /**
     * when this activity exits- the bluetooth discovery is destroyed only if the receiver was registered
     * and the socket(i.e: the communication channel) is also destroyed
     * why? bluetooth discovery consumes a lot of power
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(recieverRegistered) {
            unregisterReceiver(mReceiver);
        }
        Log.i("******", "Destroying Connect Bluetooth Activity");

    }

    /**
     * Bluetooth Broadcast receiver. Does three things:
     * (1) Checks if new devices are available - if yes put them on the list of available devices
     * (2) Checks the bluetooth state - if bluetooth is turned off due to some error or intentionally then it requests the
     *      user to enable bluetooth
     * (3) Checks if the device bonding status has changed and notifies the user accordingly
     * (*) There are other bluetooth states that we can check for depending on what the paymentSystem requires
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!isDeviceAlreadyInList(device.getName() + "\n" + device.getAddress()+ "\n" + "Status: Available for pairing")) {
                    mBT_DeviceList.add(device);
                    if (device.getName() == null)
                        mDeviceList.add("Unknown" + "\n" + device.getAddress() + "\n" + "Status: Available for pairing");
                    else
                        mDeviceList.add(device.getName() + "\n" + device.getAddress() + "\n" + "Status: Available for pairing");
                        Log.i("BT1", device.getName() + "\n" + device.getAddress());
                    listView1_adapter.notifyDataSetChanged();
                }
            }
            else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(ConnectBluetooth.this, "Bluetooth turned off", Toast.LENGTH_LONG).show();
                        enableBluetooth();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        refreshDeviceList();
                        break;
                }
            }
            else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                switch (state){
                    case BluetoothDevice.BOND_BONDED:
                        if (lastClickedDevice == null) {
                            Toast.makeText(ConnectBluetooth.this, "Pairing successful. Tap on device again to connect.", Toast.LENGTH_LONG).show();
                        }else {
                            AlertDialog alertDialog = new AlertDialog.Builder(ConnectBluetooth.this).create();
                            alertDialog.setTitle("Pairing Successful");
                            alertDialog.setMessage("Do you want to connect to " + lastClickedDevice.getName() + "?");
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    refreshDeviceList();
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    launch_intent(lastClickedDevice);
                                }
                            });
                            alertDialog.show();
                        }
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        //Toast.makeText(ConnectBluetooth.this, "Pairing... ", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothDevice.BOND_NONE:
                        //Toast.makeText(ConnectBluetooth.this, "Unable to pair. Please try again.", Toast.LENGTH_LONG).show();
                        //No need to display a message, a message is displayed automatically
                        refreshDeviceList();
                        break;
                }
            }

        }
    };
    /**
     * Matches the name of all existing devices in the list with the provided device name.
     * @param device string containing the name, MAC address and status of a device
     * @return True if device is already in the list and false otherwise
     */
    private boolean isDeviceAlreadyInList(String device) {
        String [] device_parts = device.split("\\\n");
        String name = device_parts[0];
        for (String temp : mDeviceList) {
            temp = temp.split("\\\n")[0];
            if (temp.equals(name)) return true;
        }
        return false;
    }
}
