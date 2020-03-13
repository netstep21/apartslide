package com.zslide.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.zslide.R;
import com.zslide.adapter.PaginationAdapter;
import com.zslide.widget.PaginationView;
import com.zslide.widget.UpButton;

import butterknife.BindView;

/**
 * Created by chulwoo on 16. 4. 19..
 */
public abstract class PaginationFragment<Data> extends BaseFragment implements UpButton.UsableUpButton {

    @BindView(R.id.paginationContainer) FrameLayout paginationContainer;

    private boolean autoLoading = true;
    private PaginationAdapter<Data> paginationAdapter;
    private PaginationView<Data> paginationView;


    protected abstract PaginationAdapter<Data> createPaginationAdapter();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_pagination;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        Context context = getContext();
        paginationView = new PaginationView<>(context);
        paginationAdapter = createPaginationAdapter();
        paginationView.setAdapter(paginationAdapter);
        paginationContainer.addView(paginationView,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        UpButton upButton = new UpButton(getActivity());
        upButton.setBehaviorTarget(paginationView);
        paginationContainer.addView(upButton,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.BOTTOM | GravityCompat.END));
    }

    protected void setAutoLoading(boolean autoLoading) {
        this.autoLoading = autoLoading;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (autoLoading) {
            refresh();
        }
    }

    public PaginationAdapter<Data> getPaginationAdapter() {
        return paginationAdapter;
    }

    public PaginationView<Data> getPaginationView() {
        return paginationView;
    }

    public void refresh() {
        getPaginationView().refresh(true);
    }

    @Override
    public View getScrollableView() {
        return paginationView;
    }
}
