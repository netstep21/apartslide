package com.zslide.widget;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by chulwoo on 16. 4. 6..
 */
public abstract class RightDrawableOnTouchListener implements View.OnTouchListener {

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        Drawable rightDrawable = null;
        if (v instanceof TextView) {
            rightDrawable = ((TextView) v).getCompoundDrawables()[DRAWABLE_RIGHT];
        }

        if (event.getAction() == MotionEvent.ACTION_UP && rightDrawable != null) {
            if (event.getRawX() >= (v.getRight() - rightDrawable.getBounds().width() - v.getPaddingRight())) {
                // your action here
                onDrawableTouch();
                return true;
            }
        }
        return false;
    }

    public abstract boolean onDrawableTouch();
}