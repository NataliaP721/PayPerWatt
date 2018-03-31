package com.stripe.priceselection;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.stripe.android.PaymentConfiguration;
import com.stripe.priceselection.bluetooth_and_login.ConnectBluetooth;
import com.stripe.priceselection.bluetooth_and_login.EnterPasswordActivity;
import com.stripe.priceselection.bluetooth_and_login.InheritBluetoothFunctionality;
import com.stripe.priceselection.bluetooth_and_login.MainActivity;
import com.stripe.priceselection.bluetooth_and_login.MonitorPowerConsumption;
import com.stripe.priceselection.bluetooth_and_login.controlFlags;

/**
 *
 */
public class StoreActivity
        extends InheritBluetoothFunctionality
        implements StoreAdapter.TotalItemsChangedListener{

    /**
     *
     */
    private static final String PUBLISHABLE_KEY =
            "sk_test_Pr9JjZAMTijvQnaFc1F5bV5f";
    private FloatingActionButton mGoToCartButton;
    private StoreAdapter mStoreAdapter;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        PaymentConfiguration.init(PUBLISHABLE_KEY);
        mGoToCartButton = findViewById(R.id.fab_checkout);
        mStoreAdapter = new StoreAdapter(this);
        ItemDivider dividerDecoration = new ItemDivider(this, R.drawable.item_divider);
        RecyclerView recyclerView = findViewById(R.id.rv_store_items);

        mGoToCartButton.hide();
        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerDecoration);
        recyclerView.setAdapter(mStoreAdapter);

        mGoToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                URLConnect obj = new URLConnect();
                obj.GET();

//                Toast.makeText(getApplicationContext(), "ButtonClicked", Toast.LENGTH_LONG).show();

                String response = obj.POST("authorize\t" + mStoreAdapter.mTotalOrdered);
//
                if (response.equals(Integer.toString(mStoreAdapter.mTotalOrdered))) {
                    Toast.makeText(getApplicationContext(), "Server Authorized Payment", Toast.LENGTH_LONG).show();

                    //send max authorized cost to Arduino
                    String write_cost = "$"+Integer.toString(mStoreAdapter.mTotalOrdered)+"\n";
                    mService.write(write_cost.getBytes());

                    //launch intent to login activiy
                    AlertDialog alertDialog = new AlertDialog.Builder(StoreActivity.this).create();
                    alertDialog.setMessage("Charging started. Please proceed to login and view your power consumption!");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "PROCEED TO LOGIN", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent login = new Intent(StoreActivity.this, EnterPasswordActivity.class);
                            //destroyBroadCastReceiver();
                            //if (pDialog.isShowing()) pDialog.cancel();
                            controlFlags.monitorConsumption = true;
                            controlFlags.startCharging = false;
                            controlFlags.goingToLogin = true;
                            mService.stop();
                            startActivity(login);
                        }
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            }
        });
    }

    /**
     *
     * @param totalItems
     */
    @Override
    public void onTotalItemsChanged(int totalItems) {
        if (totalItems > 0) {
            mGoToCartButton.show();
        } else {
            mGoToCartButton.hide();
        }
    }

    protected void messageReadAction(Message msg) {
        //do nothing here unless you want to read something from Arduino via bluetooth
        //DO NOT DELETE this method because it necessary to define all unimplemented methods of an abstract class
        Log.i("*************", "INSIDE MESSAGE READ ACTION IN STORE ACTIVITY");
    }

    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }

}
