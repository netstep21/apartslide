package com.zslide.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.text.Layout;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zslide.Config;
import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.activities.InviteActivity;
import com.zslide.activities.MainActivity;
import com.zslide.data.UserManager;
import com.zslide.data.model.EventBanner;
import com.zslide.data.model.Family;
import com.zslide.data.model.HomeZmoney;
import com.zslide.data.model.Payments;
import com.zslide.data.model.PaymentsState;
import com.zslide.data.model.User;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.models.TempApartment;
import com.zslide.network.ZummaApi;
import com.zslide.utils.DLog;
import com.zslide.utils.DeepLinkRouter;
import com.zslide.utils.EasySharedPreferences;
import com.zslide.widget.AlertView;
import com.zslide.widget.CircularSeekBar;
import com.bumptech.glide.Glide;
import com.github.amlcurran.showcaseview.ShowcaseDrawer;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.Calendar;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by chulwoo on 15. 8. 28..
 */
public class HomeFragment extends com.zslide.view.base.BaseFragment implements MainActivity.MainTabItem {

    private static final String KEY_ZMONEY_LEARNED_USER = "zmoney_learned_user";
    private static final String KEY_LATEST_ALERT_TIME = "HomeFragment.latestAlertTime";

    @BindColor(R.color.black) int SHOWCASE_TEXT_COLOR;
    @BindDimen(R.dimen.font_large) int SHOWCASE_TEXT_SIZE;

    @BindDimen(R.dimen.spacing_large) int EVENT_MARGIN;
    @BindDimen(R.dimen.event_banner_height) int EVENT_BANNER_HEIGHT;

    @BindDimen(R.dimen.event_visible_height) int EVENT_VISIBLE_HEIGHT;
    @BindDimen(R.dimen.tab_height) int TAB_HEIGHT;

    @BindView(R.id.container) CoordinatorLayout container;
    @BindView(R.id.appbar) AppBarLayout appbar;
    @BindView(R.id.zmoneyContainer) ViewGroup zmoneyContainer;
    @BindView(R.id.month) TextView monthView;
    @BindView(R.id.zmoney) TextView zmoneyView;
    @BindView(R.id.totalZmoney) TextView totalZmoneyView;
    @BindView(R.id.zmoneyProgress) CircularSeekBar zmoneyProgressView;
    @BindView(R.id.recentPaymentsContainer) View recentPaymentsContainer;
    @BindView(R.id.recentPaymentsZmoneyLabel) TextView recentPaymentsZmoneyLabelView;
    @BindView(R.id.recentPaymentsZmoney) TextView recentPaymentsZmoneyView;
    @BindView(R.id.recentPaymentsAlertContainer) ViewGroup recentPaymentsAlertContainer;
    @BindView(R.id.recentPaymentsAlert) TextView recentPaymentsAlertView;
    @BindView(R.id.scroll) NestedScrollView scrollView;
    @BindView(R.id.homeContainer) LinearLayout homeContainer;
    @BindView(R.id.invite) ImageView inviteView;
    @BindView(R.id.eventLabelContainer) ViewGroup eventLabelContainer;
    @BindView(R.id.eventCount) TextView eventCountView;
    @BindView(R.id.eventContainer) LinearLayout eventContainer;

    private static final int TARGET_EXPECTED_PAYMENTS = 0;
    private static final int TARGET_REGISTRATION_FAMILY = 1;
    private static final int TARGET_REGISTRATION_ACCOUNT = 2;
    private static final int TARGET_TEMP_APARTMENT_WAIT = 3;
    private static final int TARGET_TEMP_APARTMENT_COMPLETE = 4;

    private float prevAnimatedPercentage;
    private int prevAnimatedFamilyZmoney = 0;
    private int prevAnimatedZmoney = 0;
    private int prevAnimatedTotalZmoney = 0;
    private int prevAnimatedPayments = 0;

    private ShowcaseView zmoneyShowcaseView;
    private ValueAnimator zmoneyTextAnimator;
    private ValueAnimator totalZmoneyTextAnimator;
    private ObjectAnimator zmoneyProgressAnimator;
    private ValueAnimator recentPaymentsAnimator;

    private AlertView alertView;
    private BehaviorSubject<Boolean> badgeSubject = BehaviorSubject.create();

