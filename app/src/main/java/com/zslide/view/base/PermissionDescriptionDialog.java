package com.zslide.view.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;

import com.zslide.R;

import io.reactivex.functions.Action;

/**
 * Created by chulwoo on 2018. 1. 2..
 */

public class PermissionDescriptionDialog extends DialogFragment {

    private static final String ARG_MESSAGE_ID = "message_id";

    private int messageId;
    private Action onCancel;

    public static PermissionDescriptionDialog newInstance(@StringRes int messageId) {
        Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE_ID, messageId);

        PermissionDescriptionDialog fragment = new PermissionDescriptionDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public PermissionDescriptionDialog doOnCancel(Action onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            messageId = getArguments().getInt(ARG_MESSAGE_ID);
        } else {
            messageId = savedInstanceState.getInt(ARG_MESSAGE_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_MESSAGE_ID, messageId);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            messageId = savedInstanceState.getInt(ARG_MESSAGE_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(messageId)
                .setPositiveButton(R.string.label_settings, (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:" + context.getPackageName()));
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();

                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        context.startActivity(intent);
                    }
                })
                .setCancelable(false)
                .setNegativeButton(R.string.label_close, (dialog, which) -> {
                    if (onCancel != null) {
                        try {
                            onCancel.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        return builder.create();
    }
}