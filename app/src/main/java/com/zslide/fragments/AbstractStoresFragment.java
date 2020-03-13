package com.zslide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zslide.models.Dong;
import com.zslide.models.ZummaStore;

/**
 * Created by chulwoo on 2017. 8. 2..
 */

public abstract class AbstractStoresFragment extends PaginationFragment<ZummaStore> {

    public interface StoresFragmentCallback {
        Dong getLocation();
    }

    protected Dong dong;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setAutoLoading(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!getUserVisibleHint()) {
            return;
        }

        if (needRefresh()) {
            refresh();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            onResume();
        }
    }

    protected boolean needRefresh() {
        Dong location = getLocation();
        if (location != null && !location.equals(this.dong)) {
            return true;
        }

        return false;
    }

    @Override
    public void refresh() {
        dong = getLocation();

        super.refresh();
    }

    public Dong getLocation() {
        if (!(getActivity() instanceof StoresFragmentCallback)) {
            throw new IllegalStateException("Activity must be impletement " + StoresFragmentCallback.class);
        }

        return ((StoresFragmentCallback) getActivity()).getLocation();
    }

}
