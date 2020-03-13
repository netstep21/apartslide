package com.zslide.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.zslide.Navigator;
import com.zslide.R;

/**
 * Created by chulwoo on 16. 3. 30..
 */
public class SessionExpiredDialog extends DialogFragment {

    private static final String ARG_MESSAGE = "message";

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
                    Navigator.startAuthActivity(getContext());
                })
                .create();
    }
}
