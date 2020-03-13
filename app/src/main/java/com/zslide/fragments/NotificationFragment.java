package com.zslide.fragments;

import android.os.Bundle;
import android.view.View;

import com.zslide.R;
import com.zslide.models.Notification;
import com.zslide.network.ZummaApi;
import com.zslide.adapter.NotificationAdapter;
import com.zslide.adapter.PaginationAdapter;

import butterknife.BindDimen;

/**
 * Created by jdekim43 on 2016. 3. 16..
 */
public class NotificationFragment extends PaginationFragment<Notification> {

    @BindDimen(R.dimen.spacing_normal) int SPACING_NORMAL;

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        super.setupLayout(view, savedInstanceState);
        getPaginationView().setPadding(0, SPACING_NORMAL, 0, 0);
        getPaginationView().setClipToPadding(false);
    }

    @Override
    public PaginationAdapter<Notification> createPaginationAdapter() {
        return new NotificationAdapter(ZummaApi.general()::notifications, glide());
    }
}
