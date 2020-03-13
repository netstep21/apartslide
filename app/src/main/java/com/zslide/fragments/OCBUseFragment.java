package com.zslide.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.activities.OCBActivity;
import com.zslide.dialogs.OCBConfirmDialog;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.models.OCB;
import com.zslide.network.ApiConstants;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.StringUtil;
import com.zslide.widget.RequestButton;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 16. 06. 27..
 */
public abstract class OCBUseFragment extends BaseFragment {

    private static final int MIN_POINT = 1000;
    private static final int MAX_POINT = 100000;
    @BindView(R.id.ocbUseContainer) ViewGroup ocbUseContainer;
    @BindView(R.id.remainOCB) TextView remainPointView;
    @BindView(R.id.useOCB) EditText useOCBView;
    @BindView(R.id.termsAgree) CheckBox termsAgreeView;
    @BindView(R.id.terms) TextView termsView;
    @BindView(R.id.use) RequestButton useButton;
    @BindView(R.id.alertFees) TextView alertFeesView;
    private OCB ocb;
    private String type;

    protected abstract String getAuthId();

    protected abstract String getAuthPassword();

    protected void setType(String type) {
        this.type = type;
        if (OCBActivity.TYPE_OCP.equals(type)) {
            alertFeesView.setVisibility(View.GONE);
        } else {
            alertFeesView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        useOCBView.addTextChangedListener(new OCBTextWatcher(useOCBView));
        termsView.setText(Html.fromHtml("<a href=\"#\">개인정보 수집 및 이용</a>"));
    }

    protected void bind(OCB ocb) {
        this.ocb = ocb;
        ocbUseContainer.setVisibility(View.VISIBLE);
        remainPointView.setText(getContext().getString(R.string.format_ocb, ocb.getPoint()));
    }

    @OnClick(R.id.useAll)
    protected void useAll() {
        if (ocb != null) {
            int point = ocb.getPoint();
            if (point > MAX_POINT) {
                point = MAX_POINT;
            }
            useOCBView.setText(StringUtil.format("%d", point));
        }
    }

    @OnClick(R.id.terms)
    public void showTerms() {
        Navigator.startWebViewActivity(getContext(), ApiConstants.BASE_URL + "/ocb/agree/");
    }

    @OnClick(R.id.use)
    protected void use() {
        if (!termsAgreeView.isChecked()) {
            SimpleAlertDialog.newInstance(getString(R.string.message_agree_terms))
                    .show(getFragmentManager(), "alert");
            return;
        }

        if (getUseOCBPoint() < MIN_POINT) {
            SimpleAlertDialog.newInstance(getString(R.string.message_ocb_min, MIN_POINT))
                    .show(getFragmentManager(), "alert");
            return;
        }

        double feesPercentage = 0;
        if (OCBActivity.TYPE_OCB.equals(type)) {
            feesPercentage = 2.2f;
        }

        OCBConfirmDialog.newInstance(getUseOCBPoint(), feesPercentage)
                .setOnConfirmListener(this::use)
                .show(getFragmentManager(), "ocb_confirm_dialog");
    }

    public void use(int point) {
        useButton.setProgressing(true);
        ZummaApi.ocb().use(getAuthId(), getAuthPassword(), ocb, point)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ocb -> {
                    useButton.setProgressing(false);
                    if (ocb.isSuccess()) {
                        onSuccessUse();
                    } else {
                        onFailureUse(ocb.getMessage());
                    }
                }, e -> {
                    useButton.setProgressing(false);
                    ZummaApiErrorHandler.handleError(e);
                });
    }

    protected void onSuccessUse() {
        Activity activity = getActivity();
        Toast.makeText(activity, "포인트 사용이 완료됐습니다.", Toast.LENGTH_SHORT).show();
        activity.finish();
    }

    protected void onFailureUse(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public int getUseOCBPoint() {
        try {
            return Integer.parseInt(useOCBView.getText().toString().replace(",", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private class OCBTextWatcher implements TextWatcher {
        private EditText editText;
        private String text = "";
        private boolean isEditing = false;

        public OCBTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isEditing) {
                return;
            }
            isEditing = true;

            int point = 0;
            try {
                point = Integer.parseInt(s.toString().replace(",", ""));
            } catch (Exception e) {
                // do nothing
            }
            if (point > MAX_POINT) {
                s = StringUtil.format("%d", MAX_POINT);
            }

            text = makeStringComma(s.toString().replace(",", ""));
            editText.setText(text);
            Editable e = editText.getText();
            Selection.setSelection(e, text.length());
            isEditing = false;
        }

        protected String makeStringComma(String str) {
            if (str.length() == 0) {
                return "";
            }

            long value = Long.parseLong(str);
            DecimalFormat format = new DecimalFormat("###,###");
            return format.format(value);
        }
    }
}
