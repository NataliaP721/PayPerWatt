package com.stripe.priceselection;

import android.support.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.Currency;

/**
 * This class is used to store utility functions used in other activities. These methods are used to help calculate/create certain values.
 * @author Aysha Panatch
 * @since March 24, 2018
 * References: https://github.com/stripe/stripe-payments-demo
 */
public class StoreUtils {

    /**
     * Returns the String of the emoji based on it's unicode int value.
     * @param unicode
     * @return
     */
    static String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    /**
     * Turns the calculated price from a long into a well-formatted String.
     * @param price as a long
     * @param currency
     * @return the price as a formatted String
     */
    static String getPriceString(long price, @Nullable Currency currency) {
        Currency displayCurrency = currency == null
                ? Currency.getInstance("CAD")
                : currency;

        int fractionDigits = displayCurrency.getDefaultFractionDigits();
        int totalLength = String.valueOf(price).length();
        StringBuilder builder = new StringBuilder();
        builder.append('\u00A4');

        if (fractionDigits == 0) {
            for (int i = 0; i < totalLength; i++) {
                builder.append('#');
            }
            DecimalFormat noDecimalCurrencyFormat = new DecimalFormat(builder.toString());
            noDecimalCurrencyFormat.setCurrency(displayCurrency);
            return noDecimalCurrencyFormat.format(price);
        }

        int beforeDecimal = totalLength - fractionDigits;
        for (int i = 0; i < beforeDecimal; i++) {
            builder.append('#');
        }
        // So we display "$0.55" instead of "$.55"
        if (totalLength <= fractionDigits) {
            builder.append('0');
        }
        builder.append('.');
        for (int i = 0; i < fractionDigits; i++) {
            builder.append('0');
        }
        double modBreak = Math.pow(10, fractionDigits);
        double decimalPrice = price / modBreak;

        DecimalFormat decimalFormat = new DecimalFormat(builder.toString());
        decimalFormat.setCurrency(displayCurrency);

        return decimalFormat.format(decimalPrice);
    }
}
