package com.zslide.fragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zslide.activities.BaseActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import butterknife.ButterKnife;

@Deprecated
public abstract class BaseFragment extends Fragment {

    private RequestManager glideRequestManager;

    protected abstract
    @LayoutRes
    int getLayoutResourceId();

    protected abstract void setupLayout(View view, Bundle savedInstanceState);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResourceId(), container, false);
        ButterKnife.bind(this, view);
        setupLayout(view, savedInstanceState);
        view.setClickable(true);

        if (getActivity() instanceof BaseActivity.OnReadyFragmentListener) {
            ((BaseActivity.OnReadyFragmentListener) getActivity()).onReadyFragment(this);
        }

        return view;
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
}
