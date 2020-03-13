package com.zslide.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.model.AuthType;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chulwoo on 16. 5. 20..
 */
public class AuthButton extends FrameLayout {

    @BindView(R.id.icon) ImageView iconView;
    @BindView(R.id.label) TextView labelView;
    private AuthType authType;
    private OnClickListener onClickListener;
    private Authable authable;

    public AuthButton(Context context) {
        this(context, null);
    }

    public AuthButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AuthButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttrs(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AuthButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        initAttrs(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        inflate(context, R.layout.btn_login, this);
        ButterKnife.bind(this);

        super.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }

            if (authable != null) {
                authable.auth(authType);
            }
        });
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        try {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AuthButton, defStyleAttr, defStyleRes);
            final CharSequence t = a.getText(R.styleable.AuthButton_android_text);
            if (!TextUtils.isEmpty(t)) {
                setText(t);
            }
            final Drawable d = a.getDrawable(R.styleable.AuthButton_android_src);
            if (d != null) {
                setIcon(d);
            }
            authType = AuthType.values()[a.getInt(R.styleable.AuthButton_authType, AuthType.PHONE.ordinal())];
            a.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIcon(Drawable drawable) {
        iconView.setImageDrawable(drawable);
    }

    public void setText(int resId) {
        labelView.setText(resId);
    }

    public void setText(CharSequence text) {
        labelView.setText(text);
    }

    public void setTextColor(@ColorInt int color) {
        labelView.setTextColor(color);
    }

    public void setTextColor(ColorStateList colors) {
        labelView.setTextColor(colors);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.onClickListener = l;
    }

    public void setAuthable(Authable authable) {
        this.authable = authable;
    }

    public interface Authable {
        void auth(AuthType authType);
    }
}
