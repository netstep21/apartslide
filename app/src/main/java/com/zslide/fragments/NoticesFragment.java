package com.zslide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zslide.Navigator;
import com.zslide.ZummaApp;
import com.zslide.models.Notice;
import com.zslide.network.ZummaApi;
import com.zslide.adapter.NoticeAdapter;
import com.zslide.adapter.PaginationAdapter;

import java.util.Date;

/**
 * Created by chulwoo on 2015. 10. 29..
 */
public class NoticesFragment extends PaginationFragment<Notice> {

    public static NoticesFragment newInstance() {
        Bundle args = new Bundle();

        NoticesFragment instance = new NoticesFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZummaApp app = (ZummaApp) getActivity().getApplicationContext();
        app.setLastNoticeReadDate(new Date());
    }

    @Override
    public PaginationAdapter<Notice> createPaginationAdapter() {
        NoticeAdapter adapter = new NoticeAdapter(ZummaApi.notice()::items);
        adapter.setOnItemSelectedListener((notice, position) -> {
            Navigator.startNoticeActivity(getActivity(), notice);
        });
        return adapter;
    }
}
