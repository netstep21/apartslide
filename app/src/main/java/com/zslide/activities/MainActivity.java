package com.zslide.activities;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.IntentConstants;
import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.ZummaApp;
import com.zslide.data.UserManager;
import com.zslide.data.model.User;
import com.zslide.dialogs.LevelUpDialog;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.firebase.MyFirebaseInstanceIDHandler;
import com.zslide.fragments.DeliveryStoreCategoriesFragment;
import com.zslide.fragments.HomeFragment;
import com.zslide.fragments.MarketItemsFragment;
import com.zslide.fragments.VisitStoreCategoriesFragment;
import com.zslide.models.Event;
import com.zslide.network.ZummaApi;
import com.zslide.utils.BackPressFinishHandler;
import com.zslide.utils.EasySharedPreferences;
import com.zslide.utils.EventLogger;
import com.zslide.utils.ZLog;
import com.zslide.view.setting.SettingsFragment;
import com.zslide.widget.EventView;
import com.zslide.widget.ZummaTabLayout;
import com.zslide.widget.ZummaViewPager;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;


public class MainActivity extends com.zslide.view.base.BaseActivity {

    public static final int PAGE_HOME = 0;
    public static final int PAGE_DELIVERY_STORE = 1;
    public static final int PAGE_VISIT_STORE = 2;
    public static final int PAGE_ZUMMASHOPPING = 3;
    public static final int PAGE_MYPAGE = 4;

    private final Fragment[] fragments = {
            HomeFragment.newInstance(),
            DeliveryStoreCategoriesFragment.newInstance(),
            VisitStoreCategoriesFragment.newInstance(),
            MarketItemsFragment.newInstance(),
            SettingsFragment.newInstance(),
    };

    @BindView(R.id.viewPager) ZummaViewPager pager;
    @BindView(R.id.tabLayout) ZummaTabLayout tabs;

    private int[] icons = {
            R.drawable.ic_tab_home,
            R.drawable.ic_tab_delivery_store,
            R.drawable.ic_tab_visit_store,
            R.drawable.ic_tab_zummashopping,
            R.drawable.ic_tab_mypage,
    };

    private int[] names = {
            R.string.label_home,
            R.string.delivery,
            R.string.visit,
            R.string.label_zummashopping,
            R.string.label_mypage,
    };

    private BackPressFinishHandler backPressFinishHandler;
    private Set<AlertDialog> showingDialogs = new HashSet<>();

    private int startPage = PAGE_HOME;
    private User prevUser;
    private CompositeSubscription subscriptions;
    private CompositeDisposable disposables;
    private boolean loadEventPopup = false;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subscriptions = new CompositeSubscription();
        disposables = new CompositeDisposable();

        if (savedInstanceState == null) {
            startPage = getIntent().getIntExtra(IntentConstants.EXTRA_PAGE, 0);
        }

        ((MainTabItem) fragments[PAGE_HOME]).getBadgeObserver()
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(visible -> {
                    View view = tabs.getTabAt(PAGE_HOME).getCustomView();
                    ButterKnife.findById(view, R.id.badge).setVisibility(visible ? View.VISIBLE : View.GONE);
                }, ZLog::e);

        setupViewPager(pager);
        setupTabLayout(tabs);
        selectPage(startPage);
        createNotificationChannel();
        
