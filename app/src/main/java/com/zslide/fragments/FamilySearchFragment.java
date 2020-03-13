package com.zslide.fragments;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.model.Family;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.DisplayUtil;
import com.zslide.widget.FamilyCardView;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by chulwoo on 16. 5. 27..
 */
public class FamilySearchFragment extends BaseFragment {

    @BindColor(R.color.errorColor) int ERROR_COLOR;
    @BindView(R.id.keyword) EditText keywordView;
    @BindView(R.id.search) ImageButton searchButton;
    @BindView(R.id.guide) TextView guideView;
    @BindView(R.id.empty) TextView emptyView;
    @BindView(R.id.progress) CircularProgressBar progressView;
    @BindView(R.id.familyCard) FamilyCardView familyCardView;

    private PublishSubject<Family> familyPublisher = PublishSubject.create();

    public static FamilySearchFragment newInstance() {
        Bundle args = new Bundle();

        FamilySearchFragment instance = new FamilySearchFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_family_search;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        familyCardView.setFamilyJoinListener(family ->
                SimpleAlertDialog.newInstance(family.getName(), getString(R.string.message_family_joined))
                        .setOnConfirmListener(() -> familyPublisher.onNext(family))
                        .show(getFragmentManager(), "confirm_dialog"));
    }

    @OnTextChanged(R.id.keyword)
    public void onKeywordChanged(CharSequence keyword) {
        searchButton.setEnabled(keyword.length() >= 1);
    }

    @OnEditorAction(R.id.keyword)
    public boolean onEditorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchFamily();
            return true;
        }

        return false;
    }

    @OnClick(R.id.search)
    public void searchFamily() {
        DisplayUtil.hideKeyboard(getActivity());
        showProgress();

        String keyword = keywordView.getText().toString().trim();
        ZummaApi.user().searchFamily(keyword)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(family -> {
                    if (family.isNull()) {
                        bindEmptyFamily(keyword);
                    } else {
                        bindFamily(family);
                    }
                    hideProgress();
                }, e -> {
                    ZummaApiErrorHandler.handleError(e);
                    familyCardView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    guideView.setVisibility(View.VISIBLE);
                    hideProgress();
                });
    }

    private void bindFamily(Family family) {
        familyCardView.setVisibility(View.VISIBLE);
        familyCardView.setFamily(family);
    }

    private void bindEmptyFamily(String name) {
        emptyView.setVisibility(View.VISIBLE);
        String message = getString(R.string.message_empty_search_family, name);
        Spannable spannable = new SpannableString(message);
        spannable.setSpan(new ForegroundColorSpan(ERROR_COLOR),
                0, 2 + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        emptyView.setText(spannable, TextView.BufferType.SPANNABLE);
    }

    public void showProgress() {
        guideView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        familyCardView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressView.setVisibility(View.GONE);
    }

    public Observable<Family> getFamilyPublisher() {
        return familyPublisher;
    }
}
