package com.zslide.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.activities.InviteActivity;
import com.zslide.adapter.MemberAdapter;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.dialogs.ChangeFamilyLeaderDialog;
import com.zslide.dialogs.MemberBlockSelectDialog;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jdekim43 on 16. 05. 26..
 */
public class FamilySettingsMemberView extends LinearLayout {

    private static final String DIALOG_THROW_MEMBER_SELECT = "dialog.throw_member_select";
    private static final String DIALOG_MODIFY_FAMILY_LEADER = "dialog.modify_family_leader";

    @BindView(R.id.members) RecyclerView membersView;
    @BindView(R.id.familyCount) TextView familyCountView;
    @BindView(R.id.settings) TextView familySettingsView;

    @BindDimen(R.dimen.spacing_large) int itemSpacing;

    private MemberAdapter memberAdapter;
    private User user;
    private Family family;

    public FamilySettingsMemberView(Context context) {
        this(context, null);
    }

    public FamilySettingsMemberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.view_family_members, this);
        ButterKnife.bind(this);
        memberAdapter = new MemberAdapter();
        membersView.setLayoutManager(new LinearLayoutManager(context));
        membersView.addItemDecoration(new LinearSpacingItemDecoration(itemSpacing));
        membersView.setItemAnimator(null);
        membersView.setAdapter(memberAdapter);
    }

    public void setInfo(User user, Family family) {
        if (user == null || family == null) {
            return;
        }

        this.user = user;
        this.family = family;

        if (family.isFamilyLeader(user) && (family.getBlockedMembers().size() > 0 || family.getMembers().size() > 1)) {
            familySettingsView.setVisibility(View.VISIBLE);
        } else {
            familySettingsView.setVisibility(View.GONE);
        }
        familyCountView.setText(getContext().getString(R.string.label_family_members, family.getMembers().size()));

        memberAdapter.setFamily(family);
        memberAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.invite)
    public void inviteFamily() {
        Navigator.startInviteActivity(getContext(), InviteActivity.TYPE_FAMILY);
    }

    @OnClick(R.id.settings)
    public void showSettingsPopupMenu(View v) {
        if (!family.isFamilyLeader(user)) {
            return;
        }

        PopupMenu popup = new PopupMenu(getContext(), v);//v는 클릭된 뷰를 의미
        Menu menu = popup.getMenu();
        ((Activity) getContext()).getMenuInflater().inflate(R.menu.family_settings, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            int itemId = item.getItemId();
            if (itemId == R.id.actionModifyBlockedMember) {
                item.setVisible(family.getBlockedMembers().size() > 0);
            } else {
                item.setVisible(family.getMembers().size() > 1);
            }
        }

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.actionModifyFamilyPresident:
                    showModifyFamilyLeaderDialog();
                    break;
                case R.id.actionThrowMember:
                    showMemberBlockDialog();
                    break;
                case R.id.actionModifyBlockedMember:
                    modifyBlockedMember();
                    break;
            }
            return false;
        });
        popup.show();
    }

    public void showModifyFamilyLeaderDialog() {
        ChangeFamilyLeaderDialog.newInstance()
                .show(((AppCompatActivity) getContext()).getSupportFragmentManager(), DIALOG_MODIFY_FAMILY_LEADER);
    }

    public void showMemberBlockDialog() {
        MemberBlockSelectDialog.newInstance()
                .show(((AppCompatActivity) getContext()).getSupportFragmentManager(), DIALOG_THROW_MEMBER_SELECT);
    }

    public void modifyBlockedMember() {
        Navigator.startModifyBlockedMemberActivity(getContext());
    }
}