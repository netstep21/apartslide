package com.zslide.data.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;

import com.zslide.Config;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;

/**
 * Created by chulwoo on 15. 7. 2..
 * <p>
 * check_version API에서 전달하는 데이터 모델
 * <p>
 */
public class AlertMessage extends BaseModel implements Parcelable {

    public static final AlertMessage NULL = new AlertMessage();

    private static final String PREFERENCES_NAME = "version";
    private static final String KEY_NOTIFIED_MESSAGE_ID = "notified_message_id";

    @Getter @SerializedName("has_message") private boolean hasMessage;
    @Getter @SerializedName("title") private String title;
    @Getter @SerializedName("message_id") private int messageId;
    @Getter @SerializedName("message") private String message;
    @Getter @SerializedName("cancelable") private boolean cancelable;
    @Getter @SerializedName("sticky") private boolean sticky;
    @Getter @SerializedName("action") private String action;
    @Getter @SerializedName("version_name") private String versionName;
    @Getter @SerializedName("version_code") private int versionCode;

    public AlertMessage() {
        hasMessage = false;
        title = "";
        messageId = -1;
        message = "";
        cancelable = true;
        sticky = false;
        action = "";
        versionName = "1.0.0";
        versionCode = 0;
    }

    @Override
    public boolean isNotNull() {
        return versionCode != 0;
    }

    @Override
    public boolean isNull() {
        return versionCode == 0;
    }

    public boolean isNeedShowingMessage(Context context) {
        // todo 다른 곳에서 처리하도록..
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        int notifiedMessageId = prefs.getInt(KEY_NOTIFIED_MESSAGE_ID, 0);
        return hasMessage && (notifiedMessageId != messageId || sticky);
    }

    // todo 다른 곳에서 처리하도록..
    public void notified(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        prefs.edit().putInt(KEY_NOTIFIED_MESSAGE_ID, messageId).apply();
    }

    public boolean isNeedUpdate() {
        return versionCode > Config.VERSION_CODE;
    }
}
