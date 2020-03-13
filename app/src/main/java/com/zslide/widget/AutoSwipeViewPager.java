/*
 * Copyright (C) 2013 Leszek Mzyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zslide.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zslide.utils.Ticker;

public class AutoSwipeViewPager extends InfiniteViewPager {


    private static final long DEFAULT_SWIPE_INTERVAL = 5000;

    private long autoSwipeDelay = 0;
    private long autoSwipeInterval = DEFAULT_SWIPE_INTERVAL;

    private Ticker ticker;
    private boolean swipeEnabled;

    public AutoSwipeViewPager(Context context) {
        this(context, null);
    }

    public AutoSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        ticker = new Ticker();
        setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    stopAutoSwipe();
                    break;
                case MotionEvent.ACTION_UP:
                    startAutoSwipe(autoSwipeDelay);
                    break;
            }
            return false;
        });
    }

    public void setAutoSwipeDelay(long delay) {
        this.autoSwipeDelay = delay;
    }

    public void setAutoSwipeInterval(long interval) {
        this.autoSwipeInterval = interval;
    }

    public void startAutoSwipe(long delay, long interval) {
        this.autoSwipeDelay = delay;
        this.autoSwipeInterval = interval;
        startAutoSwipe();
    }

    public void startAutoSwipe(long delay) {
        this.autoSwipeDelay = delay;
        startAutoSwipe();
    }

    public void startAutoSwipe() {
        ticker.start(autoSwipeDelay, autoSwipeInterval, this::swipe);
    }

    public void stopAutoSwipe() {
        ticker.stop();
    }

    private void swipe() {
        if (getAdapter().getCount() <= 1) {
            return;
        }

        int next = getCurrentItem() + 1;
        setCurrentItem(next);
    }

    public void setSwipeEnabled(boolean enabled) {
        this.swipeEnabled = enabled;
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAutoSwipe();
        super.onDetachedFromWindow();
    }
}
