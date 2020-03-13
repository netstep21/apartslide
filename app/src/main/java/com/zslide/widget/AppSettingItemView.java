package com.zslide.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zslide.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chulwoo on 16. 1. 11..
 * <p>
 * Updated by jdekim43 on 16. 05. 31..
 * <p>
 * 설정 화면에서 사용하는 뷰
 */
public class AppSettingItemView extends LinearLayout {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.subTitle) TextView subTitleView;
    @BindView(R.id.info) TextView infoTextView;

    public AppSettingItemView(Context context) {
        this(context, null);
    }

    public AppSettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppSettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_app_setting_item, this);
        ButterKnife.bind(this);
        init(context.obtainStyledAttributes(attrs, R.styleable.AppSettingItemView));
    }

    public String getTitle() {
        return titleView.getText().toString();
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public String getSubTitle() {
        return subTitleView.getText().toString();
    }

    public void setSubTitle(String subTitle) {
        subTitleView.setText(subTitle);
        subTitleInvalidate();
    }

    public void setInfo(String info) {
        infoTextView.setText(info);
        infoTextInvalidate();
    }

    public String getInfoText() {
        return infoTextView.getText().toString();
    }

    public void setVisibleNewBadge(boolean enabled) {
        if (enabled) {
            titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_new, 0);
        } else {
            titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    protected void init(TypedArray attr) {
        titleView.setText(attr.getString(R.styleable.AppSettingItemView_android_title));
        subTitleView.setText(attr.getString(R.styleable.AppSettingItemView_subTitle));
        infoTextView.setText(attr.getString(R.styleable.AppSettingItemView_info));
        setVisibleNewBadge(attr.getBoolean(R.styleable.AppSettingItemView_isNew, false));
        attr.recycle();
        subTitleInvalidate();
        infoTextInvalidate();
    }

    private void subTitleInvalidate() {
        subTitleView.setVisibility(subTitleView.length() == 0 ? View.GONE : View.VISIBLE);
    }

    private void infoTextInvalidate() {
        infoTextView.setVisibility(infoTextView.length() == 0 ? View.GONE : View.VISIBLE);
    }
}
