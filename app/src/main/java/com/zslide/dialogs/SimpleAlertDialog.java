package com.zslide.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zslide.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chulwoo on 16. 4. 11..
 */
public class SimpleAlertDialog extends DialogFragment {

    public interface OnConfirmListener {
        void onConfirm();
    }

    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_CONFIRM_LABEL = "confirmLabel";
    private static final String ARG_CANCEL_LABEL = "cancelLabel";
    private static final String ARG_CANCELABLE = "cancelable";

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.message) TextView messageView;

    private String title;
    private String message;
    private String confirmLabel;
    private String cancelLabel;
    private boolean cancelable;

    private OnConfirmListener confirmListener;
    private BaseAlertDialog.OnCancelListener cancelListener;

    public static SimpleAlertDialog newInstance(String message) {
        return newInstance("", message);
    }

    public static SimpleAlertDialog newInstance(String title, String message) {
        return newInstance(title, message, "");
    }

    public static SimpleAlertDialog newInstance(String message, boolean cancelable) {
        return newInstance("", message, "", "", cancelable);
    }

    public static SimpleAlertDialog newInstance(String title, String message, String confirmLabel) {
        return newInstance(title, message, confirmLabel, "", false);
    }

    public static SimpleAlertDialog newInstance(String title, String message, String confirmLabel, String cancelLabel) {
        return newInstance(title, message, confirmLabel, cancelLabel, true);
    }

    public static SimpleAlertDialog newInstance(String title, String message, String confirmLabel, String cancelLabel, boolean cancelable) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_CONFIRM_LABEL, confirmLabel);
        args.putString(ARG_CANCEL_LABEL, cancelLabel);
        args.putBoolean(ARG_CANCELABLE, cancelable);

        SimpleAlertDialog instance = new SimpleAlertDialog();
        instance.setArguments(args);

        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            readFromBundle(getArguments());
        } else {
            readFromBundle(savedInstanceState);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_simple_alert, null, false);
        ButterKnife.bind(this, view);
        builder.setView(view);
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(message)) {
            messageView.setText(message);
        }

        if (TextUtils.isEmpty(confirmLabel)) {
            confirmLabel = getString(R.string.label_confirm);
        }

        if (TextUtils.isEmpty(cancelLabel)) {
            cancelLabel = getString(R.string.label_cancel);
        }

        builder.setCancelable(cancelable);
        if (cancelable) {
            builder.setNegativeButton(cancelLabel, (d, which) -> {
                if (cancelListener != null) {
                    cancelListener.onCancel();
                }
            });
        }

        Dialog dialog = builder.setPositiveButton(confirmLabel, (d, which) -> {
            if (confirmListener != null) {
                confirmListener.onConfirm();
            }
        }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(dialog1 -> {
            AlertDialog alertDialog = (AlertDialog) dialog;
            int accentColor = ContextCompat.getColor(getActivity(), R.color.accentColor);
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(accentColor);
        });
        return dialog;
    }

    public SimpleAlertDialog setOnConfirmListener(OnConfirmListener listener) {
        this.confirmListener = listener;
        return this;
    }

    public SimpleAlertDialog setOnCancelListener(BaseAlertDialog.OnCancelListener listener) {
        this.cancelListener = listener;
        return this;
    }

    private void readFromBundle(@NonNull Bundle bundle) {
        title = bundle.getString(ARG_TITLE);
        message = bundle.getString(ARG_MESSAGE);
        cancelable = bundle.getBoolean(ARG_CANCELABLE);
        confirmLabel = bundle.getString(ARG_CONFIRM_LABEL);
        cancelLabel = bundle.getString(ARG_CANCEL_LABEL);
    }
}
