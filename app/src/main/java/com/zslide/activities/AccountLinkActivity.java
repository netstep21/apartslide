package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zslide.R;
import com.zslide.fragments.AccountLinkFragment;

/**
 * Created by chulwoo on 2016. 11. 23..
 */

public class AccountLinkActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, AccountLinkFragment.newInstance())
                .commit();
    }

    @Override
    public String getScreenName() {
        return "이메일 등록";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_single_fragment;
    }

}
