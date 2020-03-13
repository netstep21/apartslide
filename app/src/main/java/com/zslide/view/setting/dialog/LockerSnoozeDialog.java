package com.zslide.view.setting.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.zslide.R;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by chulwoo on 2018. 1. 17..
 */

public class LockerSnoozeDialog extends DialogFragment {

    public static final int DISABLE_SEC = -1;

    private int[] seconds = new int[]{
            2 * 60 * 60,
            4 * 60 * 60,
            8 * 60 * 60,
            24 * 60 * 60,
            Integer.MAX_VALUE // ViewModel에서 어떻게 ?
    };

    private Consumer<Integer> snoozeAction;
    private Action cancelAction;

    public void setSnoozeAction(Consumer<Integer> snoozeAction) {
        this.snoozeAction = snoozeAction;
    }

    public void setCancelAction(Action cancelAction) {
        this.cancelAction = cancelAction;
    }

/*
    public interface Callback {
        void onSelectSnoozeSec(int snoozeSec);

        void onCancel();
    }

    private Callback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.callback = ((Callback) context);
        } catch (ClassCastException e) {
            throw new IllegalStateException("이 다이얼로그를 사용하는 Activity는 Callback을 구현해야합니다.");
        }
    }
*/

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getContext();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_slide_off_dialog, null);
        Spinner spinner = view.findViewById(R.id.restartIntervals);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.restart_intervals,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(1);
        dialogBuilder.setView(view);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(getString(R.string.label_confirm), (dialog1, which) -> {
            try {
                snoozeAction.accept(seconds[spinner.getSelectedItemPosition()]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.label_cancel), (dialog, which) -> {
            try {
                cancelAction.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Dialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static LockerSnoozeDialog newInstance(Consumer<Integer> onSnoozeAction, Action cancelAction) {

        Bundle args = new Bundle();

        LockerSnoozeDialog fragment = new LockerSnoozeDialog();
        fragment.setArguments(args);
        // TODO: 정리해야함.
        fragment.setSnoozeAction(onSnoozeAction);
        fragment.setCancelAction(cancelAction);
        return fragment;
    }
}
