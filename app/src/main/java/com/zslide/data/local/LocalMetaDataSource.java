package com.zslide.data.local;

import android.content.Context;

import com.zslide.data.local.base.AbstractLocalSource;

/**
 * Created by chulwoo on 2017. 12. 29..
 * <p>
 * TODO: 2018. 1. 5. 마이그레이션 코드는 통합할 수 있을 것으로 보임.
 */

public class LocalMetaDataSource extends AbstractLocalSource {

    private static final int PREF_VERSION = 1;

    private static final String KEY_PREF_VERSION = "pref_version";
    private static final String KEY_LOCKER_ENABLED = "enable_locker";
    private static final String KEY_REWARD_NOTIFICATION_ENABLE = "enable_reward_notification";
    private static final String KEY_DONT_ASK_AGAIN_ZMONEY_USE = "dont_ask_agin_zmoney_use";

    @Override
    protected String getPreferencesName() {
        return "settings";
    }

    public LocalMetaDataSource(Context context) {
        super(context);
    }

    public boolean isRewardNotificationEnabled() {
        return getBoolean(KEY_REWARD_NOTIFICATION_ENABLE, true);
    }

    public void setRewardNotificationEnabled(boolean enabled) {
        putBoolean(KEY_REWARD_NOTIFICATION_ENABLE, enabled);
    }

    public boolean isDontAskAgainZmoneyUse() {
        return getBoolean(KEY_DONT_ASK_AGAIN_ZMONEY_USE, false);
    }

    public void setDontAskAgainZmoneyUse(boolean donAskAgain) {
        putBoolean(KEY_DONT_ASK_AGAIN_ZMONEY_USE, donAskAgain);
    }

    public void migrationIfNeeded(Context context) {
        int oldVersion = getInt(KEY_PREF_VERSION, 0);
        if (oldVersion != PREF_VERSION) {
            migrate(context, oldVersion);
            putInt(KEY_PREF_VERSION, PREF_VERSION);
        }
    }

    private void migrate(Context context, int oldVersion) {
    }
}