package com.zslide.view.base;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zslide.network.ZummaApiErrorHandler;
import com.trello.navi2.component.support.NaviFragment;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by chulwoo on 16. 5. 3..
 * <p>
 * Updated by chulwoo on 17. 12. 1..
 * <p>
 * MVVM 적용
 */
public abstract class BaseFragment extends NaviFragment {

    /**
     * LifecycleProvider, compose를 이용할 것.
     *
     * @param disposable
     */
    @Deprecated
    protected void bind(@NonNull Disposable disposable) {
        if (Lifecycle.State.CREATED.equals(getLifecycle().getCurrentState())) {
            if (onStartDisposables != null) {
                onStartDisposables.add(disposable);
            }
        } else {
            if (onResumeDisposables != null) {
                onResumeDisposables.add(disposable);
            }
        }
    }

    @Deprecated private CompositeDisposable onStartDisposables;
    @Deprecated private CompositeDisposable onResumeDisposables;

    @LayoutRes
    protected abstract int getLayoutResource();

    @Override
    public void onStart() {
        super.onStart();
        onStartDisposables = new CompositeDisposable();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(getLayoutResource(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onResumeDisposables = new CompositeDisposable();
        if (getUserVisibleHint()) {
            onResumeWithVisibleHint();
        }
    }

    /**
     * TabLayout 안에서 사용하는 Fragment일 경우, 보이지 않아도 onResume이 호출되므로,
     * 실제로 탭을 눌러 화면에 보여졌을 때 작업을 진행하고 싶다면 해당 메소드를 오버라이드 해 사용할 것.
     */
    public void onResumeWithVisibleHint() {
        // do nothing
    }

    @Override
    public void onPause() {
        if (onResumeDisposables != null) {
            onResumeDisposables.clear();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (onStartDisposables != null) {
            onStartDisposables.clear();
        }
        super.onStop();
    }

    public void handleError(Throwable t) {
        Activity activity = getActivity();
        if (activity != null) {
            if (activity instanceof BaseActivity) {
                ((BaseActivity) activity).handleError(t);
            } else if (activity instanceof com.zslide.activities.BaseActivity) {
                ZummaApiErrorHandler.handleError(t);
            }
        }
    }
}