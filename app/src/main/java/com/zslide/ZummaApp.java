package com.zslide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.zslide.activities.BaseActivity;
import com.zslide.activities.MainActivity;
import com.zslide.data.AuthenticationManager;
import com.zslide.data.MetaDataManager;
import com.zslide.data.UserManager;
import com.zslide.data.model.Address;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.models.Notice;
import com.zslide.models.Sex;
import com.zslide.network.ZummaApi;
import com.zslide.utils.DLog;
import com.zslide.utils.EasySharedPreferences;
import com.zslide.utils.PhoneNumberUtil;
import com.zslide.utils.ZLog;
import com.zslide.view.lock.CustomLockerActivity;
import com.buzzvil.buzzscreen.sdk.BuzzScreen;
import com.buzzvil.buzzscreen.sdk.LockerServiceNotificationConfig;
import com.buzzvil.buzzscreen.sdk.UserProfile;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.squareup.leakcanary.LeakCanary;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.UUID;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ZummaApp extends android.support.multidex.MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    public static final String KEY_BUZZ_SCREEN = "556697304984992";

    public static final int FLAG_ENABLED_NEVER = 0;
    public static final int FLAG_ENABLED_TWO_HOUR = 1;
    public static final int FLAG_ENABLED_FOUR_HOUR = 2;
    public static final int FLAG_ENABLED_EIGHT_HOUR = 3;
    public static final int FLAG_ENABLED_A_DAY = 4;

    private static final String SETTING_PREFS_NAME = "Settings";
    private static final String KEY_LOCK_RESTART_FLAG = "restart_flag";
    private static final String KEY_LOCK_DISABLED_TIME = "disabled_time";
    private static final String KEY_LOCK_ENABLED = "lock_enabled";
    private static final String KEY_LOCK_PUSH_ENABLED = "lock_push_enabled";
    private static final String KEY_LATEST_NOTICE = "latest_notices";
    private static final String KEY_LAST_READ_NOTICE_DATE = "last_read_notice_date";
    private static final String KEY_NEVER_SHOWING_DIALOG = "never_showing";
    private static final String KEY_UUID = "key_uuid";

    private static final int AD_DOWNLOAD_REQUEST_CODE = 7001;
    /**
     * 기본 광고 이미지 크기 720 * 1230
     */
    private static final int DEFAULT_AD_IMAGE_WIDTH = 720;
    private static final int DEFAULT_AD_IMAGE_HEIGHT = 1230;
    private static WeakReference<AppCompatActivity> activityRef;
    private EasySharedPreferences settingPrefs;

    public static ZummaApp get(Context context) {
        return (ZummaApp) context.getApplicationContext();
    }

    public static AppCompatActivity getCurrentActivity() {
        return activityRef == null ? null : activityRef.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        LeakCanary.install(this);
        AndroidThreeTen.init(this);

        settingPrefs = EasySharedPreferences.with(this, SETTING_PREFS_NAME);
        registerActivityLifecycleCallbacks(this);

        setupFabric();
        setupBuzzScreen();
        setupKakao();
        setupCustomFont();
        setupUUID();

        ZummaApi.initialize(this);
        setupManagers();
    }

    protected void setupManagers() {
        AuthenticationManager.getInstance().init(this);
        UserManager.getInstance().init(this);
        MetaDataManager.getInstance().init(this);

        UserManager.getInstance().getUserObservable()
                .filter(User::isNotNull)
                .subscribe(this::updateUser);
        UserManager.getInstance().getFamilyObservable()
                .filter(Family::isNotNull)
                .subscribe(this::updateFamily);
    }

    protected void setupBuzzScreen() {
        BuzzScreen.init(KEY_BUZZ_SCREEN, this, CustomLockerActivity.class, R.drawable.img_lockscreen_default);
        BuzzScreen buzzScreen = BuzzScreen.getInstance();
        buzzScreen.setOnPointListener(new BuzzScreen.OnPointListener() {
            @Override
            public void onSuccess(BuzzScreen.PointType pointType, int reward) {
                MetaDataManager.getInstance().isRewardNotificationEnabled()
                        .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                        .filter(enabled -> enabled)
                        .subscribe(__ -> notifyReward(ZummaApp.this, reward), DLog::e);

                UserManager.getInstance().fetchHomeZmoney().subscribe();
            }

            @Override
            public void onFail(BuzzScreen.PointType pointType) {

            }
        });

        LockerServiceNotificationConfig config = buzzScreen.getLockerServiceNotificationConfig();
        config.setTitle(getString(R.string.app_name));
        config.setText(getString(R.string.message_notification_lockscreen));
        config.setSmallIconResourceId(R.drawable.ic_noti_reward);
        config.setShowAlways(false);
    }

    private void notifyReward(Context context, int reward) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_noti_reward)
                .setTicker(context.getString(R.string.message_notification_reward, reward))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.message_notification_reward, reward))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setSound(null);
        notificationManager.notify(8001, notificationBuilder.build());
    }

    protected void setupFabric() {
        if (Config.DEBUG) {
            Fabric.with(getApplicationContext(),
                    new Crashlytics.Builder().core(
                            new CrashlyticsCore.Builder().disabled(Config.DEBUG).build()).build());
        } else {
            Fabric.with(getApplicationContext(), new Crashlytics());
        }
    }

    protected void setupKakao() {
        KakaoSDK.init(new KakaoSDKAdapter(this));
    }

    protected void setupCustomFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    private void setupUUID() {
        if (TextUtils.isEmpty(EasySharedPreferences.with(this).getString(KEY_UUID, ""))) {
            EasySharedPreferences.with(this).putString(KEY_UUID, UUID.randomUUID().toString());
        }
    }

    public String getUUID() {
        return EasySharedPreferences.with(this).getString(KEY_UUID);
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    @SuppressLint("MissingPermission")

    private void updateUser(User user) {
        try {
            String userId = String.valueOf(user.getId());
            Crashlytics.setUserIdentifier(userId);
            Crashlytics.setUserName(user.getName());
            if (TextUtils.isEmpty(user.getEmail())) {
                Crashlytics.setUserEmail(String.format("%s@zummaslide.com", PhoneNumberUtil.getPhoneNumber(this)));
            } else {
                Crashlytics.setUserEmail(user.getEmail());
            }
            setBuzzScreenUserInfo(user);
        } catch (Exception e) {
            // failure update
        }
    }

    private void updateFamily(Family family) {
        try {
            UserProfile userProfile = BuzzScreen.getInstance().getUserProfile();
            Address addr = family.getAddress();
            userProfile.setRegion(addr.getAreaName());
        } catch (Exception e) {
            // pass
        }
    }

    public void setBuzzScreenUserInfo(User user) {
        UserProfile userProfile = BuzzScreen.getInstance().getUserProfile();
        userProfile.setUserId(String.valueOf(user.getId()));
        userProfile.setGender(Sex.MAN.equals(user.getSex()) ? UserProfile.USER_GENDER_MALE : UserProfile.USER_GENDER_FEMALE);
        try {
            userProfile.setBirthYear(Integer.parseInt(user.getBirthYear()));
        } catch (Exception e) {
            // pass
        }
    }

    public void activateBuzzScreenIfNeeded() {
        if (AuthenticationManager.getInstance().isLoggedIn()) {
            if (isFirstLaunching()) {
                setBuzzScreenUserInfo(UserManager.getInstance().getUserValue());
                BuzzScreen.getInstance().activate();
            } else {
                MetaDataManager.getInstance().isLockerEnabled()
                        .filter(enabled -> enabled)
                        .subscribe(__ -> {
                            setBuzzScreenUserInfo(UserManager.getInstance().getUserValue());
                            BuzzScreen.getInstance().activate();
                        });
            }
        }
    }

    private boolean isFirstLaunching() {
        boolean isFirstLaunching = settingPrefs.getBoolean("first_launching", true);
        if (isFirstLaunching) {
            settingPrefs.putBoolean("first_launching", false);
        }
        return isFirstLaunching;
    }

    /**
     * 최근 공지사항을 가져온다.
     *
     * @return 최근 공지사항
     */
    public Notice getLatestNotice() {
        return settingPrefs.getObject(KEY_LATEST_NOTICE, Notice.class);
    }

    /**
     * 최근 공지사항을 저장한다.
     *
     * @param notice 최근 공지사항
     */
    public void setLatestNotice(Notice notice) {
        settingPrefs.putObject(KEY_LATEST_NOTICE, notice);
    }

    /**
     * 마지막으로 공지사항 리스트를 확인한 날짜를 가져온다.
     *
     * @return 마지막으로 공지사항 리스트를 확인한 날짜
     */
    public Date getLastNoticeReadDate() {
        return settingPrefs.getObject(KEY_LAST_READ_NOTICE_DATE, Date.class);
    }

    /**
     * 마지막으로 공지사항 리스트를 확인한 날짜를 저장한다.
     *
     * @param date 마지막으로 공지사항 리스트를 확인한 날짜
     */
    public void setLastNoticeReadDate(Date date) {
        settingPrefs.putObject(KEY_LAST_READ_NOTICE_DATE, date);
    }

    /**
     * {@code SharedPreferences}에 저장된 데이터를 모두 제거한다.
     */
    public void clearSettings() {
        settingPrefs.clear();
    }

    /**
     * 최근 공지사항을 읽었는지 확인한다.
     *
     * @return 최근 공지사항 확인 여부
     */
    public boolean isUnreadLatestNotice() {
        Notice notice = getLatestNotice();
        return notice != null && isUnreadNotice(notice);
    }

    /**
     * 전달받은 공지사항이 새로운 공지사항인지 확인한다.
     * 서버에서 새로운 공지사항이라고 전달됐더라도, 어플리케이션 내에서 {@link com.zslide.fragments.NoticesFragment} 안으로 진입했다면,
     * 읽은 것으로 판단한다.
     *
     * @param notice 공지사항
     * @return 공지사항을 읽었는지 읽지 않았는지
     */
    public boolean isUnreadNotice(Notice notice) {
        Date lastReadNoticeDate = getLastNoticeReadDate();
        return lastReadNoticeDate == null || (notice.isNew() && lastReadNoticeDate.before(notice.getPubDate()));
    }

    public FirebaseAnalytics getAnalytics() {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        UserManager userManager = UserManager.getInstance();
        if (AuthenticationManager.getInstance().isLoggedIn()) {
            try {
                User user = userManager.getUserValue();
                analytics.setUserProperty("age", String.format("%s대", (user.getAge() / 10) * 10));
                analytics.setUserProperty("sex", user.getSex().getSimpleKorean());

                Family family = userManager.getFamilyValue();
                if (family.isNull()) {
                    analytics.setUserProperty("sigungu", "없음");
                    analytics.setUserProperty("dong", "없음");
                } else {
                    analytics.setUserProperty("sigungu", family.getAddress().getAreaName());
                    analytics.setUserProperty("dong", family.getAddress().getDongName());
                }
                boolean isLockerEnabled = MetaDataManager.getInstance().isLockerEnabled().blockingGet();
                analytics.setUserProperty("useLockScreen", isLockerEnabled ? "설정됨" : "해제됨");
            } catch (Exception e) {
                // failure setup info
            }
        }
        return analytics;
    }

    private void setTopActivity(Activity activity) {
        if (!(activity instanceof BaseActivity)) {
            return;
        }
        if (activityRef != null) {
            activityRef.clear();
        }
        activityRef = new WeakReference<>((BaseActivity) activity);
    }

    private void clearTopActivity(Activity activity) {
        if (!(activity instanceof BaseActivity)) {
            return;
        }

        if (activityRef != null && activity.equals(activityRef.get())) {
            activityRef.clear();
            activityRef = null;
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        ZLog.d(this, "activity created: " + activity);
        setTopActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ZLog.d(this, "activity started: " + activity);
        setTopActivity(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ZLog.d(this, "activity resumed: " + activity);
        setTopActivity(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ZLog.d(this, "activity paused: " + activity);
        clearTopActivity(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ZLog.d(this, "activity stopped: " + activity);
        clearTopActivity(activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ZLog.d(this, "activity destroyed: " + activity);
        clearTopActivity(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    private static class KakaoSDKAdapter extends KakaoAdapter {

        private Context context;

        public KakaoSDKAdapter(Context context) {
            this.context = context;
        }

        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         *
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return () -> context.getApplicationContext();
        }
    }
}