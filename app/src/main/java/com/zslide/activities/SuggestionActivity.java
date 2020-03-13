package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.R;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.widget.RequestButton;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 16. 8. 30..
 */
public class SuggestionActivity extends BaseActivity {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.content) TextView contentView;
    @BindView(R.id.confirm) RequestButton confirmButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        confirmButton.setAutoProgressing(false);
    }

    @Override
    public String getScreenName() {
        return getString(R.string.label_suggestion);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_suggestion;
    }

    public String getSuggestionTitle() {
        return titleView.getText().toString().trim();
    }

    public String getSuggestionContent() {
        return contentView.getText().toString().trim();
    }

    @OnTextChanged({R.id.title, R.id.content})
    public void onTextChanged() {
        confirmButton.setEnabled(!TextUtils.isEmpty(getSuggestionTitle()) &&
                !TextUtils.isEmpty(getSuggestionContent()));
    }

    @OnClick(R.id.confirm)
    public void suggest() {
        confirmButton.setProgressing(true);
        ZummaApi.notice().suggest(getSuggestionTitle(), getSuggestionContent())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> confirmButton.setProgressing(false))
                .subscribe(o -> {
                    Toast.makeText(this, R.string.message_suggestion_thanks, Toast.LENGTH_LONG).show();
                    finish();
                }, ZummaApiErrorHandler::handleError);
    }
}
