package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.User;
import com.zslide.dialogs.LevelDialog;
import com.zslide.models.LevelCouponLog;
import com.zslide.models.LevelInfo;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.widget.LevelBenefitLogView;
import com.crashlytics.android.Crashlytics;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 16. 7. 14..
 */
public class LevelBenefitActivity extends BaseActivity {

    @BindDimen(R.dimen.spacing_micro) int SPACING_MICRO;
    @BindDimen(R.dimen.spacing_smaller) int SPACING_SMALLER;
    @BindDimen(R.dimen.spacing_normal) int SPACING_NORMAL;
    @BindDimen(R.dimen.spacing_large) int SPACING_LARGE;

    @BindView(R.id.icon) ImageView iconView;
    @BindView(R.id.smallIcon) ImageView smallIconView;
    @BindView(R.id.levelName) TextView levelNameView;
    @BindView(R.id.keepingConditionContainer) FrameLayout keepingConditionContainer;
    @BindView(R.id.keepingConditionLabel) TextView keepingConditionLabelView;
    @BindView(R.id.keepingConditionItemContainer) LinearLayout keepingConditionItemContainer;
    @BindView(R.id.couponContainer) LinearLayout couponContainer;
    @BindView(R.id.couponItemContainer) LinearLayout couponItemContainer;
    @BindView(R.id.benefitAmount) TextView benefitAmountView;
    @BindView(R.id.logContainer) LinearLayout logContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = UserManager.getInstance().getUserValue();
        LevelInfo levelInfo = user.getLevelInfo();
        if (levelInfo == null) {
            // 대체 왜... 서버에선 이걸 해결 안해줄까;
            Crashlytics.log("LevelInfo is null...");
            finish();
            return;
        }
        LevelInfo.Advantage advantage = levelInfo.getAdvantage();
        if (advantage == null) {
            // 대체 왜... 서버에선 이걸 해결 안해줄까;
            Crashlytics.log("Advantage is null...");
            finish();
            return;
        }

        glide().load(advantage.getImageUrl()).into(iconView);
        glide().load(advantage.getSmallImageUrl()).into(smallIconView);
        levelNameView.setText(advantage.getName());

        setupKeepingContainer(levelInfo);
        setupCouponContainer(advantage);
        setupLogContainer(levelInfo);
    }

    private void setupKeepingContainer(LevelInfo levelInfo) {
        List<String> conditions = levelInfo.getConditions();
        if (conditions.size() <= 1) {
            keepingConditionContainer.setVisibility(View.GONE);
            return;
        }

        keepingConditionLabelView.setText(conditions.get(0));
        for (String condition : conditions.subList(1, conditions.size())) {
            TextView conditionView = new TextView(this);
            conditionView.setCompoundDrawablePadding(SPACING_NORMAL);
            /*conditionView.setTypeface(TypefaceUtils.load(getAssets(), getString(R.string.font_path)));*/
            conditionView.setTextColor(ContextCompat.getColor(this, R.color.gray_7));
            conditionView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bullet_blue, 0, 0, 0);
            conditionView.setText(Html.fromHtml(condition));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = SPACING_SMALLER;
            params.leftMargin = SPACING_MICRO;
            keepingConditionItemContainer.addView(conditionView, params);
        }
    }

    private void setupCouponContainer(LevelInfo.Advantage advantage) {
        if (advantage.getZummaCouponCount() == 0 &&
                advantage.getZummaStoreCouponCount() == 0 &&
                advantage.getZummaShoppingCouponCount() == 0) {
            couponContainer.setVisibility(View.GONE);
            return;
        }

        couponItemContainer.addView(createCouponTextView(
                getString(R.string.label_level_benefit_coupon1, advantage.getZmoneyCouponAmount())));
        int count = advantage.getZummaStoreCouponCount();
        if (count > 0) {
            couponItemContainer.addView(createCouponTextView(
                    getString(R.string.label_level_benefit_coupon2, advantage.getZummaStoreCouponAmount(), count)));
        }
        count = advantage.getZummaShoppingCouponCount();
        if (count > 0) {
            couponItemContainer.addView(createCouponTextView(
                    getString(R.string.label_level_benefit_coupon3, advantage.getZummaShoppingCouponAmount(), count)));
        }
    }

    private TextView createCouponTextView(String text) {
        TextView couponView = new TextView(this);
        couponView.setCompoundDrawablePadding(SPACING_NORMAL);
        couponView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coupon_small, 0, 0, 0);
        couponView.setText(text);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = SPACING_LARGE;
        couponView.setLayoutParams(params);
        return couponView;
    }

    private void setupLogContainer(LevelInfo levelInfo) {
        if (levelInfo.getAdvantageReward() == 0) {
            logContainer.setVisibility(View.GONE);
            return;
        }

        benefitAmountView.setText(Html.fromHtml("<b>" +
                getString(R.string.format_point, levelInfo.getAdvantageReward()) + "</b>"));

        ZummaApi.user().recentCouponLogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logs -> {
                    for (LevelCouponLog log : logs) {
                        logContainer.addView(new LevelBenefitLogView(this, log), logContainer.getChildCount() - 2);
                    }
                }, ZummaApiErrorHandler::handleError);
    }

    @Override
    public String getScreenName() {
        return "내 등급 혜택";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_level_benefit;
    }

    @OnClick(R.id.labelLevelName)
    public void showLevelDialog() {
        LevelDialog.newInstance().show(getSupportFragmentManager(), "level");
    }

    @OnClick(R.id.launchZmoney)
    public void startZmoney() {
        Navigator.startZmoneyApplication(this);
    }

    @OnClick(R.id.benefitLogs)
    public void startBenefitLogsActivity() {
        Navigator.startLevelBenefitLogsActivity(this);
    }

    @OnClick(R.id.levelBenefitAll)
    public void startAllLevelBenefitActivity() {
        Navigator.startAllLevelBenefitActivity(this);
    }
}