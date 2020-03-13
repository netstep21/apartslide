package com.zslide.view.base;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.zslide.R;
import com.zslide.view.auth.AuthActivity;

/**
 * Created by chulwoo on 2017. 12. 29..
 * <p>
 * <p>
 * TODO: 좀 더 다듬을 필요가 있음. 통합적으로 세션 만료 관리.
 */

public class SessionExpiredDialog extends DialogFragment {

    private static final String ARG_MESSAGE = "message";

    public static SessionExpiredDialog newInstance() {
        return newInstance("");
    }

    public static SessionExpiredDialog newInstance(String message) {
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);

        SessionExpiredDialog instance = new SessionExpiredDialog();
        instance.setArguments(args);
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(ARG_MESSAGE);
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.message_session_expired);
        }

        return new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.label_confirm, (dialog, which) -> {
                    Intent intent = new Intent(getActivity(), AuthActivity.class);
                    ComponentName componentName = intent.getComponent();
                    Intent loginIntent = Intent.makeRestartActivityTask(componentName);
                    startActivity(loginIntent);
                })
                .create();
    }
}
