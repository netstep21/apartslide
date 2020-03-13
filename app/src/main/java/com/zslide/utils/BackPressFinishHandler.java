package com.zslide.utils;

import android.app.Activity;
import android.widget.Toast;

import com.zslide.R;

import io.reactivex.functions.Action;
import lombok.Setter;

/**
 * Created by chulwoo on 15. 10. 20..
 */
public class BackPressFinishHandler {

    private static final int DEFAULT_DEALY_MILLI_SEC = 2000;

    private long mBackKeyPressedTime = 0;
    private Toast mToast;

    private Activity mActivity;
    private int mDelayMilliSec = DEFAULT_DEALY_MILLI_SEC;
    @Setter private Action backPressAction;

    public BackPressFinishHandler(Activity context) {
        this.mActivity = context;
    }

    public BackPressFinishHandler(Activity context, int delayMilliSec) {
        this(context);
        mDelayMilliSec = delayMilliSec;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > mBackKeyPressedTime + mDelayMilliSec) {
            mBackKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }

        if (System.currentTimeMillis() <= mBackKeyPressedTime + mDelayMilliSec) {
            if (backPressAction == null) {
                mActivity.finish();
            } else {
                try {
                    backPressAction.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mToast.cancel();
        }
    }

    public void showGuide() {
        mToast = Toast.makeText(mActivity, R.string.message_guide_back_press, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
