package com.zslide.fragments;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.models.AppValue;
import com.zslide.network.ApiConstants;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;

import java.util.regex.Pattern;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 16. 4. 8..
 */
public class RecommendCodeInputFragment extends BaseFragment {

    @BindString(R.string.label_terms_use) String USE_TERMS_LABEL;
    @BindString(R.string.label_terms_privacy) String PRIVACY_TERMS_LABEL;

    @BindView(R.id.message) TextView messageView;
    @BindView(R.id.recommendCode) EditText recommendCodeView;
    @BindView(R.id.terms) TextView termsView;
    private OnCompleteListener onCompleteListener;

    public static RecommendCodeInputFragment newInstance() {
        return new RecommendCodeInputFragment();
    }

    public RecommendCodeInputFragment setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
        return this;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_recommend_code_input;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        Linkify.TransformFilter transform = (match, url) -> "";

        Pattern useTermsPattern = Pattern.compile(USE_TERMS_LABEL);
        Pattern privacyTermsPattern = Pattern.compile(PRIVACY_TERMS_LABEL);

        String useTermsUrl = ApiConstants.BASE_URL + getString(R.string.path_terms_use);
        String privacyTermsUrl = ApiConstants.BASE_URL + getString(R.string.path_terms_privacy);

        Linkify.addLinks(termsView, useTermsPattern, useTermsUrl, null, transform);
        Linkify.addLinks(termsView, privacyTermsPattern, privacyTermsUrl, null, transform);

        ZummaApi.general().inviteReward()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(AppValue::getValue)
                .map(value -> getString(R.string.message_input_recommender, value))
                .subscribe(messageView::setText, ZummaApiErrorHandler::handleError);
    }

    public String getRecommendCode() {
        return recommendCodeView.getText().toString().trim();
    }

    @OnEditorAction(R.id.recommendCode)
    boolean onEditorAction(int actionId) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
                complete();
                return true;
        }
        return false;
    }

    @OnClick(R.id.complete)
    public void complete() {
        if (onCompleteListener != null) {
            onCompleteListener.onComplete();
        }
    }

    public interface OnCompleteListener {
        void onComplete();
    }
}
