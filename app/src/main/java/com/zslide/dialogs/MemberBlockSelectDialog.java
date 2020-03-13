package com.zslide.dialogs;

import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.widget.UserProfileView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

/**
 * Created by jdekim43 on 2016. 5. 26..
 */
public class MemberBlockSelectDialog extends BaseAlertDialog {

    private static final String DIALOG_THROW_MEMBER_CAUSE = "dialog.throw_member_cause";

    @BindView(R.id.memberList) LinearLayout memberContainer;

    @BindColor(R.color.subAccentColor) int nextButtonColor;
    @BindDimen(R.dimen.spacing_normal) int userItemSpacing;

    private List<User> memberList;
    private Set<User> selectedMemberList;

    public static MemberBlockSelectDialog newInstance() {
        return new MemberBlockSelectDialog();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_member_block_select;
    }

    @Override
    protected void setupLayout(View view) {
        memberList = new ArrayList<>();
        selectedMemberList = new HashSet<>();

        User user = UserManager.getInstance().getUserValue();
        Family family = UserManager.getInstance().getFamilyValue();
        memberList.addAll(family.getMembers());
        memberList.remove(user);

        for (User member : memberList) {
            if (family.isFamilyLeader(member)) {
                continue;
            }
            LinearLayout userContainer = new LinearLayout(getActivity());
            userContainer.setPadding(0, userItemSpacing, 0, userItemSpacing);

            UserProfileView userView = new UserProfileView(getActivity());
            userView.setUser(member);
            LinearLayout.LayoutParams userProfileParams =
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            userProfileParams.weight = 1;
            userContainer.addView(userView, userProfileParams);

            CheckBox userSelectView = new CheckBox(getActivity());
            userSelectView.setOnCheckedChangeListener((buttonView, isChecked) ->
                    onSelectChanged(member, isChecked));

            LinearLayout.LayoutParams checkParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            checkParams.gravity = Gravity.CENTER_VERTICAL;
            userContainer.addView(userSelectView, checkParams);

            userContainer.setOnClickListener(v ->
                    userSelectView.setChecked(!userSelectView.isChecked()));

            memberContainer.addView(userContainer);
        }
    }

    @Override
    protected void setupDialog(AlertDialog.Builder builder) {
        builder.setTitle(R.string.label_family_block);
        builder.setPositiveButton(R.string.label_next, null);
        builder.setNegativeButton(R.string.label_cancel, null);
    }

    @Override
    protected void setupButton(Button positiveButton, Button negativeButton, Button neutralButton) {
        super.setupButton(positiveButton, negativeButton, neutralButton);
        positiveButton.setTextColor(nextButtonColor);
        positiveButton.setOnClickListener(v -> {
            if (selectedMemberList.size() == 0) {
                Toast.makeText(getActivity(), "반드시 한명 이상을 선택하셔야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            MemberBlockCauseDialog causeDialog = MemberBlockCauseDialog.newInstance(selectedMemberList);
            causeDialog.show(getFragmentManager(), DIALOG_THROW_MEMBER_CAUSE);
            dismiss();
        });
    }

    protected void onSelectChanged(User member, boolean isSelected) {
        if (isSelected) {
            selectedMemberList.add(member);
        } else {
            selectedMemberList.remove(member);
        }
    }
}