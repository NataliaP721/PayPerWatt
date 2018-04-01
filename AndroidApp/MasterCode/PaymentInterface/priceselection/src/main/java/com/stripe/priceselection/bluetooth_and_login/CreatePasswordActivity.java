package com.stripe.priceselection.bluetooth_and_login;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.stripe.priceselection.R;
/**
 * This activity asks the user to create a password by entering it into the
 * editText field. The password entered is checked and then sent to the
 * server for storing.
 * @author Munifa Saeed
 * @version 1.0
 * @since March 23, 2018
 */
public class CreatePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
    }

    /**
     * Called when the user clicks on the SAVE button.
     * Gets the user's password form the view and then sends it to the server for storing
     * @param view
     */
    public void savePassword(View view){

        EditText editText = (EditText) findViewById(R.id.editText2);
        String password = editText.getText().toString();

        if(password.equals("")){
            Toast.makeText(this, "Please enter a password.", Toast.LENGTH_LONG).show();
        }
        else {
            URLConnect obj = new URLConnect();
            obj.GET();
            String response = obj.POST("passwordSave\t" + password);

            if (response.equals(password)) {
                Intent intent = new Intent(this, ConnectBluetooth.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error! Please try again." + response, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }
}

