package com.zslide.dialogs;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;

import java.util.List;
import java.util.Set;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 5. 26..
 */
public class MemberBlockCauseDialog extends BaseAlertDialog {

    @BindView(R.id.alert) TextView alertView;
    @BindViews({R.id.cause1Text, R.id.cause2Text, R.id.cause3Text}) List<TextView> causeTextViews;
    @BindViews({R.id.cause1, R.id.cause2, R.id.cause3}) List<RadioButton> causeButtons;

    @BindColor(R.color.colors_black_text) ColorStateList throwButtonColors;
    @BindColor(R.color.subAccentColor) int cancelButtonColor;

    Button throwButton;

    private Set<User> memberList;

    private String cause = "";

    public static MemberBlockCauseDialog newInstance(Set<User> memberList) {
        return new MemberBlockCauseDialog().setMemberList(memberList);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_member_block_cause;
    }

    @Override
    protected void setupLayout(View view) {
        StringBuilder memberNames = new StringBuilder();
        for (User member : memberList) {
            memberNames.append(member.getDisplayName(view.getContext()));
            memberNames.append("님, ");
        }
        if (!memberList.isEmpty()) {
            memberNames.delete(memberNames.length() - 2, memberNames.length());
        }
        alertView.setText(getString(R.string.message_throw_member_alert2, memberNames.toString()));
    }

    @Override
    protected void setupDialog(AlertDialog.Builder builder) {
        builder.setTitle(R.string.label_family_block);
        builder.setPositiveButton(R.string.label_throw, null);
        builder.setNegativeButton(R.string.label_cancel, null);
    }

    @Override
    protected void setupButton(Button positiveButton, Button negativeButton, Button neutralButton) {
        super.setupButton(positiveButton, negativeButton, neutralButton);
        throwButton = positiveButton;
        positiveButton.setTextColor(throwButtonColors);
        positiveButton.setEnabled(false);
        negativeButton.setTextColor(cancelButtonColor);
        positiveButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(cause)) {
                Toast.makeText(getActivity(), "내보내는 이유를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            block(memberList, cause);
        });
    }

    @OnClick(R.id.cause1Text)
    protected void selectCause1() {
        causeButtons.get(0).setChecked(true);
    }

    @OnClick(R.id.cause2Text)
    protected void selectCause2() {
        causeButtons.get(1).setChecked(true);
    }

    @OnClick(R.id.cause3Text)
    protected void selectCause3() {
        causeButtons.get(2).setChecked(true);
    }

    @OnTextChanged(R.id.cause3Text)
    protected void onCause3Changed(CharSequence cause) {
        this.cause = cause.toString();
    }

    @OnCheckedChanged({R.id.cause1, R.id.cause2, R.id.cause3})
    protected void causeInvalidate(CompoundButton v, boolean isChecked) {
        if (!isChecked) {
            return;
        }

        throwButton.setEnabled(true);
        ButterKnife.apply(causeButtons, new ButterKnife.Action<RadioButton>() {
            @Override
            public void apply(@NonNull RadioButton view, int index) {
                if (view == v) {
                    view.setChecked(true);
                    cause = causeTextViews.get(index).getText().toString();
                } else {
                    view.setChecked(false);
                }
            }
        });
    }

    private MemberBlockCauseDialog setMemberList(Set<User> memberList) {
        this.memberList = memberList;
        return this;
    }

    private void block(Set<User> list, String cause) {
        Family family = UserManager.getInstance().getFamilyValue();
        ZummaApi.user().blockMembers(family.getId(), list, cause)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fam -> {
                    UserManager.getInstance().fetchFamily().subscribe();
                    dismiss();
                }, ZummaApiErrorHandler::handleError);
    }
}
