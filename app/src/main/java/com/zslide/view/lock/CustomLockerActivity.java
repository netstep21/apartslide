package com.zslide.view.lock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.dialogs.LockerZmoneyDialog;
import com.zslide.utils.EasySharedPreferences;
import com.zslide.utils.ZLog;
import com.buzzvil.buzzscreen.sdk.BaseLockerActivity;
import com.buzzvil.buzzscreen.sdk.Campaign;
import com.buzzvil.buzzscreen.sdk.widget.Slider;
import com.buzzvil.buzzscreen.sdk.widget.SliderIcon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by chulwoo on 2017. 9. 8..
 */

public class CustomLockerActivity extends BaseLockerActivity {

    private static final String PAGER_TUTORIAL_LEARNED_USER = "CustomLockerActivity.pagerTutorialLearnedUser";
    private static final String UNLOCK_TUTORIAL_LEARNED_USER = "CustomLockerActivity.unlockTutorialLearnedUser";
    private static final String LANDING_TUTORIAL_LEARNED_USER = "CustomLockerActivity.landingTutorialLearnedUser";
    private static final String TAG_ZMONEY = "zmoney";

    @BindView(R.id.slider) Slider slider;
    @BindView(R.id.time) TextView timeView;
    @BindView(R.id.amPm) TextView amPmView;
    @BindView(R.id.date) TextView dateView;
    @BindView(R.id.arrowTop) ImageView arrowTopView;
    @BindView(R.id.arrowBottom) ImageView arrowBottomView;
    @BindView(R.id.tutorialMessageContainer) View tutorialMessageContainer;
    @BindView(R.id.tutorialBoldTitle) TextView tutorialBoldTitleView;
    @BindView(R.id.tutorialTitle) TextView tutorialTitleView;
    @BindView(R.id.tutorialDescription) TextView tutorialDescriptionView;
    @BindView(R.id.tutorialArrowUp) ImageView tutorialArrowUpView;
    @BindView(R.id.tutorialArrowDown) ImageView tutorialArrowDownView;
    @BindView(R.id.tutorialArrowRight) ImageView tutorialArrowRightView;
    @BindView(R.id.tutorialArrowLeft) ImageView tutorialArrowLeftView;

