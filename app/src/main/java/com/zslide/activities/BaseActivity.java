package com.zslide.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.zslide.R;
import com.zslide.fragments.BaseFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO: 2018. 1. 2. 오픈 전 전부 이 액티비티에서 새로운 액티비티로 변경해야 함
@Deprecated
public abstract class BaseActivity extends AppCompatActivity {

    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;
    private View toolbarProgressView;
    private RequestManager glideRequestManager;

    /**
     * Google Analytics 화면 추적에 사용할 이름을 전달한다.
     * {@link BaseActivity}에서 Toolbar의 기본 타이틀로도 사용한다.
     * 직접 Toolbar의 타이틀을 변경하기 위해선 {@link #setTitle(CharSequence)} 혹은 {@link #setTitle(int)} 메소드를 사용한다.
     *
     * @return 화면 이름
     */
    @Deprecated
    public String getScreenName() {
        return "";
    }

    protected abstract int getLayoutResourceId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        int layoutResource = getLayoutResourceId();
        if (layoutResource != 0) {
            setContentView(layoutResource);
        }
        ButterKnife.bind(this);
        if (toolbar != null) {
            setupToolbar(toolbar);
            setupActionBar(getSupportActionBar());
        }
    }

    /*

        @Override
        protected void attachBaseContext(Context newBase) {
            super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        }


    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setupToolbar(Toolbar toolbar) {
        toolbarProgressView = ButterKnife.findById(toolbar, R.id.toolbarProgress);
        toolbar.setTitleTextAppearance(this, R.style.TextAppearance_AppBar);
        toolbar.setTitle(getScreenName());
        setSupportActionBar(toolbar);
    }

    protected void setupActionBar(ActionBar actionBar) {
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void setToolbarTitle(CharSequence title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    public CharSequence getToolbarTitle() {
        CharSequence title = "";
        if (toolbar != null) {
            title = toolbar.getTitle();
        }

        return title;
    }

    protected void setToolbarTitle(@StringRes int titleId) {
        setToolbarTitle(getString(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        if (toolbar != null) {
            setToolbarTitle(title);
        } else {
            super.setTitle(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        if (toolbar != null) {
            setToolbarTitle(titleId);
        } else {
            super.setTitle(titleId);
        }
    }

    public void showTitleProgress() {
        if (toolbar != null && toolbarProgressView != null) {
            toolbarProgressView.setVisibility(View.VISIBLE);
        }/* else {
            throw new IllegalStateException("showTitleProgress need to toolbar and toolbarProgressView");
        }*/
    }

    public void hideTitleProgress() {
        if (toolbar != null && toolbarProgressView != null) {
            toolbarProgressView.setVisibility(View.GONE);
        } /*else {
            throw new IllegalStateException("hideTitleProgress need to toolbar and toolbarProgressView");
        }*/
    }

    public RequestManager glide() {
        if (glideRequestManager == null) {
            glideRequestManager = createGlideRequestManager();
        }

        return glideRequestManager;
    }

    protected RequestManager createGlideRequestManager() {
        return Glide.with(this);
    }

    public interface OnReadyFragmentListener {
        void onReadyFragment(BaseFragment fragment);
    }
}
