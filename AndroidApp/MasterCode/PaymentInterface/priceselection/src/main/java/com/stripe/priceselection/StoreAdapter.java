package com.stripe.priceselection;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Currency;

/**
 * This class manages display items in the StoreActivity (in our case the activity only contains one price selection).
 * @author Aysha Panatch
 * @since March 24, 2018
 * References: https://github.com/stripe/stripe-payments-demo
 */
public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    /**
     * This method contains the emoji's used in the recyclerview, which corresponds to a dollarsign.
     */
    private static final int[] EMOJI_DOLLARSIGN = {
            0x1F4B2,
    };

    /**
     * This method is not used in the functionality of our code, but the price of each increment is artibitraly set to $1.
     */
    private static final int[] EMOJI_PRICES = {
            100,
    };

    /**
     * This class extends recyclerview as a method to display items in the StoreActivity (in our case the acitvity only contains one price selection).
     * It also helps display the desired emoji sign.
     * @author Aysha Panatch
     * @since March 24, 2018
     * References: https://github.com/stripe/stripe-payments-demo
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * Holds the currency of the payment exchange (arbitrary as we do not process payments only test tokens)
         */
        private Currency mCurrency;
        /**
         * TextView for the emoji (in our case this is a dollar sign)
         */
        private TextView mEmojiTextView;
        /**
         * TextView for the price corresponding to the quantity selected (this value is faded into the background for our application as we don't need it)
         */
        private TextView mPriceTextView;
        /**
         * TextView for the quantity selected
         */
        private TextView mQuantityTextView;
        /**
         * "+" button used to increment the selected price
         */
        private ImageButton mAddButton;
        /**
         * "-" button used to decrement the selected price
         */
        private ImageButton mRemoveButton;

        private int mPosition;
        ViewHolder(final LinearLayout pollingLayout, Currency currency) {
            super(pollingLayout);
            mEmojiTextView = pollingLayout.findViewById(R.id.tv_emoji);
            mPriceTextView = pollingLayout.findViewById(R.id.tv_price);
            mQuantityTextView = pollingLayout.findViewById(R.id.tv_quantity);
            mAddButton = pollingLayout.findViewById(R.id.tv_plus);
            mRemoveButton = pollingLayout.findViewById(R.id.tv_minus);

            mCurrency = currency;
            mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StoreAdapter.this.bumpItemQuantity(mPosition, true);
                }
            });

            mRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StoreAdapter.this.bumpItemQuantity(mPosition, false);
                }
            });
        }


        void setHidden(boolean hidden) {
            int visibility = hidden ? View.INVISIBLE : View.VISIBLE;
            mEmojiTextView.setVisibility(visibility);
            mPriceTextView.setVisibility(visibility);
            mQuantityTextView.setVisibility(visibility);
            mAddButton.setVisibility(visibility);
            mRemoveButton.setVisibility(visibility);
        }

        /**
         * Sets the emoji in the activity
         * @param emojiUnicode
         */
        void setEmoji(int emojiUnicode) {
            mEmojiTextView.setText(StoreUtils.getEmojiByUnicode(emojiUnicode));
        }

        /**
         * Sets the price in the activity (this value is faded into the background for our application as we don't need it)
         * @param price
         */
        void setPrice(int price) {
            mPriceTextView.setText(StoreUtils.getPriceString(price, mCurrency));
        }

        /**
         * Sets the quantity of dollars ordered in the activity
         * @param quantity
         */
        void setQuantity(int quantity) {
            mQuantityTextView.setText(String.valueOf(quantity));
        }

        /**
         * Sets the position in the activity
         * @param position
         */
        void setPosition(int position) {
            mPosition = position;
        }
    }

    /**
     * An activity to launch for the result
     */
    private Activity mActivity;
    /**
     * Holds the currency of the payment exchange (arbitrary as we do not process payments only test tokens)
     */
    private Currency mCurrency;
    /**
     * An array to store quantity per item
     */
    private int[] mQuantityOrdered;
    /**
     * Corresponds to the total items ordered
     */
    public int mTotalOrdered;
    /**
     * Listens to the total items changed
     */
    private TotalItemsChangedListener mTotalItemsChangedListener;

    /**
     * Assigns the activity to the fields
     * @param activity
     */
    public StoreAdapter(StoreActivity activity) {
        mActivity = activity;
        mTotalItemsChangedListener = activity;
        mCurrency = Currency.getInstance("CAD");
        mQuantityOrdered = new int[EMOJI_DOLLARSIGN.length];
    }

    /**
     * Increments/decrements the corresponding fields when the user selects the + or - images on the activity
     * @param index
     * @param increase
     */
    public void bumpItemQuantity(int index, boolean increase) {
        if (index >= 0 && index < mQuantityOrdered.length) {
            if (increase) {
                mQuantityOrdered[index]++;
                mTotalOrdered++;
                mTotalItemsChangedListener.onTotalItemsChanged(mTotalOrdered);
            } else if(mQuantityOrdered[index] > 0) {
                mQuantityOrdered[index]--;
                mTotalOrdered--;
                mTotalItemsChangedListener.onTotalItemsChanged(mTotalOrdered);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == EMOJI_DOLLARSIGN.length) {
            holder.setHidden(true);
        } else {
            holder.setHidden(false);
            holder.setEmoji(EMOJI_DOLLARSIGN[position]);
            holder.setPrice(EMOJI_PRICES[position]);
            holder.setQuantity(mQuantityOrdered[position]);
            holder.setPosition(position);
        }

    }

    /**
     * Returns the total items, in our case this is always 1 corresponding to the one payment choice option
     * @return
     */
    @Override
    public int getItemCount() {
        return 1;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout pollingView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_item, parent, false);

        return new ViewHolder(pollingView, mCurrency);
    }

    /**
     * This method clears the selected items (resets).
     */
    public void clearItemSelections() {
        mQuantityOrdered = new int[EMOJI_DOLLARSIGN.length];
        notifyDataSetChanged();
        if (mTotalItemsChangedListener != null) {
            mTotalItemsChangedListener.onTotalItemsChanged(0);
        }
    }

    /**
     * Listens to the number of items that have been changed
     */
    public interface TotalItemsChangedListener {
        void onTotalItemsChanged(int totalItems);
    }
}
