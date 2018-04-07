package com.stripe.priceselection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * The class ItemDivider extends RecyclerView which we used to display our custom price increment/decrement interface.
 * It formats the way items in this RecyclerView would be formatted and divided up.
 * In our case our recycler view contains only one item (price selection) but this format can be used to extend the list to any number of items.
 * @author Aysha Panatch
 * @since March 24, 2018
 * References: https://github.com/stripe/stripe-payments-demo
 */
class ItemDivider extends RecyclerView.ItemDecoration {

    /**
     * the divider being created illustrated through a Drawable.
     */
    private Drawable divider;

    /**
     * Custom divider will be used in the list.
     */
    ItemDivider(Context context, int resId) {
        divider = ContextCompat.getDrawable(context, resId);
    }

    /**
     * Sets the bounds and creates the divider.
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}