        backPressFinishHandler = new BackPressFinishHandler(this);
        backPressFinishHandler.setBackPressAction(super::onBackPressed);
        //pager.addOnPageChangeListener(new UpButton.SimplePagerUpButtonBehaviorMixer(upButton, fragments));
        EventLogger.action(this, "enter_main");
//        getSupportFragmentManager().beginTransaction()
//                .add(android.R.id.content, HomeFragment.newInstance())
//                .commit();
    }

    private void showEventDialogs(List<Event> events) {
        Date today = new Date();
        for (Event event : events) {
            if (today.before(EasySharedPreferences.with(this)
                    .getObject(EventView.PREF_DISABLE_SHOWING_DATE + "-" + event.getId(),
                            today, Date.class))) {
                continue;
            }
            EventView eventView = new EventView(this);
            eventView.setEvent(event);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.EventDialogTheme);
            dialogBuilder.setView(eventView);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            dialog.getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.dialog_event_width), WindowManager.LayoutParams.WRAP_CONTENT);
            showingDialogs.add(dialog);

            eventView.setButtonClickListener(v -> {
                if (v.getId() == R.id.close) {
                    dialog.dismiss();
                    showingDialogs.remove(dialog);
                }
            });
            eventView.setDisableSettingChangeListener(isDisable -> setDisableEvent(event, isDisable));
        }
    }

    private void createNotificationChannel() {
        // 오레오 업데이트 후 체널 생성하는 부분. 안드로이드 문서에서 26버전 체크로 앱 실행시에 만드는 것을 추천
        // 알림 설정에 대한 중요도는 일단 최상으로 넣어놓음
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = getString(R.string.base_channel_name);
            String description = getString(R.string.base_channel_description);
            NotificationChannel channelMessage = new NotificationChannel(getString(R.string.base_channel_id), name, android.app.NotificationManager.IMPORTANCE_DEFAULT);
            channelMessage.setDescription(description);
            channelMessage.enableLights(true);
            channelMessage.setLightColor(Color.GREEN);
            channelMessage.enableVibration(true);
            channelMessage.setVibrationPattern(new long[]{100, 200, 100, 200});
            channelMessage.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channelMessage.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channelMessage);
        }
    }


    private void setDisableEvent(Event event, boolean isDisable) {
        Calendar disableDate = Calendar.getInstance();
        if (isDisable) {
            switch (event.getType()) {
                case Event.TYPE_NEVER_SHOWING:
                    disableDate.add(Calendar.DATE, 365);
                    break;
                case Event.TYPE_TODAY_NOT_SHOWING:
                    disableDate.add(Calendar.DATE, 1);
                    break;
                case Event.TYPE_WEEK_NOT_SHOWING:
                    disableDate.add(Calendar.DATE, 7);
                    break;
            }
        }
        EasySharedPreferences.with(MainActivity.this)
                .putObject(EventView.PREF_DISABLE_SHOWING_DATE + "-" + event.getId(),
                        disableDate.getTime());
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyFirebaseInstanceIDHandler.sendGcmInfoIfNeeded(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!loadEventPopup) {
            loadEventPopup = true;
            loadEventPopup();
        }

        disposables.add(UserManager.getInstance()
                .getUserObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateUser));

        // TODO: 2018. 1. 8. sync user zmoney data
    }

    private void loadEventPopup() {
        subscriptions.add(ZummaApi.event().popups()
                .subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(this::showEventDialogs, this::handleError));
    }

    @Override
    protected void onPause() {
        if (subscriptions != null) {
            subscriptions.clear();
        }

        if (disposables != null) {
            disposables.clear();
        }
        super.onPause();
    }

    private void updateUser(User user) {
        this.prevUser = user;
        checkUserBanished(user);
        checkLevelUp(user);
        ZummaApp.get(this).activateBuzzScreenIfNeeded();
    }

    private void checkUserBanished(User user) {
        if (user.isBanished()) {
            UserManager.getInstance().checkNotifiedBan()
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(notified -> !notified)
                    .subscribe(__ -> {
                        UserManager.getInstance().setNotifiedBan(true)
                                .subscribeOn(Schedulers.io())
                                .subscribe();

                        SimpleAlertDialog.newInstance("",
                                //getString(R.string.message_family_registration_blocked, user.getFamily().getName()),
                                getString(R.string.message_family_registration_blocked, ""),
                                getString(R.string.label_registration_address), "", true)
                                .setOnConfirmListener(() -> Navigator.startFamilyRegistrationActivity(this))
                                .show(getSupportFragmentManager(), "alert");
                    });
        }
    }

    private void setupViewPager(ZummaViewPager viewPager) {
        MainPageAdapter adapter = new MainPageAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setAdapter(adapter);
    }

    private void setupTabLayout(TabLayout tabs) {
        tabs.setupWithViewPager(pager);
        tabs.setSelectedTabIndicatorHeight(0);
        for (int i = 0; i < tabs.getTabCount(); i++) {
            TabLayout.Tab tab = tabs.getTabAt(i);
            //noinspection ConstantConditions
            tab.setCustomView(R.layout.view_main_tab);
            View customView = tab.getCustomView();
            if (customView != null) {
                ImageView iconView = ButterKnife.findById(customView, R.id.icon);
                iconView.setImageResource(icons[i]);
                TextView nameView = ButterKnife.findById(customView, R.id.name);
                nameView.setText(names[i]);
            }
        }
    }

    public void selectPage(int page) {
        if (page >= tabs.getTabCount()) {
            return;
        }

        TabLayout.Tab tab = tabs.getTabAt(page);
        if (tab != null) {
            tab.select();
        }
    }

    private void checkLevelUp(User user) {
        if (prevUser != null) {
            if (prevUser.getLevelInfo() == null || user.getLevelInfo() == null) {
                return;
            }

            if (prevUser.getLevelInfo().getAdvantage() == null ||
                    user.getLevelInfo().getAdvantage() == null) {
                return;
            }

            if (prevUser.getLevelInfo().getAdvantage().getPriority() <
                    user.getLevelInfo().getAdvantage().getPriority()) {
                showLevelUpDialog();
            }
        }
    }

    public void showLevelUpDialog() {
        Fragment levelUpDialog = getSupportFragmentManager().findFragmentByTag("level_up");
        if (levelUpDialog == null || !levelUpDialog.isAdded()) {
            LevelUpDialog.newInstance().show(getSupportFragmentManager(), "level_up");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(IntentConstants.EXTRA_PAGE, pager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectPage(savedInstanceState.getInt(IntentConstants.EXTRA_PAGE, 0));
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        backPressFinishHandler.onBackPressed();
    }

    public interface MainTabItem {
        Observable<Boolean> getBadgeObserver();
    }

    class MainPageAdapter extends FragmentPagerAdapter {

        public MainPageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

}
