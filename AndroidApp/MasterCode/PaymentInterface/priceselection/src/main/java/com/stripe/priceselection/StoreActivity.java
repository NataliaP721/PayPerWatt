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
 * This activity represents the "store" page or the page where the user select's dollar amount to pay. It contains a increment/decrement dollar selecting interface.
 * It also has a floating action button and a message about payment process (if you do not meet your selected maximum amount, you will be charged for how much power you used).
 * @author Aysha Panatch
 * @since March 26, 2018
 * References: https://github.com/stripe/stripe-payments-demo
 */
public class StoreActivity
        extends InheritBluetoothFunctionality
        implements StoreAdapter.TotalItemsChangedListener{

    /**
     * Field String PUBLISHABLE_KEY is our personal testing key, set to default for this public code.
     */
    private static final String PUBLISHABLE_KEY =
            "put your key here";
    /**
     * mGoToCartButton is the floating action button the user uses to pay or proceed to payment checkout.
     */
    private FloatingActionButton mGoToCartButton;
    /**
     * Used to help display the store item list in the xml file of the activity.
     */
    private StoreAdapter mStoreAdapter;

    /**
     * The StoreActivity is initially setup in this method, creating the item divided recyclerview as well as initialises the listener for the checkout (charge) button linked to the server.
     * There is also an intent to the login activity setup here in a dialog.
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
     * This methods controls whether the floating action button (charge symbol) displays or not.
     * The button displays for prices over $0.
     * @param totalItems is the number of dollars the user selects
     */
    @Override
    public void onTotalItemsChanged(int totalItems) {
        if (totalItems > 0) {
            mGoToCartButton.show();
        } else {
            mGoToCartButton.hide();
        }
    }

    /**
     * Bluetooth communication method inherited as a method of an abstract class, not used in the acitvity.
     * @param msg
     */
    protected void messageReadAction(Message msg) {
        //do nothing here unless you want to read something from Arduino via bluetooth
        //DO NOT DELETE this method because it necessary to define all unimplemented methods of an abstract class
        Log.i("*************", "INSIDE MESSAGE READ ACTION IN STORE ACTIVITY");
    }

    /**
     * This method allows the phyical back button on the Android device to navigate to where the back button on the application would lead.
     */
    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }

}
