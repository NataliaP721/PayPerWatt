package com.stripe.priceselection.bluetooth_and_login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.stripe.priceselection.R;

public class EnterPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        controlFlags.goingToLogin = false;
    }
    //called when the user clicks on the SAVE button
    public void verifyPassword(View view) {
        EditText editText = (EditText) findViewById(R.id.editText1);
        String password = editText.getText().toString();

        if (password.equals("")) {
            Toast.makeText(this, "Please enter a password.", Toast.LENGTH_LONG).show();
        } else {
            URLConnect obj = new URLConnect();
            obj.GET();
            String response = obj.POST("passwordVerify\t" + password);

            if (response.equals("true")) {
                Intent intent = new Intent(this, ConnectBluetooth.class);
                startActivity(intent);
            } else if (response.equals("false")) {
                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Verification Failed");
                alertDialog.setMessage("Incorrect password. Please try again.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            } else {
                Toast.makeText(this, "Error! Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }
}

