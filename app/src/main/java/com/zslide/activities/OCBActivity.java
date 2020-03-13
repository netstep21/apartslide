package com.zslide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.fragments.OCBCardFragment;
import com.zslide.fragments.OCBPhoneFragment;
import com.zslide.utils.ZLog;
import com.zslide.widget.ZummaTabLayout;

import butterknife.BindView;

/**
 * Created by chulwoo on 16. 6. 27..
 */
public class OCBActivity extends BaseActivity {

    public static final String TYPE_OCB = "default";
    public static final String TYPE_OCP = "plus";

    @BindView(R.id.tabLayout) ZummaTabLayout tabView;
    @BindView(R.id.viewPager) ViewPager pagerView;

    @Override
    public String getScreenName() {
        return "OK캐쉬백";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_ocb;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String type = getIntent().getStringExtra(IntentConstants.EXTRA_TYPE);
        pagerView.setAdapter(new PagerAdapter(getSupportFragmentManager(), type));
        tabView.setupWithViewPager(pagerView);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZLog.e(this, "onNewIntent: " + intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ZLog.e(this, "onActivityResult:" + data);
    }

    class PagerAdapter extends FragmentPagerAdapter {

        private String type;
        private String[] titles = new String[]{"카드번호로 인증", "휴대폰번호로 인증"};

        public PagerAdapter(FragmentManager fm, String type) {
            super(fm);
            this.type = type;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return OCBCardFragment.newInstance(type);
                case 1:
                    return OCBPhoneFragment.newInstance(type);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
