package com.zslide.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.zslide.IntentConstants;
import com.zslide.R;

import butterknife.BindView;

/**
 * Created by chulwoo on 16. 6. 30..
 */
public class OCBConfirmDialog extends BaseAlertDialog {

    @BindView(R.id.usePoint) TextView usePointView;
    @BindView(R.id.feesLabel) TextView feesLabelView;
    @BindView(R.id.fees) TextView feesView;
    @BindView(R.id.applyPoint) TextView applyPointView;
    private int point;
    private double feesPercentage;
    private OnConfirmListener listener;

    public static OCBConfirmDialog newInstance(int point, double feesPercentage) {
        Bundle args = new Bundle();
        args.putInt(IntentConstants.EXTRA_POINT, point);
        args.putDouble("fees", feesPercentage);

        OCBConfirmDialog fragment = new OCBConfirmDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            point = args.getInt(IntentConstants.EXTRA_POINT);
            feesPercentage = args.getDouble("fees");
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_ocb_confirm;
    }

    @Override
    protected void setupLayout(View view) {
        usePointView.setText(getString(R.string.format_ocb, point));
        int feesPoint = (int) (point / 100.0f * feesPercentage);
        feesLabelView.setText(getString(R.string.format_ocb_fees, feesPercentage));
        feesView.setText(getString(R.string.format_ocb, feesPoint));
        applyPointView.setText(getString(R.string.format_price, point - feesPoint));
    }

    public OCBConfirmDialog setOnConfirmListener(OnConfirmListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected void setupDialog(AlertDialog.Builder builder) {
        builder.setPositiveButton(R.string.label_confirm, (dialog, which) -> {
            if (listener != null) {
                listener.onConfirm(point);
            }
        }).setNegativeButton(R.string.label_cancel, null);
    }

    public interface OnConfirmListener {
        void onConfirm(int usePoint);
    }
}
