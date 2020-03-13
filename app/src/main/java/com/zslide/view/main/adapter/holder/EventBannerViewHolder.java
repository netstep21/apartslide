package com.zslide.view.main.adapter.holder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.zslide.R;
import com.zslide.data.model.EventBanner;
import com.zslide.view.base.BaseViewHolder;
import com.bumptech.glide.RequestManager;

import butterknife.BindView;

/**
 * Created by chulwoo on 2017. 12. 29..
 */

public class EventBannerViewHolder extends BaseViewHolder {

    private final RequestManager requestManager;

    @BindView(R.id.image) public ImageView image;

    public EventBannerViewHolder(View itemView, @NonNull RequestManager requestManager) {
        super(itemView);
        this.requestManager = requestManager;
    }

    public void bind(EventBanner event) {
        requestManager.load(event.getImageUrl()).into(image);
    }
}