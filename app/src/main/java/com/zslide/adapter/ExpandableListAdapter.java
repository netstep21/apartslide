package com.zslide.adapter;

import android.support.v7.widget.RecyclerView;

import com.zslide.widget.BaseRecyclerView;
import com.bumptech.glide.RequestManager;

import java.util.HashSet;

/**
 * Created by chulwoo on 2016. 7. 25..
 */
public abstract class ExpandableListAdapter<Data> extends BaseRecyclerView.BaseListAdapter<Data> {

    protected boolean autoExpand;
    protected HashSet<Integer> expandedPositions;
    //protected int expandedPositions = -1;

    public ExpandableListAdapter() {
        this(true);
    }

    public ExpandableListAdapter(boolean autoExpand) {
        this(null, autoExpand);
    }

    public ExpandableListAdapter(RequestManager glide, boolean autoExpand) {
        super(glide);
        this.autoExpand = autoExpand;
        this.expandedPositions = new HashSet<>();
    }

    public void setAutoExpand(boolean autoExpand) {
        this.autoExpand = autoExpand;
    }

    public void expand(RecyclerView.ViewHolder viewHolder, int position) {
        if (isExpanded(position)) {
            close(viewHolder, position);
        } else {
            open(viewHolder, position);
        }
        /*final int finalExpandedPosition = expandedPositions;
        if (finalExpandedPosition >= 0) {
            close(viewHolder, finalExpandedPosition);
        }

        if (finalExpandedPosition != position) {
            open(viewHolder, position);
        }*/
    }

    public void open(RecyclerView.ViewHolder viewHolder, int position) {
        expandedPositions.add(position);
        //notifyContentItemChanged(expandedPositions);
        notifyContentItemChanged(position);
    }

    public void close(RecyclerView.ViewHolder viewHolder, int position) {
        expandedPositions.remove(position);
        notifyContentItemChanged(position);
    }

    public boolean isExpanded(int position) {
        return expandedPositions.contains(position);
    }

    @Override
    protected boolean onItemSelected(RecyclerView.ViewHolder viewHolder, int position, Data item) {
        if (autoExpand) {
            expand(viewHolder, position);
        }
        return super.onItemSelected(viewHolder, position, item);
    }
}
