package com.zslide.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.zslide.Config;
import com.zslide.R;

import java.util.Arrays;

/**
 * Created by chulwoo on 2018. 1. 8..
 */


@SuppressLint({"HardwareIds", "MissingPermission"})
public class PhoneNumberUtil {

    /**
     * 디바이스에 등록된 핸드폰 번호를 가져온다.
     * 하이픈이 없는 문자열이고, 통신사에 따라 있을 수 있는 국가 코드를 0으로 대체한다.
     *
     * @return 핸드폰 번호
     */
    public static String getPhoneNumber(Context context) {
        if (Config.DEBUG) {
            return getLatestDebugPhoneNumber(context);
        } else {
            return getPhoneNumberInternal(context);
        }
    }

    protected static String getPhoneNumberInternal(Context context) {
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNumber = telephonyManager.getLine1Number();
            if (TextUtils.isEmpty(phoneNumber)) {
                phoneNumber = "";
            } else if (phoneNumber.startsWith("+82")) {
                // 통신사에 따라 앞에 +82가 붙는 경우가 있음
                phoneNumber = phoneNumber.replace("+82", "0");
            }

            return phoneNumber;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 디바이스에 등록된 핸드폰 번호(000-0000-0000 포맷)를 가져온다.
     *
     * @return 핸드폰 번호
     */
    public static String getKorPhoneNumber(Context context) {
        String phoneNumber;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            phoneNumber = PhoneNumberUtils.formatNumber(getPhoneNumber(context), "KO");
        } else {
            phoneNumber = PhoneNumberUtils.formatNumber(getPhoneNumber(context));
        }

        if (TextUtils.isEmpty(phoneNumber)
                || "null".equalsIgnoreCase(phoneNumber)) {
            phoneNumber = getPhoneNumber(context);
        }
        return phoneNumber;
    }


    /**
     * 디버그용 핸드폰 번호
     */
    public static final String[] TEST_PHONE_NUMBER_LIST = {
            "15555215551",
            "15555215552",
            "15555215553",
            "15555215554",
            "15555215555",
            "15555215556",
            "15555215557",
            "15555215558",
            "15555215559"
    };

    private static final int TEST_PHONE_NUMBER_COUNT = TEST_PHONE_NUMBER_LIST.length;
    private static final int USE_DEVICE_PHONE_NUMBER = TEST_PHONE_NUMBER_COUNT;
    private static final int USE_LATEST_PHONE_NUMBER = TEST_PHONE_NUMBER_COUNT + 1;
    private static final int USE_INPUT_PHONE_NUMBER = TEST_PHONE_NUMBER_COUNT + 2;

    private static final String PREF_DEBUG_PHONE_NUMBER = "user.test_phone_number";

    @SuppressLint("MissingPermission")
    public static void showSelectTestAccountDialog(Activity activity, Runnable onDismiss) {
        String[] items = Arrays.copyOf(TEST_PHONE_NUMBER_LIST, TEST_PHONE_NUMBER_COUNT + 3);
        items[USE_DEVICE_PHONE_NUMBER] = getPhoneNumberInternal(activity);
        items[USE_LATEST_PHONE_NUMBER] = getLatestDebugPhoneNumber(activity);
        items[USE_INPUT_PHONE_NUMBER] = "직접 입력";

        new AlertDialog.Builder(activity)
                .setItems(items, (dialog, which) -> onSelectedDebugPhoneNumber(activity, which, onDismiss))
                .setTitle("로그인할 테스트 계정")
                .show();
    }

    @SuppressLint("MissingPermission")
    private static void onSelectedDebugPhoneNumber(Activity activity, int which, Runnable onDismiss) {
        if (which == USE_INPUT_PHONE_NUMBER) {
            EditText inputPhoneNumberView = new EditText(activity);
            inputPhoneNumberView.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            new AlertDialog.Builder(activity).setTitle("로그인할 번호 직접 입력")
                    .setView(inputPhoneNumberView)
                    .setPositiveButton(R.string.label_confirm, (dialog1, which1) -> {
                        setLatestDebugPhoneNumber(activity, inputPhoneNumberView.getText().toString().trim().replace("-", ""));
                        dialog1.dismiss();
                        onDismiss.run();
                    })
                    .show();
        } else if (which == USE_DEVICE_PHONE_NUMBER) {
            setLatestDebugPhoneNumber(activity, getPhoneNumberInternal(activity));
            onDismiss.run();
        } else if (which == USE_LATEST_PHONE_NUMBER) {
            // pass
            onDismiss.run();
        } else {
            setLatestDebugPhoneNumber(activity, TEST_PHONE_NUMBER_LIST[which]);
            onDismiss.run();
        }

    }

    private static String getLatestDebugPhoneNumber(Context context) {
        return EasySharedPreferences.with(context).getString(PREF_DEBUG_PHONE_NUMBER, "01012345678");
    }

    private static void setLatestDebugPhoneNumber(Context context, String testPhoneNumber) {
        EasySharedPreferences.with(context).putString(PREF_DEBUG_PHONE_NUMBER, testPhoneNumber);
    }
}
