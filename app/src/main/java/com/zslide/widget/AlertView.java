package com.zslide.widget;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zslide.Navigator;
import com.zslide.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chulwoo on 2016. 11. 17..
 * <p>
 * 메시지의 볼드 처리는 *, **로 가능
 */
public class AlertView extends RelativeLayout {

    @BindView(R.id.container) ViewGroup container;
    @BindView(R.id.message) TextView messageView;
    @BindView(R.id.button) TextView button;
    private OnButtonClickListener onButtonClickListener;
    private OnDismissListener onDismissListener;
    private boolean forceClose = false;

    private AlertView(Context context) {
        super(context);
        init(context);
    }

    public static AlertView forFamily(Context context) {
        return new AlertView.Builder(context)
                .setMessage(R.string.message_alert_family)
                .setOnButtonClickListener(() -> Navigator.startFamilyRegistrationActivity(context))
                .setButtonLabel(R.string.label_alert_family)
                .setColorResource(R.color.alert_family)
                .create();
    }

    public static AlertView forAuth(Context context) {
        return new AlertView.Builder(context)
                .setMessage(R.string.message_alert_auth)
                .setOnButtonClickListener(() -> Navigator.startAccountLinkActivity(context))
                .setButtonLabel(R.string.label_alert_auth)
                .setColorResource(R.color.alert_auth)
                .create();
    }

    private void init(Context context) {
        inflate(context, R.layout.view_alert, this);
        ButterKnife.bind(this);
        container.setClickable(true);
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.onButtonClickListener = listener;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    @OnClick(R.id.button)
    public void performButtonClick() {
        if (onButtonClickListener != null) {
            onButtonClickListener.onButtonClick();
        }
        dismiss();
    }

    @OnClick(R.id.close)
    public void close() {
        forceClose = true;
        dismiss();
    }

    public void dismiss() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(forceClose);
        }
        ((ViewGroup) getParent()).removeView(this);
    }

    public interface OnButtonClickListener {
        void onButtonClick();
    }

    public interface OnDismissListener {
        void onDismiss(boolean force);
    }

    public static class Builder {

        private final Params P;

        public Builder(Context context) {
            P = new Params(context);
        }

        /**
         * 볼드 처리는 <b></b>가 아니라 *, **로 가능
         *
         * @param resId 메시지 리소스 아이디
         * @return 현재 AlertView.Builder
         */
        public Builder setMessage(@StringRes int resId) {
            P.message = P.context.getText(resId);
            return this;
        }

        /**
         * 볼드 처리는 <b></b>가 아니라 *, **로 가능
         *
         * @param message 메시지
         * @return 현재 AlertView.Builder
         */
        public Builder setMessage(CharSequence message) {
            P.message = message;
            return this;
        }

        public Builder setButtonLabel(@StringRes int resId) {
            P.buttonLabel = P.context.getText(resId);
            return this;
        }

        public Builder setButtonLabel(CharSequence buttonLabel) {
            P.buttonLabel = buttonLabel;
            return this;
        }

        public Builder setColor(@ColorInt int color) {
            P.color = color;
            return this;
        }

        public Builder setColorResource(@ColorRes int colorRes) {
            P.color = ContextCompat.getColor(P.context, colorRes);
            return this;
        }

        public Builder setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
            P.onButtonClickListener = onButtonClickListener;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            P.onDismissListener = onDismissListener;
            return this;
        }

        public AlertView create() {
            // Context has already been wrapped with the appropriate theme.
            final AlertView view = new AlertView(P.context);
            P.apply(view);
            view.setOnButtonClickListener(P.onButtonClickListener);
            view.setOnDismissListener(P.onDismissListener);
            return view;
        }

        private class Params {
            private Context context;
            private int color;
            private CharSequence message;
            private CharSequence buttonLabel;
            private OnButtonClickListener onButtonClickListener;
            private OnDismissListener onDismissListener;

            Params(Context context) {
                this.context = context;
            }

            void apply(AlertView alertView) {
                alertView.container.setBackgroundColor(color);
                alertView.button.setTextColor(color);
                alertView.button.setText(Html.fromHtml("<b>" + buttonLabel.toString() + "</b>"));
                alertView.messageView.setText(Html.fromHtml(message.toString()
                        .replace("**", "</b>")
                        .replace("*", "<b>")
                        .replace("\n", "<br/>")));
            }
        }
    }
}