    private boolean isFirstResume = true;
    private CompositeSubscription subscriptions;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_home;
    }


    @OnClick(R.id.delivery)
    public void startDeliveryInfoPage() {
        Navigator.startLockerServiceActivity(getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupZmoney();
        setupInviteView();
    }

    public void updatePayments(Payments payments) {
        if (payments == null) {
            return;
        }

        if (payments.isCompletePayments()) {
            recentPaymentsZmoneyLabelView.setText(R.string.payments_price);
        } else {
            recentPaymentsZmoneyLabelView.setText(R.string.label_expected_zmoney);
        }

        PaymentsState paymentsState = payments.getState();
        if (paymentsState != null && PaymentsState.WRONG_ADDRESS.equals(paymentsState)) {
            recentPaymentsAlertView.setText(R.string.message_error_expected_payments5);
            recentPaymentsAlertContainer.setVisibility(View.VISIBLE);
            recentPaymentsContainer.setTag(TARGET_TEMP_APARTMENT_COMPLETE);
        }

        animateZmomneyPayments(payments);
    }

    public void updateFamily(Family family) {
        if (family.isNull()) {
            recentPaymentsZmoneyView.setVisibility(View.GONE);
            recentPaymentsAlertView.setText(R.string.message_error_expected_payments1);
            recentPaymentsAlertContainer.setVisibility(View.VISIBLE);
            recentPaymentsContainer.setTag(TARGET_REGISTRATION_FAMILY);
        } else {
            if (family.hasTempApartment()) {
                switch (family.getTempApartment().getStatus()) {
                    case TempApartment.STATUS_WAIT:
                        recentPaymentsAlertView.setText(R.string.message_error_expected_payments3);
                        recentPaymentsAlertContainer.setVisibility(View.VISIBLE);
                        recentPaymentsContainer.setTag(TARGET_TEMP_APARTMENT_WAIT);
                        break;
                    case TempApartment.STATUS_SUCCESS:
                        recentPaymentsAlertView.setText(R.string.message_error_expected_payments4);
                        recentPaymentsAlertContainer.setVisibility(View.VISIBLE);
                        recentPaymentsContainer.setTag(TARGET_TEMP_APARTMENT_COMPLETE);
                        break;
                    case TempApartment.STATUS_FAILURE:
                        recentPaymentsAlertView.setText(R.string.message_error_expected_payments4);
                        recentPaymentsAlertContainer.setVisibility(View.VISIBLE);
                        recentPaymentsContainer.setTag(TARGET_TEMP_APARTMENT_COMPLETE);
                        break;
                }
            } else {
                if (!family.getApartment().isJoined() && !family.hasAccount()) {
                    recentPaymentsAlertView.setText(R.string.message_error_expected_payments2);
                    recentPaymentsAlertContainer.setVisibility(View.VISIBLE);
                    recentPaymentsContainer.setTag(TARGET_EXPECTED_PAYMENTS);
                } else {
                    recentPaymentsZmoneyView.setVisibility(View.VISIBLE);
                    recentPaymentsAlertContainer.setVisibility(View.GONE);
                    recentPaymentsContainer.setTag(TARGET_EXPECTED_PAYMENTS);
                }
            }
        }

        updatePayments(family.getPayments());

        showFamilyAlertViewIfNeeded(UserManager.getInstance().getFamilyValue());
    }


    @Override
    public Observable<Boolean> getBadgeObserver() {
        return badgeSubject;
    }

    /*
    public void bind(User user, FamilyZmoney familyZmoney) {
        //zummacastNewBadgeView.setVisibility(user.hasNewCast() ? View.VISIBLE : View.GONE);
        notifyBadgeStatusIfNeeded();
        //setStampCards(user.getStampCards());
        showAlertViewIfNeeded(user);
    }*/

    private void showFamilyAlertViewIfNeeded(Family family) {
        if (zmoneyShowcaseView != null || getContext() == null) {
            return;
        }
        long latestAlertTime = EasySharedPreferences.with(getContext()).getLong(KEY_LATEST_ALERT_TIME);
        long aDay = (Config.DEBUG) ? 10 * 1000L : 24 * 60 * 60 * 1000L;
        if (System.currentTimeMillis() - latestAlertTime >= aDay && alertView == null) {
            if (family.isNull()) {
                showFamilyAlertView();
            }
        }
    }

    private void showFamilyAlertView() {
        alertView = AlertView.forFamily(getContext());
        alertView.setOnDismissListener(force -> {
            alertView = null;
            if (force) {
                EasySharedPreferences.with(getContext())
                        .putLong(KEY_LATEST_ALERT_TIME, System.currentTimeMillis());
            }
        });
        container.addView(alertView);
    }

    @Override
    public void onResume() {
        super.onResume();

        bind(UserManager.getInstance().fetchHomeZmoney().subscribe());

        if (isFirstResume) {
            isFirstResume = false;
            loadingImmutableData();
        }

        bind(UserManager.getInstance().getFamilyObservable()
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(this::updateFamily, this::handleError));

        bind(UserManager.getInstance().getZmoneysObservable()
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(this::animateZmoney, this::handleError));

        boolean isZmoneyLearnedUser = EasySharedPreferences.with(getActivity()).getBoolean(KEY_ZMONEY_LEARNED_USER);
        if (!isZmoneyLearnedUser && zmoneyShowcaseView == null) {
            showZmoneyShowcaseView();
        }
    }

    private void loadingImmutableData() {
        subscriptions.add(ZummaApi.event().items()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(banners -> {
                    // todo: recyclerview로 교체
                    eventCountView.setText(Html.fromHtml("<b>" + banners.size() + "</b>"));
                    for (EventBanner banner : banners) {
                        ImageView imageView = new ImageView(getContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, EVENT_BANNER_HEIGHT);
                        params.bottomMargin = EVENT_MARGIN;
                        imageView.setLayoutParams(params);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        imageView.setOnClickListener(v ->
                                DeepLinkRouter.route(getContext(), Uri.parse(banner.getTarget()), false));
                        int[] attrs = new int[]{R.attr.selectableItemBackground};
                        TypedArray typedArray = getContext().obtainStyledAttributes(attrs);
                        int backgroundResource = typedArray.getResourceId(0, 0);
                        imageView.setBackgroundResource(backgroundResource);
                        typedArray.recycle();
                        eventContainer.addView(imageView);

                        Glide.with(this).load(banner.getImageUrl()).into(imageView);
                    }
                }, this::handleError));
    }

    private void showZmoneyShowcaseView() {
        if (alertView != null) {
            alertView.dismiss();
        }

        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(SHOWCASE_TEXT_SIZE);
        paint.setColor(SHOWCASE_TEXT_COLOR);
        /*paint.setTypeface(TypefaceUtils.load(getActivity().getAssets(), getString(R.string.font_path)));*/
        zmoneyShowcaseView = new ShowcaseView.Builder(getActivity())
                .setTarget(new ViewTarget(R.id.zmoneyContainer, getActivity()))
                .setContentText("\n눌러보세요!\n내가 모은 줌머니 내역이 나타납니다.")
                .setContentTextPaint(paint)
                .hideOnTouchOutside()
                .setShowcaseDrawer(new ZmoneyShowcaseView(getResources()))
                .replaceEndButton(R.layout.btn_transparent)
                .build();
        zmoneyShowcaseView.setDetailTextAlignment(Layout.Alignment.ALIGN_CENTER);
        zmoneyShowcaseView.forceTextPosition(ShowcaseView.BELOW_SHOWCASE);
        zmoneyShowcaseView.setOnShowcaseEventListener(new SimpleShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                if (zmoneyShowcaseView != null) {
                    EasySharedPreferences.with(getActivity()).putBoolean(KEY_ZMONEY_LEARNED_USER, true);
                    zmoneyShowcaseView = null;
                }

                showFamilyAlertViewIfNeeded(UserManager.getInstance().getFamilyValue());
            }
        });
    }

    protected void setupZmoney() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        monthView.setText(getString(R.string.label_home_month, month));
        zmoneyProgressView.setIsTouchEnabled(false);
    }

    protected void animateZmoney(HomeZmoney zmoney) {
        if (!isAdded()) {
            return;
        }

        if (zmoneyProgressAnimator != null) {
            zmoneyProgressAnimator.cancel();
        }

        int userZmoney = zmoney.getUserZmoney();
        int familyZmoney = zmoney.getFamilyZmoney();
        float startPercentage = prevAnimatedPercentage * prevAnimatedFamilyZmoney / ((familyZmoney == 0) ? 1.0f : familyZmoney);
        float currentPercentage = userZmoney / ((familyZmoney == 0) ? 1.0f : familyZmoney);
        zmoneyProgressView.setMax(10000);
        zmoneyProgressAnimator = ObjectAnimator.ofInt(zmoneyProgressView, "progress",
                (int) (startPercentage * 10000), (int) (currentPercentage * 10000));
        zmoneyProgressAnimator.setDuration(1000);
        zmoneyProgressAnimator.setInterpolator(new LinearInterpolator());
        zmoneyProgressAnimator.addUpdateListener(valueAnimator -> {
            String value = valueAnimator.getAnimatedValue().toString();
            prevAnimatedPercentage = Integer.parseInt(value) / 10000.0f;
            prevAnimatedFamilyZmoney = familyZmoney;
        });

        zmoneyProgressAnimator.start();
        animateText(zmoney);
    }


    protected void animateText(HomeZmoney homeZmoney) {
        if (!isAdded()) {
            return;
        }

        if (zmoneyTextAnimator != null) {
            zmoneyTextAnimator.cancel();
        }

        zmoneyTextAnimator = ValueAnimator.ofInt(prevAnimatedZmoney, homeZmoney.getFamilyZmoney());
        zmoneyTextAnimator.setDuration(1000);
        zmoneyTextAnimator.setInterpolator(new LinearInterpolator());

        zmoneyTextAnimator.addUpdateListener(valueAnimator -> {
            if (isAdded()) {
                String value = valueAnimator.getAnimatedValue().toString();
                int zmoney = Integer.parseInt(value);
                zmoneyView.setText(getString(R.string.format_number, zmoney));
                prevAnimatedZmoney = zmoney;
            }
        });
        zmoneyTextAnimator.start();

        if (totalZmoneyTextAnimator != null) {
            totalZmoneyTextAnimator.cancel();
        }

        totalZmoneyTextAnimator = ValueAnimator.ofInt(prevAnimatedTotalZmoney, homeZmoney.getTotalZmoney());
        totalZmoneyTextAnimator.setDuration(1000);
        totalZmoneyTextAnimator.setInterpolator(new LinearInterpolator());
        totalZmoneyTextAnimator.addUpdateListener(valueAnimator -> {
            if (isAdded()) {
                String value = valueAnimator.getAnimatedValue().toString();
                int zmoney = Integer.parseInt(value);
                totalZmoneyView.setText(getString(R.string.format_total_zmoney, zmoney));
                prevAnimatedTotalZmoney = zmoney;
            }
        });
        totalZmoneyTextAnimator.start();
    }

    protected void animateZmomneyPayments(Payments payments) {
        if (!isAdded()) {
            return;
        }

        int recentPayments = 0;
        if (payments != null) {
            recentPayments = payments.getRecentPayments();
        }

        if (recentPaymentsAnimator != null) {
            recentPaymentsAnimator.cancel();
        }
        recentPaymentsAnimator = ValueAnimator.ofInt(prevAnimatedPayments, recentPayments);
        recentPaymentsAnimator.setDuration(1000);
        recentPaymentsAnimator.setInterpolator(new LinearInterpolator());
        recentPaymentsAnimator.addUpdateListener(valueAnimator -> {
            if (isAdded()) {
                String value = valueAnimator.getAnimatedValue().toString();
                int money = Integer.parseInt(value);
                recentPaymentsZmoneyView.setText(getString(R.string.format_price, money));
                prevAnimatedPayments = money;
            }
        });
        recentPaymentsAnimator.start();
    }

    private void setupInviteView() {
        ZummaApi.general().friendInviteBannerUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(url -> Glide.with(this).load(url).into(inviteView), DLog::e);
    }

    @OnClick(R.id.invite)
    public void invite() {
        Navigator.startInviteActivity(getActivity(), InviteActivity.TYPE_FRIEND);
    }

