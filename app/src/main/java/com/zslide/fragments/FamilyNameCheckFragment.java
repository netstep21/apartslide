package com.zslide.fragments;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.StringUtil;
import com.zslide.widget.RequestButton;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by chulwoo on 16. 5. 27..
 */
public class FamilyNameCheckFragment extends BaseFragment {

    @BindView(R.id.name) EditText familyNameView;
    @BindView(R.id.alert) TextView alertView;
    @BindView(R.id.next) RequestButton nextButton;

    private PublishSubject<String> familyNamePublisher = PublishSubject.create();

    public static FamilyNameCheckFragment newInstance() {
        return new FamilyNameCheckFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_family_name_check;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        familyNameView.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) ->
                !StringUtil.getFamilyNamePattern().matcher(source).matches() ? "" : null, new InputFilter.LengthFilter(10)});
    }

    public String getName() {
        return familyNameView.getText().toString().trim();
    }

    @OnTextChanged(R.id.name)
    public void onFamilyNameChanged() {
        nextButton.setEnabled(!getName().isEmpty() && getName().length() > 1);
    }

    @OnClick(R.id.next)
    public void next() {
        final String name = getName();
        nextButton.action(ZummaApi.user().searchFamily(name), family -> {
            if (family.isNull()) {
                alertView.setVisibility(View.GONE);
                familyNamePublisher.onNext(name);
            } else {
                alertView.setVisibility(View.VISIBLE);
            }
        }, ZummaApiErrorHandler::handleError);
    }

    public Observable<String> getFamilyNamePublisher() {
        return familyNamePublisher;
    }
}
