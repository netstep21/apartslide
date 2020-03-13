package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.Toast;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.data.AuthenticationManager;
import com.zslide.fragments.LeaveAgreeFragment;
import com.zslide.fragments.LeaveCompleteFragment;
import com.zslide.fragments.LeaveReasonFragment;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 16. 7. 21..
 */
public class LeaveActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, LeaveAgreeFragment.newInstance())
                .commit();
    }

    @Override
    public String getScreenName() {
        return "회원 탈퇴 신청";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_single_fragment;
    }

    public void next() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, LeaveReasonFragment.newInstance())
                .setCustomAnimations(
                        R.anim.slide_in_from_right, R.anim.slide_out_to_left,
                        R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                .addToBackStack(null)
                .commit();
    }

    public void leave(boolean zmoney, boolean zstore, boolean zmall, String extra) {
        ZummaApi.user().leave(zmoney, zstore, zmall, extra)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        // TODO: v4 연결 해야 함
                        AuthenticationManager.getInstance().leave(this);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, LeaveCompleteFragment.newInstance())
                                .setCustomAnimations(
                                        R.anim.slide_in_from_right, R.anim.slide_out_to_left,
                                        R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                                .commit();
                    } else {
                        Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, ZummaApiErrorHandler::handleError);
    }

    @Override
    public void onBackPressed() {
        if (!AuthenticationManager.getInstance().isLoggedIn()) {
            Navigator.startAuthActivity(this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
