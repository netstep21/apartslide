package com.zslide.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zslide.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action0;

/**
 * Created by chulwoo on 2016. 10. 10..ㅇㅇ
 */

public class ListHeaderView extends RelativeLayout {

    @BindView(R.id.usage) TextView usageView;

    private Action0 usageBehavior;

    public ListHeaderView(Context context) {
        super(context);
        init(context);
    }

    public ListHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_list_header, this);
        ButterKnife.bind(this);
        if (isInEditMode()) return;
    }

    public void setName(String name) {
        usageView.setText(name);
    }

    public void setName(@StringRes int resId) {
        usageView.setText(resId);
    }

    public void setUsageBehavior(Action0 usageBehavior) {
        this.usageBehavior = usageBehavior;
    }

    @OnClick(R.id.usage)
    public void showUsage() {
        if (usageBehavior != null) {
            usageBehavior.call();
        }
    }
}
