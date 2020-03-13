package com.zslide.view.splash;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;

import com.zslide.R;
import com.zslide.data.model.AlertMessage;

/**
 * Created by chulwoo on 2018. 1. 2..
 */

public class AlertMessageDialog extends AppCompatDialogFragment {

    interface Callback {
        void onConfirm(String action);

        void onCancel();
    }

    public static AlertMessageDialog newInstance(AlertMessage alertMessage) {

        Bundle args = new Bundle();
        args.putParcelable("alert_message", alertMessage);

        AlertMessageDialog fragment = new AlertMessageDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private Callback callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (Callback) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertMessage alertMessage = getArguments().getParcelable("alert_message");
        alertMessage.notified(getActivity());

        AlertDialog.Builder updateDialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(alertMessage.getTitle())
                .setMessage(alertMessage.getMessage())
                .setCancelable(alertMessage.isCancelable());

        String action = alertMessage.getAction();
        if (!TextUtils.isEmpty(action)) {
            updateDialogBuilder.setPositiveButton(
                    R.string.label_confirm, (dialog, which) -> callback.onConfirm(action));
        }

        if (alertMessage.isCancelable()) {
            updateDialogBuilder.setNegativeButton(
                    R.string.label_cancel, (dialogInterface, i) -> callback.onCancel());
            updateDialogBuilder.setOnCancelListener(
                    dialog -> callback.onCancel());
        }

        return updateDialogBuilder.create();
    }
}
