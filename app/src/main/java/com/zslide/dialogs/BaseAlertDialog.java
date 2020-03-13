package com.zslide.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.zslide.R;

import butterknife.ButterKnife;

/**
 * Created by jdekim43 on 2016. 5. 26..
 */
public abstract class BaseAlertDialog extends AppCompatDialogFragment {

    private DialogInterface.OnDismissListener dismissListener;
    private DialogInterface.OnCancelListener cancelListener;
    private DialogInterface.OnShowListener showListener;
    private DialogInterface.OnKeyListener keyListener;

    protected abstract
    @LayoutRes
    int getLayoutResource();

    protected abstract void setupLayout(View view);

    protected abstract void setupDialog(AlertDialog.Builder builder);

    protected View onCreateTitleView() {
        return null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View titleView = onCreateTitleView();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog_Alert);
        if (titleView != null) {
            builder.setCustomTitle(titleView);
        }

        View view = createContentView();
        ButterKnife.bind(this, view);

        setupLayout(view);
        builder.setView(view);

        setupDialog(builder);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this::onShow);
        dialog.setOnKeyListener(this::onKey);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (cancelListener != null) {
            cancelListener.onCancel(dialog);
        }
    }

    public void onShow(DialogInterface dialog) {
        if (dialog instanceof AlertDialog) {
            AlertDialog alertDialog = (AlertDialog) dialog;
            setupButton(alertDialog.getButton(DialogInterface.BUTTON_POSITIVE),
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE),
                    alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL));
        }

        if (showListener != null) {
            showListener.onShow(dialog);
        }
    }

    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return keyListener != null && keyListener.onKey(dialog, keyCode, event);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setOnShowListener(DialogInterface.OnShowListener showListener) {
        this.showListener = showListener;
    }

    public void setOnKeyListener(DialogInterface.OnKeyListener keyListener) {
        this.keyListener = keyListener;
    }

    protected View createContentView() {
        return LayoutInflater.from(getActivity()).inflate(getLayoutResource(), null);
    }

    protected void setupButton(Button positiveButton, Button negativeButton, Button neutralButton) {
        /*int pink = ContextCompat.getColor(getActivity(), R.color.pink);
        int gray7 = ContextCompat.getColor(getActivity(), R.color.gray_7);
        positiveButton.setTextColor(pink);
        negativeButton.setTextColor(gray7);
        neutralButton.setTextColor(gray7);*/
    }

    public interface OnCancelListener {
        void onCancel();
    }
}
