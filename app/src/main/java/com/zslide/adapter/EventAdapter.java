package com.zslide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.model.EventBanner;
import com.zslide.widget.BaseRecyclerView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.RequestManager;

import org.threeten.bp.format.DateTimeFormatter;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;

/**
 * Created by jdekim43 on 2016. 1. 26..
 */
public class EventAdapter extends PaginationAdapter<EventBanner> {

    public EventAdapter(Loader<EventBanner> loader, RequestManager glide) {
        super(loader);
        setGlideRequestManager(glide);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Context context = viewHolder.itemView.getContext();
        EventBanner banner = getItem(position);

        EventViewHolder holder = (EventViewHolder) viewHolder;
        holder.titleView.setText(banner.getTitle());
        if (banner.getPubDate() != null && banner.getEndDate() != null) {
            holder.periodView.setText(String.format("%s ~ %s",
                    banner.getPubDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    banner.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        } else {
            holder.periodView.setText("상시 이벤트");
        }

        DrawableRequestBuilder builder = glide().load(banner.getImageUrl());
        if (!banner.isActive()) {
            builder.bitmapTransform(new GrayscaleTransformation(context));
        }
        builder.into(holder.imageView);
    }

    class EventViewHolder extends BaseRecyclerView.BaseViewHolder {

        @BindView(R.id.title) TextView titleView;
        @BindView(R.id.period) TextView periodView;
        @BindView(R.id.image) ImageView imageView;

        EventViewHolder(View itemView) {
            super(itemView);
        }
    }
}