package com.zslide.view.main;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.data.MetaDataManager;
import com.zslide.data.UserManager;
import com.zslide.data.model.HomeZmoney;
import com.zslide.data.model.Payments;
import com.zslide.utils.RxUtil;
import com.zslide.widget.CircularSeekBar;
import com.zslide.widget.LinearSpacingItemDecoration;
import com.zslide.view.base.BaseFragment;
import com.zslide.view.main.adapter.EventBannerAdapter;
import com.bumptech.glide.Glide;
import com.github.amlcurran.showcaseview.ShowcaseView;

import org.threeten.bp.LocalDateTime;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public class MainFragment extends BaseFragment {

    @BindColor(R.color.black) int showcaseTextColor;

    @BindDimen(R.dimen.spacing_normal) int eventMargin;
    @BindDimen(R.dimen.font_normal) int showcaseTextSize;

    @BindView(R.id.container) CoordinatorLayout container;
    @BindView(R.id.familyRegistrationGuide) View familyRegistrationGuideView;
    @BindView(R.id.appbar) AppBarLayout appbar;
    @BindView(R.id.zmoneyContainer) ViewGroup zmoneyContainer;
    @BindView(R.id.month) TextView monthView;
    @BindView(R.id.zmoney) TextView zmoneyView;
    @BindView(R.id.zmoneyProgress) CircularSeekBar zmoneyProgressView;
    @BindView(R.id.recentPaymentsContainer) View recentPaymentsContainer;
    @BindView(R.id.recentPaymentsZmoneyLabel) TextView recentPaymentsZmoneyLabelView;
    @BindView(R.id.recentPaymentsZmoney) TextView recentPaymentsZmoneyView;
    @BindView(R.id.recentPaymentsAlertContainer) ViewGroup recentPaymentsAlertContainer;
    @BindView(R.id.recentPaymentsAlert) TextView recentPaymentsAlertView;
    @BindView(R.id.scroll) NestedScrollView scrollView;
    @BindView(R.id.homeContainer) LinearLayout homeContainer;
    @BindView(R.id.eventLabelContainer) ViewGroup eventLabelContainer;
    @BindView(R.id.eventCount) TextView eventCountView;
    @BindView(R.id.events) RecyclerView eventsView;

    private static final int TARGET_EXPECTED_PAYMENTS = 0;
    private static final int TARGET_REGISTRATION_FAMILY = 1;
    private static final int TARGET_REGISTRATION_ACCOUNT = 2;
    private static final int TARGET_TEMP_APARTMENT_WAIT = 3;
    private static final int TARGET_TEMP_APARTMENT_COMPLETE = 4;

    private float prevAnimatedPercentage;
    private int prevAnimatedFamilyZmoney = 0;
    private int prevAnimatedZmoney = 0;
    private int prevAnimatedPayments = 0;

    private ShowcaseView zmoneyShowcaseView;

    private ObjectAnimator zmoneyProgressAnimator;
    private ValueAnimator zmoneyTextAnimator;
    private ValueAnimator recentPaymentsAnimator;

    private EventBannerAdapter eventAdapter;

    private MainContract.ViewModel viewModel;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new MainViewModel(
                new MainNavigator((AppCompatActivity) getActivity()),
                MetaDataManager.getInstance(),
                UserManager.getInstance());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = view.getContext();

        int month = LocalDateTime.now().getMonthValue();
        monthView.setText(getString(R.string.label_home_month, month));
        zmoneyProgressView.setIsTouchEnabled(false);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        eventAdapter = new EventBannerAdapter(Glide.with(this));
        eventsView.addItemDecoration(new LinearSpacingItemDecoration(eventMargin));
        eventsView.setLayoutManager(llm);
        eventsView.setNestedScrollingEnabled(false);
        eventsView.setAdapter(eventAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        bind(viewModel.refreshZmoney()
                .subscribeOn(Schedulers.io())
                .subscribe(RxUtil::doNothing, RxUtil::doNothing));

        bind(viewModel.getHomeZmoneyObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateZmoneyUi, this::handleError));

        bind(viewModel.getHomeUiModelObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateHomeUi, this::handleError));

        bind(viewModel.getEventBannerItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showEvents, this::handleError));

        /*bind(Observable.combineLatest(
                viewModel.needShowZmoneyTutorialStream(),
                viewModel.needShowFamilyRegistrationGuide(),
                (n1, n2) -> !n1 && !n2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(needShowFamilyRegistrationGuide -> {
                    if (needShowFamilyRegistrationGuide) {
                        showFamilyRegistrationGuide();
                    } else {
                        hideFamilyRegistrationGuide();
                    }
                }, this::handleError));

        bind(viewModel.needShowZmoneyTutorialStream()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(need -> (need && !isVisibleShowcase()))
                .subscribe(need -> showZmoneyTutorial(), this::handleError));*/
    }

    private void updateZmoneyUi(HomeZmoney homeZmoney) {
        animateZmoneyCircle(homeZmoney.getUserZmoney(), homeZmoney.getFamilyZmoney());
        animateZmoneyText(homeZmoney.getFamilyZmoney());
    }

    private void updateHomeUi(MainUiModel model) {
        // inviteRewardView.setText(getString(R.string.format_invite_reward, model.getInviteReward()));

        if (model.isRecentPaymentsLabelVisibility()) {
            recentPaymentsZmoneyLabelView.setVisibility(View.VISIBLE);
            recentPaymentsZmoneyLabelView.setText(model.getRecentPaymentsLabel());
        } else {
            recentPaymentsZmoneyLabelView.setVisibility(View.GONE);
        }

        if (model.isRecentPaymentsAlertVisibility()) {
            recentPaymentsAlertContainer.setVisibility(View.VISIBLE);
            recentPaymentsAlertView.setText(model.getRecentPaymentsAlert());
        } else {
            recentPaymentsAlertContainer.setVisibility(View.GONE);
        }

        recentPaymentsContainer.setOnClickListener(v -> {
            try {
                model.getOnRecentPaymentsClickAction().run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (model.getPayments() != null) {
            animateZmoneyPayments(model.getPayments());
        }
    }

    private void showEvents(List<EventBannerItem> eventBannerItems) {
        eventCountView.setText(Html.fromHtml("<b>" + eventBannerItems.size() + "</b>"));
        eventAdapter.setEventBannerItems(eventBannerItems);
    }

    protected void animateZmoneyCircle(int userZmoney, int familyZmoney) {
        if (zmoneyProgressAnimator != null) {
            zmoneyProgressAnimator.cancel();
        }

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
    }

    protected void animateZmoneyText(int familyZmoney) {
        if (zmoneyTextAnimator != null) {
            zmoneyTextAnimator.cancel();
        }

        zmoneyTextAnimator = ValueAnimator.ofInt(prevAnimatedZmoney, familyZmoney);
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
    }

    protected void animateZmoneyPayments(Payments payments) {
        int recentPayments = payments.getRecentPayments();

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

    @OnClick(R.id.settings)
    public void onSettingsClick() {
        viewModel.onSettingsClicked();
    }

    @OnClick(R.id.zmoneyContainer)
    public void startZmoneyActivity() {
        // TODO: viewmodel에서
        if (zmoneyShowcaseView != null) {
            zmoneyShowcaseView.hide();
        }

        Navigator.startZmoneyActivity(getActivity());
    }

    /*@OnClick(R.id.invite)
    public void startInviteActivity() {
        Navigator.startInviteActivity(getActivity(), InviteActivity.TYPE_FRIEND);
    }
*/
    /*public void showZmoneyTutorial() {
        Activity activity = getActivity();
        Context context = getActivity().getApplicationContext();
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(showcaseTextSize);
        paint.setColor(showcaseTextColor);
        *//*paint.setTypeface(TypefaceUtils.load(getActivity().getAssets(), getString(R.string.font_path)));*//*
        zmoneyShowcaseView = new ShowcaseView.Builder(activity)
                .setTarget(new ViewTarget(R.id.zmoneyContainer, activity))
                .setContentText("\n눌러보세요!\n내가 모은 줌머니 내역이 나타납니다.")
                .setContentTextPaint(paint)
                .hideOnTouchOutside()
                .setShowcaseDrawer(new ZmoneyShowcaseView(context))
                .replaceEndButton(R.layout.btn_transparent)
                .build();

        zmoneyShowcaseView.setDetailTextAlignment(Layout.Alignment.ALIGN_CENTER);
        zmoneyShowcaseView.forceTextPosition(ShowcaseView.BELOW_SHOWCASE);
        zmoneyShowcaseView.setOnShowcaseEventListener(new SimpleShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                zmoneyShowcaseView = null;
                viewModel.completeZmoneyTutorial().subscribe(() ->
                        DLog.i("complete zmoney tutorial"), throwable -> HomeFragment.this.handleError(throwable));
            }
        });
    }*/

    public boolean isVisibleShowcase() {
        return zmoneyShowcaseView != null;
    }

    @OnClick(R.id.familyRegistration)
    public void registrationFamily() {
        // todo : viewmodel에서
        Navigator.startFamilyRegistrationActivity(getActivity());
    }

    @OnClick(R.id.closeFamilyAlert)
    public void closeFamilyAlertView() {
        familyRegistrationGuideView.setVisibility(View.GONE);
    }

    public void showFamilyRegistrationGuide() {
        familyRegistrationGuideView.setVisibility(View.VISIBLE);
    }

    public void hideFamilyRegistrationGuide() {
        familyRegistrationGuideView.setVisibility(View.GONE);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_main;
    }
}
