package com.zslide.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zslide.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chulwoo on 2015. 11. 3..
 */
public class EmptyView extends LinearLayout {

    @BindView(R.id.image) ImageView imageView;
    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.message) TextView messageView;
    @BindView(R.id.button) Button button;
    private Drawable image;
    private String title;
    private String message;
    private String buttonLabel;
    private int layoutResources = R.layout.view_empty;
    private OnButtonClickListener listener;

    public EmptyView(Context context, String title, String message, String buttonLabel) {
        this(context, null, title, message, buttonLabel);
    }

    public EmptyView(Context context, Drawable image, String title, String message, String buttonLabel) {
        super(context);
        this.image = image;
        this.title = title;
        this.message = message;
        this.buttonLabel = buttonLabel;
        init(context);
    }

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            loadTypedArray(context, attrs);
            init(context);
        }
    }

    public static EmptyView getDefault(Context context) {
        return new EmptyView(context, "내용이 없습니다.", "", "");
    }

    public void setButtonVisibility(int visibility) {
        button.setVisibility(visibility);
    }

    private void loadTypedArray(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EmptyView);
        image = a.getDrawable(R.styleable.EmptyView_image);
        title = a.getString(R.styleable.EmptyView_title);
        message = a.getString(R.styleable.EmptyView_message);
        buttonLabel = a.getString(R.styleable.EmptyView_buttonLabel);
        layoutResources = a.getResourceId(R.styleable.EmptyView_layout, R.layout.view_empty);
        a.recycle();
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(layoutResources, this, true);
        ButterKnife.bind(this, view);

        setImageDrawable(image);
        setTitle(title);
        setMessage(message);
        setButtonLabel(buttonLabel);
    }

    public void setImageDrawable(Drawable image) {
        if (image != null) {
            imageView.setImageDrawable(image);
        }
    }

    public void setTitle(@StringRes int resId) {
        setTitle(getContext().getString(resId));
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            titleView.setVisibility(View.GONE);
        } else {
            titleView.setVisibility(View.VISIBLE);
            //titleView.setText(Html.fromHtml("<b>" + title + "</b>"));
            titleView.setText(title);
        }
    }

    public void setMessage(@StringRes int resId) {
        setMessage(getContext().getString(resId));
    }

    public void setMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            messageView.setVisibility(View.GONE);
        } else {
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(message);
        }
    }

    public void setButtonLabel(@StringRes int resId) {
        setButtonLabel(getContext().getString(resId));
    }

    public void setButtonLabel(String buttonLabel) {
        if (TextUtils.isEmpty(buttonLabel)) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setText(Html.fromHtml("<b>" + buttonLabel + "</b>"));
        }
    }

    @OnClick(R.id.button)
    protected void onClick() {
        if (listener == null) {
            return;
        }

        listener.onClick();
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;
    }

    public interface OnButtonClickListener {
        void onClick();
    }
}