    private HashMap<View, Animator> animators;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_locker);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        animators = new HashMap<>();
        timeView.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
        slider.setLeftOnSelectListener(new SliderIcon.OnSelectListener() {
            @Override
            public void onSelect() {
                landing();
            }
        });
        slider.setRightOnSelectListener(new SliderIcon.OnSelectListener() {
            @Override
            public void onSelect() {
                unlock();
            }
        });

        setPageIndicators(arrowTopView, arrowBottomView);
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.BLACK);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            if (!EasySharedPreferences.with(CustomLockerActivity.this).getBoolean(PAGER_TUTORIAL_LEARNED_USER, false)) {
                startPagerTutorial();
            } else if (!EasySharedPreferences.with(CustomLockerActivity.this).getBoolean(UNLOCK_TUTORIAL_LEARNED_USER, false)) {
                startUnlockTutorial();
            } else if (!EasySharedPreferences.with(CustomLockerActivity.this).getBoolean(LANDING_TUTORIAL_LEARNED_USER, false)) {
                startLandingTutorial();
            }
        }
    }

    @Override
    protected void onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (animators.containsKey(tutorialArrowUpView)) {
                stopPagerTutorial();
            }
        }

        if (getSupportFragmentManager().findFragmentByTag(TAG_ZMONEY) != null) {
            ((LockerZmoneyDialog) getSupportFragmentManager().findFragmentByTag(TAG_ZMONEY)).dismiss();
        }

        super.onPause();
    }

    @RequiresApi(api = LOLLIPOP)
    private void startPagerTutorial() {
        tutorialMessageContainer.setVisibility(View.VISIBLE);

        tutorialBoldTitleView.setText(R.string.tutorial_locker_title1b);
        tutorialTitleView.setText(R.string.tutorial_locker_title1);
        tutorialDescriptionView.setText(R.string.tutorial_locker_desc1);

        tutorialArrowUpView.setVisibility(View.VISIBLE);
        tutorialArrowDownView.setVisibility(View.VISIBLE);

        startTutorialAnimation(tutorialArrowUpView, tutorialArrowUpView.getWidth() / 2, tutorialArrowUpView.getHeight());
        startTutorialAnimation(tutorialArrowDownView, tutorialArrowDownView.getWidth() / 2, 0);
    }

    @RequiresApi(api = LOLLIPOP)
    private void stopPagerTutorial() {
        if (animators.containsKey(tutorialArrowUpView)) {
            stopTutorialAnimation(animators.remove(tutorialArrowUpView));
        }

        if (animators.containsKey(tutorialArrowDownView)) {
            stopTutorialAnimation(animators.remove(tutorialArrowDownView));
        }

        tutorialMessageContainer.setVisibility(View.GONE);
        tutorialArrowUpView.setVisibility(View.GONE);
        tutorialArrowDownView.setVisibility(View.GONE);
    }

    @RequiresApi(api = LOLLIPOP)
    private void completePagerTutorial() {
        stopPagerTutorial();
        EasySharedPreferences.with(CustomLockerActivity.this).putBoolean(PAGER_TUTORIAL_LEARNED_USER, true);
        startUnlockTutorial();
    }

    @RequiresApi(api = LOLLIPOP)
    private void startUnlockTutorial() {
        tutorialMessageContainer.setVisibility(View.VISIBLE);

        tutorialBoldTitleView.setText(R.string.tutorial_locker_title2b);
        tutorialTitleView.setText(R.string.tutorial_locker_title2);
        tutorialDescriptionView.setText(R.string.tutorial_locker_desc2);

        tutorialArrowRightView.setVisibility(View.VISIBLE);

        startTutorialAnimation(tutorialArrowRightView, 0, tutorialArrowRightView.getHeight() / 2);
    }

    @RequiresApi(api = LOLLIPOP)
    private void stopUnlockTutorial() {
        tutorialMessageContainer.setVisibility(View.GONE);
        tutorialArrowRightView.setVisibility(View.INVISIBLE);
        if (animators.containsKey(tutorialArrowRightView)) {
            stopTutorialAnimation(animators.remove(tutorialArrowRightView));
        }
    }

    @RequiresApi(api = LOLLIPOP)
    private void completeUnlockTutorial() {
        stopUnlockTutorial();
        EasySharedPreferences.with(CustomLockerActivity.this).putBoolean(UNLOCK_TUTORIAL_LEARNED_USER, true);
    }

    @RequiresApi(api = LOLLIPOP)
    private void startLandingTutorial() {
        tutorialMessageContainer.setVisibility(View.VISIBLE);

        tutorialBoldTitleView.setText(R.string.tutorial_locker_title3b);
        tutorialTitleView.setText(R.string.tutorial_locker_title3);
        tutorialDescriptionView.setText(R.string.tutorial_locker_desc3);

        tutorialArrowLeftView.setVisibility(View.VISIBLE);
        startTutorialAnimation(tutorialArrowLeftView, tutorialArrowLeftView.getWidth(), tutorialArrowLeftView.getHeight() / 2);
    }

    @RequiresApi(api = LOLLIPOP)
    private void stopLandingTutorial() {
        tutorialMessageContainer.setVisibility(View.GONE);
        tutorialArrowLeftView.setVisibility(View.INVISIBLE);
        if (animators.containsKey(tutorialArrowLeftView)) {
            stopTutorialAnimation(animators.remove(tutorialArrowLeftView));
        }
    }

    @RequiresApi(api = LOLLIPOP)
    private void completeLandingTutorial() {
        stopLandingTutorial();
        EasySharedPreferences.with(CustomLockerActivity.this).putBoolean(LANDING_TUTORIAL_LEARNED_USER, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startTutorialAnimation(View view, int centerX, int centerY) {
        if (view.isAttachedToWindow()) {
            int radius = view.getHeight() > view.getWidth() ? view.getHeight() * 2 : view.getWidth() * 2;
            Animator animator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, 0, radius).setDuration(1000);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    startTutorialAnimation(view, centerX, centerY);
                }
            });
            animator.start();
            if (animators.containsKey(view)) {
                stopTutorialAnimation(animators.get(view));
            }
            animators.put(view, animator);
        } else {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    startTutorialAnimation(view, centerX, centerY);
                }
            });
        }
    }

    private void stopTutorialAnimation(Animator animator) {
        animator.removeAllListeners();
        animator.cancel();
    }

    @Override
    protected void onCurrentCampaignUpdated(Campaign campaign) {
        ZLog.i(this, campaign.toString());
        int landingPoints = campaign.getLandingPoints();
        int unlockPoints = campaign.getUnlockPoints();
        if (landingPoints > 0) {
            this.slider.setLeftText(String.format("+ %d", new Object[]{Integer.valueOf(campaign.getLandingPoints())}));
        } else {
            this.slider.setLeftText("");
        }

        if (unlockPoints > 0) {
            this.slider.setRightText(String.format("+ %d", new Object[]{Integer.valueOf(campaign.getUnlockPoints())}));
        } else {
            this.slider.setRightText("");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (animators.containsKey(tutorialArrowUpView)) {
                completePagerTutorial();
            }
        }
    }

    @Override
    protected void onTimeUpdated(Calendar cal) {
        Date date = cal.getTime();
        String minuteFormat = (new SimpleDateFormat("h:mm", Locale.getDefault())).format(date);
        this.timeView.setText(minuteFormat);
        String amPmFormat = (new SimpleDateFormat("aa", Locale.US)).format(date);
        this.amPmView.setText(amPmFormat);
        String strTime = (new SimpleDateFormat("EEEE M/d", Locale.getDefault())).format(date);
        this.dateView.setText(strTime);
    }

    @OnClick(R.id.zmoney)
    public void showZmoney() {
        LockerZmoneyDialog.newInstance().show(getSupportFragmentManager(), TAG_ZMONEY);
    }

    @Override
    protected void landing() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (animators.containsKey(tutorialArrowLeftView)) {
                completeLandingTutorial();
            }
        }
        super.landing();
    }

    @Override
    protected void unlock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (animators.containsKey(tutorialArrowRightView)) {
                completeUnlockTutorial();
            }
        }
        super.unlock();
    }
}
