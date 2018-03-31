package com.stripe.priceselection.bluetooth_and_login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.stripe.priceselection.R;

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

    }

    //if the PayPerWatt Icon is pressed then a request is sent to the server to check
    //if there is an existing user already charging
    public void startCharging(View view){
        URLConnect obj = new URLConnect();
        obj.GET();
        String response = obj.POST("isInUse\t");

        if(response.equals("true")){
            //show option dialog
            //Options Login or Cancel
            //launch intent to login page
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage("If you are the customer currently using the device, please " +
                    "proceed. If not, please try again later.");
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
}