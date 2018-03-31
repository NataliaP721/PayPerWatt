package com.example.munifa.payperwatt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreatePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
    }

    //called when the user clicks on the SAVE button
    public void savePassword(View view){

        EditText editText = (EditText) findViewById(R.id.editText2);
        String password = editText.getText().toString();

        if(password.equals("")){
            Toast.makeText(this, "Please enter a password.", Toast.LENGTH_LONG).show();
        }
        else{
            URLConnect obj = new URLConnect();
            obj.GET();
            String response = obj.POST("passwordSave\t"+password);

            if(response.equals(password)) {
                //TODO delete toast
                //For now I am just showing the password
                Toast.makeText(this, password +" "+ response, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, ConnectBluetooth.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error! Please try again."+response, Toast.LENGTH_LONG).show();
            }
        }


    }
}
