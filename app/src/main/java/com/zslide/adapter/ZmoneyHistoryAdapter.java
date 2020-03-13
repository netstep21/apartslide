package com.zslide.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.models.ZmoneyHistory;
import com.zslide.widget.BaseRecyclerView;

import butterknife.BindView;

/**
 * TODO: pagination 이상하게 사용했음.
 */
public class ZmoneyHistoryAdapter extends PaginationAdapter<ZmoneyHistory> {

    public interface PrevYearLoader {
        void loadPrevYear();
    }

    private final PrevYearLoader prevYearLoader;

    public ZmoneyHistoryAdapter(PrevYearLoader loader) {
        super();
        this.prevYearLoader = loader;
        this.loadingOffset = 12;
        this.autoLoadMore = false;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zmoney_history, parent, false);
        return new ZmoneyHistoryViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ZmoneyHistory history = getItem(position);
        ZmoneyHistoryViewHolder holder = (ZmoneyHistoryViewHolder) viewHolder;
        Context context = holder.getContext();
        int year = history.getYear();
        int month = history.getMonth();
        holder.dateView.setText(context.getString(R.string.format_date2, year, month));
        holder.zmoneyView.setText(context.getString(R.string.format_point, history.getTotalReward()));
        if (year == 2017 && month == 1) {
            holder.alertView.setVisibility(View.VISIBLE);
        } else {
            holder.alertView.setVisibility(View.GONE);
        }
    }

    @Override
    protected View createLoadMoreProgressView(Context context) {
        FrameLayout container = new FrameLayout(context);
        container.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        View view = LayoutInflater.from(context).inflate(R.layout.item_zmoney_history_more, container, false);
        container.addView(view);
        container.setOnClickListener(v -> loadPrevYear());
        return container;
    }

    private void loadPrevYear() {
        prevYearLoader.loadPrevYear();
    }

    class ZmoneyHistoryViewHolder extends BaseRecyclerView.BaseViewHolder {

        @BindView(R.id.date) TextView dateView;
        @BindView(R.id.zmoney) TextView zmoneyView;
        @BindView(R.id.alert) TextView alertView;

        public ZmoneyHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}