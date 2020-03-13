package com.zslide.dialogs;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 5. 26..
 */
public class ChangeFamilyLeaderDialog extends BaseAlertDialog {

    @BindView(R.id.message) TextView messageView;
    @BindView(R.id.list) RadioGroup userListView;
    @BindColor(R.color.subAccentColor) int modifyButtonColor;
    @BindDimen(R.dimen.spacing_normal) int itemSpacing;
    private Context context;
    private Family family;
    private User selectedUser;

    public static ChangeFamilyLeaderDialog newInstance() {
        return new ChangeFamilyLeaderDialog();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_change_family_leader;
    }

    @Override
    protected void setupLayout(View view) {
        context = view.getContext();
        User leader = UserManager.getInstance().getUserValue();
        Family family = UserManager.getInstance().getFamilyValue();
        messageView.setText(R.string.message_modify_family_leader_alert);
        for (User member : family.getMembers()) {
            if (member.getId() == leader.getId()) {
                continue;
            }

            RadioButton userButton = new RadioButton(context);
            userButton.setHint(R.string.empty_name);
            userButton.setHintTextColor(ContextCompat.getColor(context, R.color.gray_7));
            userButton.setText(member.getDisplayName(context));
            userButton.setTag(member);
            userButton.setPadding(0, itemSpacing, 0, itemSpacing);
            userListView.addView(userButton,
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        userListView.setOnCheckedChangeListener((group, checkedId) ->
                selectedUser = (User) ButterKnife.findById(group, checkedId).getTag());
        ((RadioButton) userListView.getChildAt(0)).setChecked(true);
    }

    @Override
    protected void setupDialog(AlertDialog.Builder builder) {
        builder.setTitle(R.string.label_family_change_family_leader);
        builder.setPositiveButton(R.string.label_modify_president, null);
        builder.setNegativeButton(R.string.label_cancel, null);
    }

    @Override
    protected void setupButton(Button positiveButton, Button negativeButton, Button neutralButton) {
        super.setupButton(positiveButton, negativeButton, neutralButton);
        positiveButton.setTextColor(modifyButtonColor);
        positiveButton.setOnClickListener(v ->
                ZummaApi.user().changeFamilyLeader(selectedUser.getId())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> {
                            // TODO: 로직 상 바로 업데이트 가능하나 changeFamilyLeader의 User 결과가 달라 불가능
                            UserManager.getInstance().fetchFamily().subscribe(this::dismiss);
                        }, ZummaApiErrorHandler::handleError));
    }
}
