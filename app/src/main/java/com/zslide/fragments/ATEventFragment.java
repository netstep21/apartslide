package com.zslide.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.IntentConstants;
import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.activities.ATEventActivity;
import com.zslide.activities.BaseActivity;
import com.zslide.data.UserManager;
import com.zslide.data.model.User;
import com.zslide.network.ApiConstants;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.EasySharedPreferences;
import com.bumptech.glide.signature.StringSignature;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 16. 2. 15..
 */
public class ATEventFragment extends BaseFragment {

    @BindView(R.id.img) ImageView imageView;
    @BindView(R.id.eventDetail) View eventDetailButton;
    @BindView(R.id.recruiterCode) EditText recruiterCodeView;
    @BindView(R.id.agreementContainer) View agreementContainer;
    @BindView(R.id.agreement) CheckBox agreementView;
    @BindView(R.id.agreementDetail) TextView agreementDetailView;
    @BindView(R.id.apply) Button applyButton;
    @BindView(R.id.simpleApply) Button simpleApplyButton;

    private String type;
    private String recruiterCode;

    public static ATEventFragment newInstance(String type, String recruiterCode) {
        Bundle args = new Bundle();
        args.putString(IntentConstants.EXTRA_TYPE, type);
        args.putString(IntentConstants.EXTRA_RECRUITER_CODE, recruiterCode);

        ATEventFragment instance = new ATEventFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString(IntentConstants.EXTRA_TYPE);
        recruiterCode = getArguments().getString(IntentConstants.EXTRA_RECRUITER_CODE);
        if (TextUtils.isEmpty(recruiterCode)) {
            String savedATEventType = EasySharedPreferences.with(getActivity())
                    .getString(ATEventActivity.KEY_AT_EVENT_TYPE);
            if (ATEventActivity.TYPE_ALL.equals(savedATEventType)
                    || type.equals(savedATEventType)) {
                recruiterCode = EasySharedPreferences.with(getActivity())
                        .getString(ATEventActivity.KEY_AT_EVENT_RECRUITER_CODE);
            }
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_at_event;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        switch (type) {
            case ATEventActivity.TYPE_SAMSUNG:
                eventDetailButton.setVisibility(View.GONE);
                recruiterCodeView.setVisibility(View.GONE);
                agreementContainer.setVisibility(View.GONE);
                applyButton.setVisibility(View.GONE);
                simpleApplyButton.setText(R.string.label_event_at_apply_samsung);
                simpleApplyButton.setEnabled(true);
                break;
            case ATEventActivity.TYPE_SHINHAN:
                applyButton.setText(R.string.label_event_at_apply);
                simpleApplyButton.setVisibility(View.GONE);
                break;
            case ATEventActivity.TYPE_KB:
                applyButton.setText(R.string.label_event_at_apply_phone);
                simpleApplyButton.setVisibility(View.VISIBLE);
                break;
        }

        glide().load(ApiConstants.BASE_URL + "/media/images/staticimage/img_" + type + ".png")
                .asBitmap()
                .signature(new StringSignature("" + System.currentTimeMillis() / (24 * 60 * 60 * 1000)))
                .into(imageView);

        agreementDetailView.setText(
                Html.fromHtml("<a href=#>" + getString(R.string.label_view_detail) + "</a>"));
        if (!TextUtils.isEmpty(recruiterCode)) {
            recruiterCodeView.setText(recruiterCode);
        }
    }

    @OnCheckedChanged(R.id.agreement)
    public void onChangedAgreement(boolean agree) {
        if (!type.equals(ATEventActivity.TYPE_SAMSUNG)) {
            applyButton.setEnabled(agree);
            simpleApplyButton.setEnabled(agree);
        }
    }

    @OnClick(R.id.agreementDetail)
    public void showTerms() {
        Navigator.startWebViewActivity(getContext(),
                agreementView.getText().toString(), ApiConstants.BASE_URL + "/app/agree");
    }

    public String getRecruiterCode() {
        return recruiterCodeView.getText().toString();
    }

    public void setRecruiterCode(String recruiterCode) {
        recruiterCodeView.setText(recruiterCode);
    }

    @OnClick(R.id.apply)
    public void apply() {
        checkCertifiedAndValidateRecruiterCode(() -> sendRecruiterCode(recruiterCode));
    }

    protected void checkCertifiedAndValidateRecruiterCode(Runnable action) {
        User user = UserManager.getInstance().getUserValue();
        if (user != null) {
            validateRecruiterCode(action);
            /*if (user.isCertified()) {
            } else {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.message_event_at_apply_require_certify_alert)
                        .setPositiveButton(R.string.label_redirect, (dialog, which) ->
                                Navigator.startCertificationActivity(getActivity()))
                        .setNegativeButton(R.string.label_cancel, null)
                        .show();
            }*/
        }
    }

    protected void validateRecruiterCode(Runnable action) {
        final String recruiterCode = getRecruiterCode();
        boolean emptyRecruiter = TextUtils.isEmpty(recruiterCode);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(getString(emptyRecruiter ?
                        R.string.message_confirm_recruiter_empty :
                        R.string.message_confirm_recruiter, recruiterCode))
                .setPositiveButton(R.string.label_confirm, (dialog, which) -> {
                    if (!emptyRecruiter) {
                        action.run();
                    }
                });
        if (!emptyRecruiter) {
            builder.setNegativeButton(R.string.label_cancel, null);
        }

        builder.show();
    }

    private void sendRecruiterCode(String recruiterCode) {
        ZummaApi.general().participateATEvent(recruiterCode, type)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        callCounselor();
                    } else {
                        showInvalidRecruiterCodeDialog(result.getMessage());
                    }
                }, ZummaApiErrorHandler::handleError);
    }

    private void callCounselor() {
        String telNumber = "";
        switch (type) {
            case ATEventActivity.TYPE_SHINHAN:
                telNumber = "1522-0284";
                break;
            case ATEventActivity.TYPE_KB:
                telNumber = "1833-8534";
                break;
        }
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telNumber)));
    }

    private void showInvalidRecruiterCodeDialog(String message) {
        if (!TextUtils.isEmpty(message)) {
            message = "모집인 코드를 확인하세요.";
        }

        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.label_confirm, null)
                .show();
    }

    @OnClick(R.id.eventDetail)
    public void startEventDetailPage() {
        Navigator.startWebViewActivity(getContext(), ApiConstants.BASE_URL + "/app/" + type + "/");
    }

    @OnClick(R.id.simpleApply)
    public void simpleApply() {
        BaseActivity activity = (BaseActivity) getActivity();
        switch (type) {
            case ATEventActivity.TYPE_KB:
                checkCertifiedAndValidateRecruiterCode(() ->
                        Navigator.startWebViewActivity(activity, activity.getToolbarTitle().toString(),
                                "https://m.kbcard.com/CXHMWAPC0009.cms?solicitorcode=7110172002"));
                break;
            case ATEventActivity.TYPE_SAMSUNG:
                Navigator.startWebViewActivity(activity, activity.getToolbarTitle().toString(),
                        "https://www.samsungcard.com/personal/services/apartment-card/apply/UHPPAP0101M0.jsp?affcode=co_zmslide");
                break;
        }
    }
}
