package com.zslide.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zslide.R;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

/**
 * Created by jdekim43 on 2016. 3. 2..
 * <p>
 * Updated by jdekim43 on 2016. 6.23..
 * 글씨와 프로그레스 위치 지정기능 및 강제 프로그레스 중지 기능 제거
 */
public class ProgressButton extends FrameLayout {

    protected TextView textView;
    protected ProgressBar progressView;
    protected OnClickListener clickListener;
    @BindDrawable(R.drawable.btn_subaccent) Drawable backgroundDrawable;
    @BindDimen(R.dimen.progress_size_micro) int progressSize;
    @BindDimen(R.dimen.progress_width_micro) int progressWidth;
    @BindColor(R.color.white) int progressColor;
    private boolean isProgressing = false;
    private boolean canAutoProgressing = true;

    private Animation animForAppear;
    private Animation animForDisappear;

    public ProgressButton(Context context) {
        this(context, null);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton);
            init(context, a);
            a.recycle();
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.clickListener = l;
        super.setOnClickListener(this::onClick);
    }

    public boolean isProgressing() {
        return isProgressing;
    }

    public void setProgressing(boolean isProgressing) {
        this.isProgressing = isProgressing;
        invalidateView();
    }

    public void setAnimForAppear(Animation animation) {
        this.animForAppear = animation;
    }

    public void setAnimForDisappear(Animation animation) {
        this.animForDisappear = animation;
    }

    public TextView getTextView() {
        return textView;
    }

    public ProgressBar getProgressView() {
        return progressView;
    }

    public void setText(@StringRes int resId) {
        textView.setText(resId);
    }

    public void setTextColor(@ColorInt int color) {
        textView.setTextColor(color);
    }

    public void setTextSize(float size) {
        textView.setTextSize(size);
    }

    public void setTextSize(int unit, float size) {
        textView.setTextSize(unit, size);
    }

    public CharSequence getText() {
        return textView.getText();
    }

    public void setText(CharSequence text) {
        textView.setText(text);
    }

    public void setProgressColor(@ColorInt int color) {
        progressColor = color;
        invalidateProgressView();
    }

    public void setProgressSize(int pixel) {
        this.progressSize = pixel;
        invalidateProgressView();
    }

    public void setAutoProgressing(boolean enable) {
        this.canAutoProgressing = enable;
    }

    public boolean canAutoProgressing() {
        return canAutoProgressing;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setEnabled(enabled);
        }
    }

    protected void onClick(View v) {
        if (!isProgressing) {
            if (canAutoProgressing) {
                setProgressing(true);
            }
            clickListener.onClick(v);
        }
    }

    protected void invalidateView() {
        if (isProgressing) {
            visibleView(progressView);
            invisibleView(textView);
        } else {
            visibleView(textView);
            invisibleView(progressView);
        }
    }

    protected TextView createTextView(CharSequence text, ColorStateList textColor, int textSizePx) {
        TextView textView = new TextView(getContext());
        textView.setId(android.R.id.content);
        textView.setText(text);
        if (textColor != null) {
            textView.setTextColor(textColor);
        } else {
            textView.setTextColor(progressColor);
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx);
        textView.setLayoutParams(createTextLayoutParams());
        return textView;
    }

    protected LayoutParams createTextLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        return params;
    }

    protected ProgressBar createProgressView() {
        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true);
        progressBar.setIndeterminateDrawable(createProgressDrawable());
        progressBar.setLayoutParams(createProgressLayoutParams());
        return progressBar;
    }

    protected void invalidateProgressView() {
        progressView.setIndeterminateDrawable(createProgressDrawable());
        progressView.setLayoutParams(createProgressLayoutParams());
    }

    protected Drawable createProgressDrawable() {
        return new CircularProgressDrawable.Builder(getContext())
                .style(CircularProgressDrawable.STYLE_ROUNDED)
                .strokeWidth(progressWidth)
                .color(progressColor)
                .build();
    }

    protected LayoutParams createProgressLayoutParams() {
        LayoutParams params = new LayoutParams(progressSize, progressSize);
        params.gravity = Gravity.CENTER;
        return params;
    }

    private void init(Context context, TypedArray attrs) {
        ButterKnife.bind(this);

        Drawable backgroundDrawable = attrs.getDrawable(R.styleable.ProgressButton_android_background);
        if (backgroundDrawable != null) {
            this.backgroundDrawable = backgroundDrawable;
        }

        progressSize = attrs.getDimensionPixelSize(R.styleable.ProgressButton_progressSize, progressSize);
        progressWidth = attrs.getDimensionPixelOffset(R.styleable.ProgressButton_progressWidth, progressWidth);
        progressColor = attrs.getColor(R.styleable.ProgressButton_progressColor, progressColor);

        animForAppear = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animForDisappear = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);

        textView = createTextView(attrs.getString(R.styleable.ProgressButton_android_text),
                attrs.getColorStateList(R.styleable.ProgressButton_android_textColor),
                attrs.getDimensionPixelSize(R.styleable.ProgressButton_android_textSize, 0));

        String fontPath = attrs.getString(R.styleable.ProgressButton_fontPath);
        if (!TextUtils.isEmpty(fontPath)) {
            textView.setTypeface(Typeface.createFromAsset(context.getAssets(), fontPath));
        }
        progressView = createProgressView();
        progressView.setVisibility(View.GONE);

        addView(textView);
        addView(progressView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(this.backgroundDrawable);
        } else {
            setBackgroundDrawable(this.backgroundDrawable);
        }


        setEnabled(attrs.getBoolean(R.styleable.ProgressButton_android_enabled, true));
        super.setOnClickListener(this::onClick);
    }

    private void visibleView(View view) {
        view.post(() -> {
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
                view.startAnimation(animForAppear);
            }
        });
    }

    private void invisibleView(View view) {
        view.post(() -> {
            if (view.getVisibility() == View.VISIBLE) {
                view.startAnimation(animForDisappear);
                view.setVisibility(View.INVISIBLE);
            }
        });
    }
}