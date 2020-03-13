package com.zslide.view.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zslide.data.ErrorHandler;
import com.zslide.data.remote.exception.RemoteErrorHandler;
import com.trello.navi2.component.support.NaviAppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public abstract class BaseActivity extends NaviAppCompatActivity {

    private static final String SESSION_EXPIRED_DIALOG = "session_expired";

    // TODO: 더 좋은 구조 생각
    private ErrorHandler errorHandler;

    @LayoutRes
    protected int getLayoutResource() {
        return 0;
    }

    public BaseActivity() {
        errorHandler = new RemoteErrorHandler(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        int layoutResource = getLayoutResource();
        if (layoutResource != 0) {
            setContentView(layoutResource);
        }
        ButterKnife.bind(this);
    }

    public void handleError(Throwable throwable) {
        errorHandler.handleError(throwable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /*if (this instanceof MainActivity) {
                    return false;
                }*/
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void useToolbar(Toolbar toolbar) {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void showSessionExpiredDialog() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(SESSION_EXPIRED_DIALOG) == null) {
            SessionExpiredDialog.newInstance().show(fm, SESSION_EXPIRED_DIALOG);
        }
    }

    public void showSessionExpiredDialog(String message) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(SESSION_EXPIRED_DIALOG) == null) {
            SessionExpiredDialog.newInstance(message).show(fm, SESSION_EXPIRED_DIALOG);
        }
    }
}