//    @OnClick(R.id.settings)
//    public void startSettings() {
//        startActivity(new Intent(getActivity(), SettingsActivity.class));
//    }

    @OnClick(R.id.zmoneyContainer)
    public void startZmoneyActivity() {
        if (zmoneyShowcaseView != null) {
            zmoneyShowcaseView.hide();

        }
        Navigator.startZmoneyActivity(getActivity());
    }

    @OnClick(R.id.recentPaymentsContainer)
    public void checkCanPayments(View view) {
        int target = (int) view.getTag();
        switch (target) {
            case TARGET_EXPECTED_PAYMENTS:
                Navigator.startZmoneyPaymentsActivity(getActivity());
                break;
            case TARGET_REGISTRATION_FAMILY:
                Navigator.startFamilyRegistrationActivity(getActivity());
                break;
            case TARGET_REGISTRATION_ACCOUNT:
                Navigator.startZmoneyPaymentsActivity(getActivity());
                break;
            case TARGET_TEMP_APARTMENT_WAIT:
                SimpleAlertDialog.newInstance("제보하신 아파트는 2영업일 이내 처리됩니다.\n처리 완료 푸시를 받으시면, 주소를 꼭 다시 입력해주세요.")
                        .show(getActivity().getSupportFragmentManager(), "wait");
                break;
            case TARGET_TEMP_APARTMENT_COMPLETE:
                User user = UserManager.getInstance().getUserValue();
                Family family = UserManager.getInstance().getFamilyValue();
                if (family.isFamilyLeader(user)) {
                    Navigator.startTempApartmentCompleteActivity(getActivity());
                } else {
                    User leader = family.getLeader();
                    String message = getString(R.string.message_address_alert,
                            leader.getBlurredName(getContext()), leader.getBlurredPhoneNumber(true));
                    SimpleAlertDialog.newInstance(message)
                            .show(getActivity().getSupportFragmentManager(), "alert");
                }
                break;
        }
    }

    @OnClick(R.id.eventLabelContainer)
    public void scrollToEventContainer() {
        Navigator.startEventsActivity(getActivity());
    }

    private void notifyBadgeStatusIfNeeded() {
        //TODO: boolean current = user.hasNewCast();
        boolean current = false;
        if (badgeSubject.getValue() == null ||
                badgeSubject.getValue() != current) {
            badgeSubject.onNext(current);
        }
    }

    private static abstract class CustomShowcaseView implements ShowcaseDrawer {

        protected final float radius;
        protected final Paint eraserPaint;
        protected final Paint basicPaint;
        protected final int backgroundColor;

        public CustomShowcaseView(Resources res, float radius) {
            this.radius = radius;
            this.eraserPaint = new Paint();
            this.eraserPaint.setColor(0xFFFFFF);
            this.eraserPaint.setAlpha(0);
            this.eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            this.eraserPaint.setAntiAlias(true);
            this.basicPaint = new Paint();
            this.backgroundColor = res.getColor(R.color.bg_showcase);
        }

        @Override
        public void setShowcaseColour(int color) {
            // no-op
        }

        @Override
        public int getShowcaseWidth() {
            return (int) (radius * 2);
        }

        @Override
        public int getShowcaseHeight() {
            return (int) (radius * 2);
        }

        @Override
        public float getBlockedRadius() {
            return radius;
        }

        @Override
        public void setBackgroundColour(int backgroundColor) {
            // do nothing
        }

        @Override
        public void erase(Bitmap bitmapBuffer) {
            bitmapBuffer.eraseColor(backgroundColor);
        }

        @Override
        public void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer) {
            canvas.drawBitmap(bitmapBuffer, 0, 0, basicPaint);
        }
    }

    private class ZmoneyShowcaseView extends CustomShowcaseView {

        private final Drawable finger;
        private final int fingerMargin;

        public ZmoneyShowcaseView(Resources res) {
            super(res, res.getDimensionPixelSize(R.dimen.zmoney_circle_size) / 2
                    + res.getDimensionPixelSize(R.dimen.zmoney_circle_stroke_width));
            this.finger = res.getDrawable(R.drawable.img_finger_pointer);
            this.fingerMargin = res.getDimensionPixelSize(R.dimen.zmoney_showcase_finger_margin);
        }

        @Override
        public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier) {
            Canvas bufferCanvas = new Canvas(buffer);
            bufferCanvas.drawCircle(x, y, radius, eraserPaint);

            int left = (int) x - finger.getIntrinsicWidth() / 2;
            int top = (int) (y + radius) + fingerMargin;
            finger.setBounds(left, top,
                    left + finger.getIntrinsicWidth(),
                    top + finger.getIntrinsicHeight());
            finger.draw(bufferCanvas);
        }
    }
}
