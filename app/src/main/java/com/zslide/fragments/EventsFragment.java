package com.zslide.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.adapter.EventAdapter;
import com.zslide.adapter.PaginationAdapter;
import com.zslide.data.model.EventBanner;
import com.zslide.models.PaginationData;
import com.zslide.network.ZummaApi;
import com.zslide.utils.DeepLinkRouter;
import com.zslide.utils.ZLog;
import com.zslide.widget.LinearSpacingItemDecoration;

import java.util.List;

import butterknife.BindDimen;
import rx.Observable;

/**
 * Created by jdekim43 on 2016. 1. 26..
 */
public class EventsFragment extends PaginationFragment<EventBanner> {

    public static final int TYPE_ON_GOING = 0;
    public static final int TYPE_COMPLETED = 1;

    @BindDimen(R.dimen.spacing_large) int SPACING_LARGE;

    private int type = TYPE_ON_GOING;

    public static EventsFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(IntentConstants.EXTRA_TYPE, type);

        EventsFragment instance = new EventsFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(IntentConstants.EXTRA_TYPE);
        }
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        super.setupLayout(view, savedInstanceState);
        getPaginationView().addItemDecoration(new LinearSpacingItemDecoration(SPACING_LARGE));
        getPaginationView().setOnItemSelectedListener((eventBanner, position) -> {
            if (!eventBanner.isActive()) {
                return;
            }

            try {
                DeepLinkRouter.route(getActivity(), Uri.parse(eventBanner.getTarget()), false);
            } catch (ClassCastException e) {
                ZLog.e(e);
            }
        });
    }

    public Observable<PaginationData<EventBanner>> load(int page) {
        switch (type) {
            case TYPE_ON_GOING:
                return ZummaApi.event().items().map(this::convertPaginationData);
            case TYPE_COMPLETED:
                return ZummaApi.event().completedItems(page);
            default:
                throw new IllegalArgumentException("not supported type");
        }
    }

    @Override
    protected PaginationAdapter<EventBanner> createPaginationAdapter() {
        return new EventAdapter(this::load, glide());
    }

    private PaginationData<EventBanner> convertPaginationData(List<EventBanner> items) {
        return new PaginationData<>(items);
    }
}
