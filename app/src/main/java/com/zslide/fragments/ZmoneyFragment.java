package com.zslide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.zslide.R;
import com.zslide.adapter.ZmoneyDashboardItemAdapter;
import com.zslide.data.model.FamilyZmoney;
import com.zslide.data.model.Zmoney;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.widget.BaseRecyclerView;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 2015. 10. 29..
 */
public class ZmoneyFragment extends BaseFragment {

    @BindView(R.id.container) SwipeRefreshLayout container;

    @BindView(R.id.items) BaseRecyclerView itemsView;

    public static final int TYPE_DAILY = 0;
    public static final int TYPE_MONTHLY = 1;

    private ZmoneyDashboardItemAdapter adapter;
    private DateTimeFormatter dateFormatter;

    private Observable<FamilyZmoney> request;

    public static ZmoneyFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);

        ZmoneyFragment fragment = new ZmoneyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int type = getArguments().getInt("type");

        switch (type) {
            case TYPE_MONTHLY:
                request = ZummaApi.zmoney().monthly();
                dateFormatter = DateTimeFormatter.ofPattern(getString(R.string.format_zmoney_dashboard_date_monthly), Locale.getDefault());
                break;
            case TYPE_DAILY:
            default:
                request = ZummaApi.zmoney().daily();
                dateFormatter = DateTimeFormatter.ofPattern(getString(R.string.format_zmoney_dashboard_date_daily));
                break;
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_zmoney;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        container.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.zmoney_dashboard_accent));
        adapter = new ZmoneyDashboardItemAdapter(getContext(), dateFormatter);
        adapter.setGlideRequestManager(glide());
        adapter.setOnItemSelectedListener((userZmoney, position) -> {
            if (adapter.isExpanded(position)) {
                itemsView.smoothScrollToPosition(position + adapter.getHeaderItemCount());
            }
        });
        itemsView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        itemsView.setAdapter(adapter);
        container.setOnRefreshListener(this::refresh);
        container.setRefreshing(true);
        refresh();
    }

    public void refresh() {
        request.subscribeOn(Schedulers.newThread())
                .map(FamilyZmoney::getZmoneys)
                .map(userZmoneyList -> {
                    Collections.sort(userZmoneyList, (Zmoney z1, Zmoney z2) -> {
                        if (z1.getTotal() > z2.getTotal()) {
                            return -1;
                        } else if (z1.getTotal() < z2.getTotal()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });

                    return userZmoneyList;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> container.setRefreshing(false))
                .subscribe(this::bind, ZummaApiErrorHandler::handleError);

    }

    private void bind(List<Zmoney> zmoneyList) {
        adapter.replaceAll(zmoneyList);
        adapter.notifyDataSetChanged();
        container.setRefreshing(false);
    }
}