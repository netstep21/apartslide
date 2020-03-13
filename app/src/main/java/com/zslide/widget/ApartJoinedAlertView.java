package com.zslide.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.models.Apartment;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action0;

/**
 * Created by jdekim43 on 2016. 6. 2..
 */
public class ApartJoinedAlertView extends LinearLayout {

    @BindView(R.id.state) TextView stateView;
    @BindView(R.id.toggleImage) ImageView toggleImageView;
    @BindView(R.id.messageContainer) View messageContainer;
    @BindView(R.id.message) TextView messageView;
    @BindView(R.id.request) Button requestButton;
    @BindView(R.id.requestCount) TextView requestCountView;
    private Apartment apartment;
    private State state;
    private boolean isExpand = true;
    private Action0 requestApartListener;
    private Action0 neverShowListener;

    public ApartJoinedAlertView(Context context) {
        this(context, null);
    }

    public ApartJoinedAlertView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ApartJoinedAlertView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_apart_joined, this);
        ButterKnife.bind(this);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ApartJoinedAlertView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.view_apart_joined, this);
        ButterKnife.bind(this);
        init();
    }

    @OnClick(R.id.titleContainer)
    public void toggleExpand() {
        setExpand(!isExpand);
    }

    public void setExpand(boolean expand) {
        this.isExpand = expand;
        expandInvalidate();
    }

    public void setApart(Apartment apartment) {
        this.apartment = apartment;
    }

    public void setState(State state) {
        if (apartment == null) {
            throw new IllegalStateException("apartment 가 먼저 설정되어야 합니다.");
        }

        this.state = state;
        setupMessage(state);
        setupButton(state);
    }

    public void setOnRequestApartListener(Action0 listener) {
        this.requestApartListener = listener;
    }

    public void setOnNeverShowListener(Action0 listener) {
        this.neverShowListener = listener;
    }

    @OnClick(R.id.request)
    protected void requestApart() {
        if (requestApartListener != null && (State.NEED.equals(state) || State.WAIT.equals(state))) {
            requestApartListener.call();
        } else if (neverShowListener != null && State.COMPLETE_NOTIFY.equals(state)) {
            neverShowListener.call();
        }
    }

    protected void init() {

    }

    protected void setupMessage(State state) {
        switch (state) {
            case HOUSE:
            case COMPLETE:
                setVisibility(View.GONE);
                break;
            case WAIT:
                setVisibility(View.VISIBLE);
                setExpand(false);
            case NEED:
                setVisibility(View.VISIBLE);
                stateView.setText(state.getTextResource());
                stateView.setTextColor(getContext().getResources().getColor(state.getColorResource()));
                messageView.setText(getContext().getString(R.string.message_notification_apartment_agree, apartment.getName()));
                requestCountView.setText(getContext().getString(R.string.format_apartment_agree_request, apartment.getRequestCount()));
                break;
            case COMPLETE_NOTIFY:
                setVisibility(View.VISIBLE);
                Date dateJoined = apartment.getDateJoined();
                Calendar c = Calendar.getInstance();
                c.setTime(dateJoined);
                int joinedYear = c.get(Calendar.YEAR);
                int joinedMonth = c.get(Calendar.MONTH) + 1;
                int joinedDay = c.get(Calendar.DAY_OF_MONTH);

                c.add(Calendar.MONTH, 1);
                int deductionStartYear = c.get(Calendar.YEAR);
                int deductionStartMonth = c.get(Calendar.MONTH) + 1;

                messageView.setText(getContext().getString(R.string.message_notification_apartment_agree_complete,
                        apartment.getName(), joinedYear, joinedMonth, joinedDay,
                        deductionStartYear, deductionStartMonth));
                break;
        }
    }

    protected void setupButton(State state) {
        switch (state) {
            case WAIT:
                requestButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                requestButton.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.spacing_smaller));
                requestButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_white, 0, 0, 0);
                requestButton.setBackgroundResource(R.drawable.btn_blue_normal);
                requestButton.setText(R.string.label_request_apartment_agree_complete);
                break;
            case NEED:
                requestButton.setTextColor(ContextCompat.getColor(getContext(), R.color.subAccentColor));
                requestButton.setBackgroundResource(R.drawable.btn_subaccent_border);
                requestButton.setText(getContext().getString(R.string.label_request_apartment_agree));
                break;
            case COMPLETE:
            case COMPLETE_NOTIFY:
                requestButton.setTextColor(ContextCompat.getColor(getContext(), R.color.subAccentColor));
                requestButton.setBackgroundResource(R.drawable.btn_subaccent_border);
                requestButton.setText(R.string.label_close_always);
                break;
        }
    }

    private void expandInvalidate() {
        messageContainer.setVisibility(isExpand ? View.VISIBLE : View.GONE);
        toggleImageView.setImageResource(isExpand
                ? R.drawable.ic_arrow_up_gray
                : R.drawable.ic_arrow_down_gray);
    }

    public enum State {
        NEED(R.string.none, R.color.black),
        WAIT(R.string.label_apartment_agree_wait, R.color.blue),
        COMPLETE_NOTIFY(R.string.label_apartment_agree_complete, R.color.subAccentColor),
        COMPLETE(R.string.none, R.color.black),
        HOUSE(R.string.none, R.color.black);

        private int textResource;
        private int colorResource;

        State(int textResource, int colorResource) {
            this.textResource = textResource;
            this.colorResource = colorResource;
        }

        public int getTextResource() {
            return textResource;
        }

        public int getColorResource() {
            return colorResource;
        }
    }
}
