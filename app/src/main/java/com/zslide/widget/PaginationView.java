package com.zslide.widget;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.zslide.adapter.PaginationAdapter;

import butterknife.ButterKnife;

/**
 * Created by chulwoo on 16. 4. 19..
 */
public class PaginationView<T> extends BaseRecyclerView {

    protected PaginationAdapter<T> adapter;

    public PaginationView(Context context) {
        this(context, null);
    }

    public PaginationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaginationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        ButterKnife.bind(this);
        setLayoutManager(new LinearLayoutManager(context));
        setClipToPadding(false);
        setPadding(0, 0, 0, 200);
        setOverScrollMode(OVER_SCROLL_NEVER);
        ((DefaultItemAnimator) getItemAnimator()).setSupportsChangeAnimations(true);
    }

    @Override
    public PaginationAdapter<T> getAdapter() {
        return adapter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setAdapter(Adapter adapter) {
        if (!(adapter instanceof PaginationAdapter)) {
            throw new IllegalArgumentException("PaginationView's adapter must be has PaginationAdapter");
        }
        super.setAdapter(adapter);
        this.adapter = (PaginationAdapter<T>) adapter;
    }

    @Override
    public void setEmptyView(View view) {
        // TODO: 동작확인
    }

    public void setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
        adapter.setOnItemSelectedListener(onItemSelectedListener);
    }

    public void refresh(boolean force) {
        adapter.refresh(force);
    }
}

