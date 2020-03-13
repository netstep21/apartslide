package com.zslide.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zslide.R;
import com.zslide.ZummaApp;
import com.zslide.models.Event;
import com.zslide.utils.ZLog;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by jdekim43 on 2016. 1. 18..
 */
public class EventView extends LinearLayout {

    public static final String PREF_DISABLE_SHOWING_DATE = "Event.disableSowing.date";
    public static final String PREF_DISABLE_SHOWING = "Event.disableShowing";

    @BindView(R.id.image) ImageView noticeImageView;
    @BindView(R.id.participate) Button participateButton;
    @BindView(R.id.disableCheckBoxContainer) ViewGroup disableCheckBoxContainer;
    @BindView(R.id.disableShowing) CheckBox disableCheckBoxView;

    private ButtonClickListener buttonClickListener;
    private DisableSettingChangeListener disableSettingChangeListener;

    private Event event;

    public EventView(Context context) {
        this(context, null);
    }

    public EventView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_event_notice, this);
        ButterKnife.bind(this);
    }

    public void setEvent(Event event) {
        this.event = event;
        Glide.with(getContext()).load(event.getImageUrl())
                .into(noticeImageView);
        participateButton.setText(event.getLabel());
        participateButton.setTag(event.getTarget());

        if (TextUtils.isEmpty(event.getLabel()) || TextUtils.isEmpty(event.getTarget())) {
            participateButton.setVisibility(View.GONE);
        }

        switch (event.getType()) {
            case Event.TYPE_NEVER_SHOWING:
                disableCheckBoxView.setText(getResources().getString(R.string.label_event_type_never));
                break;
            case Event.TYPE_TODAY_NOT_SHOWING:
                disableCheckBoxView.setText(getResources().getString(R.string.label_event_type_today));
                break;
            case Event.TYPE_WEEK_NOT_SHOWING:
                disableCheckBoxView.setText(getResources().getString(R.string.label_event_type_week));
                break;
        }

        logEvent("read_event_popup");
    }

    public void setVisibleDisableCheckBoxView(boolean visible) {
        disableCheckBoxContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setButtonClickListener(ButtonClickListener listener) {
        this.buttonClickListener = listener;
    }

    public void setDisableSettingChangeListener(DisableSettingChangeListener listener) {
        this.disableSettingChangeListener = listener;
    }

    @OnCheckedChanged(R.id.disableShowing)
    protected void setDisableNoticeShowing(CompoundButton compoundButton, boolean isChecked) {
        if (disableSettingChangeListener != null) {
            disableSettingChangeListener.onDisable(isChecked);
        }
    }

    @OnClick(R.id.close)
    protected void onClickCloseButton(View v) {
        if (buttonClickListener != null) {
            buttonClickListener.onClick(v);
        }
    }

    @OnClick(R.id.participate)
    protected void onClickParticipate(View v) {
        try {
            String target = (String) v.getTag();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(target);
            intent.setData(uri);
            getContext().startActivity(intent);
        } catch (ClassCastException e) {
            ZLog.e(e);
        }

        logEvent("click_event_popup");
        if (buttonClickListener != null) {
            buttonClickListener.onClick(v);
        }
    }

    private void logEvent(String action) {
        Bundle bundle = new Bundle();
        switch (event.getType()) {
            case Event.TYPE_TODAY_NOT_SHOWING:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "today_not_showing");
                break;
            case Event.TYPE_NEVER_SHOWING:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "never_showing");
                break;
            case Event.TYPE_WEEK_NOT_SHOWING:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "a_week_not_showing");
                break;
        }
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(event.getId()));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event.getTitle());
        ZummaApp.get(getContext()).getAnalytics().logEvent(action, bundle);
    }

    public interface ButtonClickListener {
        void onClick(View v);
    }

    public interface DisableSettingChangeListener {
        void onDisable(boolean isDisable);
    }
}