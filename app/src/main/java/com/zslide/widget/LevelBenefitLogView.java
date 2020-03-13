package com.zslide.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.models.LevelCouponLog;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chulwoo on 16. 8. 5..
 */
public class LevelBenefitLogView extends LinearLayout {

    @BindView(R.id.date) TextView dateView;
    @BindView(R.id.description) TextView descriptionView;
    @BindView(R.id.zmoney) TextView zmoneyView;

    public LevelBenefitLogView(Context context, LevelCouponLog levelCouponLog) {
        this(context);
        bind(levelCouponLog);
    }

    public LevelBenefitLogView(Context context) {
        this(context, null, 0);
    }

    public LevelBenefitLogView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LevelBenefitLogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LevelBenefitLogView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_level_benefit_log, this);
        ButterKnife.bind(this);
    }

    public void bind(LevelCouponLog log) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateView.setText(formatter.format(log.getCreatedAt()));
        descriptionView.setText(log.getDescription());
        zmoneyView.setText(getContext().getString(R.string.format_point, log.getZmoney()));
    }
}
