package com.zslide.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zslide.R;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chulwoo on 16. 1. 11..
 * <p>
 * Updated by jdekim43 on 16. 05. 31..
 * <p>
 * 설정 화면에서 사용하는 뷰
 */
public class AppSettingItemSwitchView extends LinearLayout {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.switchCompat) SwitchCompat switchView;

    @BindColor(R.color.subAccentColor) int ACCENT_COLOR;

    public AppSettingItemSwitchView(Context context) {
        this(context, null);
    }

    public AppSettingItemSwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppSettingItemSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_app_setting_item_switch, this);
        ButterKnife.bind(this);
        init(context.obtainStyledAttributes(attrs, R.styleable.AppSettingItemSwitchView));
    }

    public String getTitle() {
        return titleView.getText().toString();
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public boolean isChecked() {
        return switchView.isChecked();
    }

    public void setChecked(boolean checked) {
        switchView.setChecked(checked);
    }

    protected void init(TypedArray attr) {
        titleView.setText(attr.getString(R.styleable.AppSettingItemSwitchView_android_title));
        switchView.setClickable(false);
        setOnClickListener(v -> switchView.setChecked(!switchView.isChecked()));
        attr.recycle();
        switchView.setThumbTintList(getSwitchThumbColorStateList(getContext(), ACCENT_COLOR));
        switchView.setTrackTintList(getSwitchTrackColorStateList(getContext(), ACCENT_COLOR));
    }

    private ColorStateList getSwitchThumbColorStateList(Context context, int accentColor) {
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int i = 0;

        states[i] = new int[]{-android.R.attr.state_enabled};
        colors[i] = ContextCompat.getColor(context, R.color.gray_d);
        i++;

        states[i] = new int[]{android.R.attr.state_checked};
        colors[i] = accentColor;
        i++;

        states[i] = new int[0];
        colors[i] = ContextCompat.getColor(context, R.color.gray_d);

        return new ColorStateList(states, colors);
    }

    private ColorStateList getSwitchTrackColorStateList(Context context, int accentColor) {
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int i = 0;

        states[i] = new int[]{-android.R.attr.state_enabled};
        colors[i] = ContextCompat.getColor(context, R.color.gray_d);
        i++;

        states[i] = new int[]{android.R.attr.state_checked};
        colors[i] = ColorUtils.setAlphaComponent(accentColor, 100);
        i++;

        states[i] = new int[0];
        colors[i] = ContextCompat.getColor(context, R.color.gray_d);

        return new ColorStateList(states, colors);
    }
}
