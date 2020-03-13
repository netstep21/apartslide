package com.zslide.fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zslide.R;
import com.zslide.models.LevelCouponLog;
import com.zslide.network.ZummaApi;
import com.zslide.adapter.PaginationAdapter;
import com.zslide.widget.BaseRecyclerView;
import com.zslide.widget.LevelBenefitLogView;

/**
 * Created by chulwoo on 16. 8. 8..
 */
public class LevelBenefitLogsFragment extends PaginationFragment<LevelCouponLog> {

    public static LevelBenefitLogsFragment newInstance() {
        Bundle args = new Bundle();

        LevelBenefitLogsFragment fragment = new LevelBenefitLogsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        super.setupLayout(view, savedInstanceState);
        view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
    }

    @Override
    protected PaginationAdapter<LevelCouponLog> createPaginationAdapter() {
        return new LevelBenefitLogAdapter(ZummaApi.user()::couponLogs);
    }

    class LevelBenefitLogAdapter extends PaginationAdapter<LevelCouponLog> {

        LevelBenefitLogAdapter(Loader<LevelCouponLog> loader) {
            super(loader);
        }

        @Override
        protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
            View view = new LevelBenefitLogView(parent.getContext());
            view.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new LevelCouponLogItemView(view);
        }

        @Override
        protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((LevelBenefitLogView) holder.itemView).bind(getItem(position));
        }

        class LevelCouponLogItemView extends BaseRecyclerView.BaseViewHolder {

            LevelCouponLogItemView(View itemView) {
                super(itemView);
            }
        }
    }
}
