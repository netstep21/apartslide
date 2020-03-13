package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.zslide.R;
import com.zslide.adapter.BlockedMemberAdapter;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.widget.BaseRecyclerView;

import butterknife.BindView;

/**
 * Created by chulwoo on 16. 6. 7..
 */
public class ModifyBlockedMemberActivity extends BaseActivity {

    @BindView(R.id.list) BaseRecyclerView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Family family = UserManager.getInstance().getFamilyValue();
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(new BlockedMemberAdapter(this, family.getBlockedMembers()));
    }

    @Override
    public String getScreenName() {
        return getString(R.string.screen_family_modify_block);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_modify_blocked_member;
    }
}