package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.fragments.FamilyInviteFragment;
import com.zslide.fragments.FriendInviteFragment;
import com.zslide.fragments.InviteFragment;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by chulwoo on 15. 11. 26..
 */
public class InviteActivity extends com.zslide.view.base.BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    public static final String TYPE_FAMILY = "family";
    public static final String TYPE_FRIEND = "friend";

    private String type = TYPE_FRIEND;
    private boolean isLoadedInviteReward = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            type = getIntent().getStringExtra(IntentConstants.EXTRA_TYPE);
        }

        useToolbar(toolbar);
        if (TYPE_FAMILY.equals(type)) {
            toolbar.setTitle(R.string.label_invite_family);
        } else {
            toolbar.setTitle(R.string.label_invite_friend);
        }


        InviteFragment fragment;
        if (TYPE_FAMILY.equals(type)) {
            fragment = FamilyInviteFragment.newInstance();
        } else if (TYPE_FRIEND.equals(type)) {
            fragment = FriendInviteFragment.newInstance();
        } else {
            fragment = FriendInviteFragment.newInstance();
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, fragment, "fragment")
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isLoadedInviteReward) {
            UserManager.getInstance().getInviteInfo()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(inviteInfo -> {
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag("fragment");
                        if (fragment != null) {
                            ((InviteFragment) fragment).setInviteInfo(inviteInfo);
                            isLoadedInviteReward = true;
                        }
                    }, e -> {
                        handleError(e);
                        finish();
                    });
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_single_fragment;
    }
}
