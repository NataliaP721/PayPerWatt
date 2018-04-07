package com.stripe.priceselection.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceCardData;
import com.stripe.android.model.SourceParams;
import com.stripe.android.view.CardMultilineWidget;
import com.stripe.priceselection.R;
import com.stripe.priceselection.bluetooth_and_login.InheritBluetoothFunctionality;
import com.stripe.priceselection.controller.ErrorDialogHandler;
import com.stripe.priceselection.controller.ProgressDialogController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * This class represents the page the user enters their card information into.
 * @author Aysha Panatch
 * @since March 24, 2018
 * References: https://github.com/stripe/stripe-payments-demo
 */
public class PaymentMultilineActivity extends InheritBluetoothFunctionality {

    /**
     * A controller used to start or finish progress based on the compositesubscription.
     */
    ProgressDialogController mProgressDialogController;
    /**
     * An object to show errors.
     */
    ErrorDialogHandler mErrorDialogHandler;
    /**
     * The widget used to recieve the card information from the user.
     */
    CardMultilineWidget mCardMultilineWidget;
    /**
     * A collection of subscriptions (used to sell content in a recurring manner on android applications).
     */
    CompositeSubscription mCompositeSubscription;
    /**
     * Used to help display the list in the xml file of the activity.
     */
    private SimpleAdapter mSimpleAdapter;
    /**
     * The list of the card sources to display.
     */
    private List<Map<String, String>> mCardSources= new ArrayList<>();

    /**
     * The PaymentMultilineActivity is initially setup in this method, creating a multiline widget to enter card information.
     * It also initialises a list of tokens at the bottom of the page.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_multiline);

        mCompositeSubscription = new CompositeSubscription();
        mCardMultilineWidget = findViewById(R.id.card_multiline_widget);

        mProgressDialogController =
                new ProgressDialogController(getSupportFragmentManager());

        mErrorDialogHandler = new ErrorDialogHandler(getSupportFragmentManager());

        ListView listView = findViewById(R.id.card_list_pma);
        mSimpleAdapter = new SimpleAdapter(
                this,
                mCardSources,
                R.layout.list_item_layout,
                new String[]{"last4", "tokenId"},
                new int[]{R.id.last4, R.id.tokenId});

        listView.setAdapter(mSimpleAdapter);
        mCompositeSubscription.add(
                RxView.clicks(findViewById(R.id.save_payment)).subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        saveCard();
                    }
                }));
    }

    /**
     * The card entered in the multiline widget is saved in this method into a field of Card called card. We create a Stripe token using the card source parameters.
     * Then we add this token to the composite subscription for this particular transaction.
     */
    private void saveCard() {
        Card card = mCardMultilineWidget.getCard();
        if (card == null) {
            return;
        }

        final Stripe stripe = new Stripe(this);
        final SourceParams cardSourceParams = SourceParams.createCardParams(card);
        // Note: using this style of Observable creation results in us having a method that
        // will not be called until we subscribe to it.
        final Observable<Source> tokenObservable =
                Observable.fromCallable(
                        new Callable<Source>() {
                            @Override
                            public Source call() throws Exception {
                                return stripe.createSourceSynchronous(cardSourceParams,
                                        PaymentConfiguration.getInstance().getPublishableKey());
                            }
                        });

        mCompositeSubscription.add(tokenObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(
                        new Action0() {
                            @Override
                            public void call() {
                                mProgressDialogController.startProgress();
                            }
                        })
                .doOnUnsubscribe(
                        new Action0() {
                            @Override
                            public void call() {
                                mProgressDialogController.finishProgress();
                            }
                        })
                .subscribe(
                        new Action1<Source>() {
                            @Override
                            public void call(Source source) {
                                addToList(source);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                mErrorDialogHandler.showError(throwable.getLocalizedMessage());
                            }
                        }));
    }

    /**
     * This method adds the token to the list of tokens at the bottom of the page so the user can view their token. In our case only one token is entered in each transaction and
     * the token list is cleared between users for safety reasons. However, the one token is shown to the user to show the token process for the purposes of ENEL 400.
     * @param source
     */
    private void addToList(@Nullable Source source) {
        if (source == null || !Source.CARD.equals(source.getType())) {
            return;
        }
        SourceCardData sourceCardData = (SourceCardData) source.getSourceTypeModel();

        String endingIn = getString(R.string.endingIn);
        Map<String, String> map = new HashMap<>();
        map.put("last4", endingIn + " " + sourceCardData.getLast4());
        map.put("tokenId", source.getId());
        mCardSources.add(map);
        mSimpleAdapter.notifyDataSetChanged();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Sends token to server using POST request and displays Toast with response. 
        URLConnect obj = new URLConnect();
        CharSequence getResponse =  obj.GET();
        CharSequence postResponse =  obj.POST("token\t"+source.getId());
        Context context_p = getApplicationContext();
        int duration_p = Toast.LENGTH_LONG;
        Toast toast_p = Toast.makeText(context_p, postResponse, duration_p);
        toast_p.show();

        Intent intent = new Intent("com.stripe.priceselection.intent.action.Launch");
        startActivity(intent);

    }

    /**
     * Bluetooth communication method inherited as a method of an abstract class, not used in the acitvity.
     * @param msg
     */
    protected void messageReadAction(Message msg) {
        //do nothing here unless you want to read something from Arduino via bluetooth
        //DO NOT DELETE this method - necessary to define all unimplemented methods of an abstract class
        Log.i("*************", "INSIDE MESSAGE READ ACTION IN MULTILINE");

    }

    /**
     * This method allows the phyical back button on the Android device to navigate to where the back button on the application would lead.
     */
    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }


}
