package com.zslide.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zslide.R;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jdekim43 on 2016. 5. 26..
 */
public class AccountSettingItemView extends RelativeLayout {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.subTitle) TextView subTitleView;
    @BindView(R.id.arrow) ImageView arrowView;

    @BindDimen(R.dimen.account_setting_item_title_only) int titleOnlyFontSize;
    @BindDimen(R.dimen.account_setting_item_title) int titleFontSize;
    @BindDimen(R.dimen.account_setting_item_sub_title) int subTitleFontSize;

    public AccountSettingItemView(Context context) {
        this(context, null);
    }

    public AccountSettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccountSettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_account_setting_item, this);
        ButterKnife.bind(this);
        init(context.obtainStyledAttributes(attrs, R.styleable.AccountSettingItemView));
        setClickable(true);
    }

    public void setTitle(CharSequence text) {
        titleView.setText(text);
    }

    public void setSubTitle(CharSequence text) {
        subTitleView.setText(text);
        draw();
    }

    protected void init(TypedArray attr) {
        titleView.setText(attr.getString(R.styleable.AccountSettingItemView_android_title));
        subTitleView.setText(attr.getString(R.styleable.AccountSettingItemView_subTitle));

        draw();
    }

    protected void draw() {
        if (subTitleView.length() == 0) {
            subTitleView.setVisibility(View.GONE);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleOnlyFontSize);
        } else {
            subTitleView.setVisibility(View.VISIBLE);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleFontSize);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        arrowView.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }
}
