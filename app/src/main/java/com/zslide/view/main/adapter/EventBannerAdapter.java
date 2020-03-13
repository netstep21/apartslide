package com.zslide.view.main.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zslide.R;
import com.zslide.view.main.EventBannerItem;
import com.zslide.view.main.adapter.holder.EventBannerViewHolder;
import com.bumptech.glide.RequestManager;

import java.util.List;

public class EventBannerAdapter extends RecyclerView.Adapter<EventBannerViewHolder> {

    private List<EventBannerItem> eventBannerItems;
    private RequestManager requestManager;

    public EventBannerAdapter(@NonNull RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    public void setEventBannerItems(List<EventBannerItem> eventBannerItems) {
        this.eventBannerItems = eventBannerItems;
        notifyDataSetChanged();
    }

    @Override
    public EventBannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_banner, parent, false);
        return new EventBannerViewHolder(view, requestManager);
    }

    @Override
    public void onBindViewHolder(EventBannerViewHolder holder, int position) {
        EventBannerItem item = eventBannerItems.get(position);
        holder.bind(item.getEventBanner());
        holder.itemView.setOnClickListener(view ->
                onItemClick(holder.getAdapterPosition()));
    }

    private void onItemClick(int position) {
        if (position < 0 || position >= eventBannerItems.size()) {
            return;
        }

        EventBannerItem item = eventBannerItems.get(position);
        if (item.getClickAction() != null) {
            try {
                item.getClickAction().run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return eventBannerItems == null ? 0 : eventBannerItems.size();
    }
}