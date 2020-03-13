package com.zslide.data.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.zslide.data.local.base.AbstractLocalSource;
import com.zslide.data.model.Family;
import com.zslide.data.model.HomeZmoney;
import com.zslide.data.model.User;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public class LocalUserSource extends AbstractLocalSource {

    private static final String KEY_USER = "user";
    private static final String KEY_FAMILY = "family";
    private static final String KEY_HOME_ZMONEY = "home_zmoney";
    private static final String KEY_STAMP_CARDS = "stamp_cards";
    private static final String KEY_NOTIFIED_BAN = "notified_ban";

    public LocalUserSource(Context context) {
        super(context);
    }

    @Override
    protected String getPreferencesName() {
        return "user";
    }

    @NonNull
    public User getUser() {
        return getObject(KEY_USER, User.NULL, User.class);
    }

    public void setUser(User user) {
        putObject(KEY_USER, user);
    }

    @NonNull
    public Family getFamily() {
        return getObject(KEY_FAMILY, Family.NULL, Family.class);
    }

    public void setFamily(Family family) {
        putObject(KEY_FAMILY, family);
    }

    @NonNull
    public HomeZmoney getHomeZmoney() {
        return getObject(KEY_HOME_ZMONEY, HomeZmoney.NULL, HomeZmoney.class);
    }

    public void setHomeZmoney(HomeZmoney zmoney) {
        putObject(KEY_HOME_ZMONEY, zmoney);
    }

    public boolean isNotifiedBan() {
        return getBoolean(KEY_NOTIFIED_BAN, false);
    }

    public void setNotifiedBan(boolean notified) {
        putBoolean(KEY_NOTIFIED_BAN, notified);
    }
}
