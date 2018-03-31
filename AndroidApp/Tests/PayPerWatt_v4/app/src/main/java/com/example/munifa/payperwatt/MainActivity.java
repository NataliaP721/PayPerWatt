package com.example.munifa.payperwatt;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("******", "Creating Main Activity");
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        controlFlags.startCharging = false;
        controlFlags.monitorConsumption = false;

        //TODO delete later - for testing only
        controlFlags.password = "123pay";
    }

    //if the PayPerWatt Icon is pressed then a request is sent to the server to check
    //if there is an existing user already charging
    public void startCharging(View view){
        URLConnect obj = new URLConnect();
        obj.GET();
        String response = obj.POST("isInUse\t");

        //TODO delete later - for testing only
        if(controlFlags.password != ""){ //if password is not empty then somebody is charging
            response = "true";
        }
        //TODO end of delete later

        while (response == null){ //keep trying until you get something from the server
            response = obj.POST("isInUse\t");
            //display progress dialog
            ProgressDialog progressDialog = new ProgressDialog(getBaseContext(), ProgressDialog.STYLE_SPINNER);
            progressDialog = ProgressDialog.show(this, "Please Wait", "Starting...",
                    true, false);
        }

        if(response.equals("true")){
            //show option dialog
            //Options Login or Cancel
            //launch intent to login page
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage("The device is currently in use by another customer. If you are this customer, please " +
                    "proceed to the login page. If not, please try again later.");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "PROCEED TO LOGIN", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent login = new Intent(MainActivity.this, EnterPasswordActivity.class);
                    controlFlags.monitorConsumption = true;
                    startActivity(login);
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "GO BACK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
        else if (response.equals("false")){
            //launch intent to create password page
            Intent newUser = new Intent(MainActivity.this, CreatePasswordActivity.class);
            controlFlags.startCharging = true;
            startActivity(newUser);
        }
        else{
            Toast.makeText(this, "Error! Please try again.", Toast.LENGTH_LONG).show();
        }
    }

//    /**
//     * Called when the user taps the Monitor Power Consumption
//     */
//    public void monitorConsumption(View view) {
//        Intent intent = new Intent(this, ConnectBluetooth.class);
//        controlFlags.monitorConsumption = true;
//        startActivity(intent);
//    }
//    /**
//     * Called when the user taps on the Start Charging Button
//     */
//    public void startCharging(View view){
//        Intent intent = new Intent(this, ConnectBluetooth.class);
//        controlFlags.startCharging = true;
//        startActivity(intent);
//    }
}