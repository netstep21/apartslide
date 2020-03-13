package com.zslide.adapter;

import android.support.v7.widget.RecyclerView;

import com.zslide.models.ApiData;

/**
 * Created by chulwoo on 2016. 7. 25..
 */
public abstract class ExpandablePaginationAdapter<Item extends ApiData> extends PaginationAdapter<Item> {

    private boolean autoExpand;
    private int expandedPosition = -1;

    public ExpandablePaginationAdapter(Loader<Item> loader) {
        this(loader, true);
    }

    public ExpandablePaginationAdapter(Loader<Item> loader, boolean autoExpand) {
        super(loader);
        this.autoExpand = autoExpand;
    }

    public void setAutoExpand(boolean autoExpand) {
        this.autoExpand = autoExpand;
    }

    @Override
    public void refresh(boolean force) {
        expandedPosition = -1;
        super.refresh(force);
    }

    public void expand(int position) {
        final int finalExpandedPosition = expandedPosition;
        if (finalExpandedPosition >= 0) {
            close(finalExpandedPosition);
        }

        if (finalExpandedPosition != position) {
            open(position);
        }
    }

    public void open(int position) {
        expandedPosition = position;
        notifyContentItemChanged(expandedPosition);
        notifyContentItemChanged(position);
    }

    public void close(int position) {
        expandedPosition = -1;
        notifyContentItemChanged(position);
    }

    public boolean isExpanded(int position) {
        return expandedPosition == position;
    }

    @Override
    protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, int position) {
        super.onBindContentItemViewHolder(contentViewHolder, position);
        if (autoExpand) {
            contentViewHolder.itemView.setOnClickListener(view -> expand(position));
        }
    }
}
