package com.zslide.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.zslide.R;
import com.zslide.models.MarketItem;
import com.zslide.widget.BaseRecyclerView;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation.CornerType;

public class MarketItemAdapter extends PaginationAdapter<MarketItem> {

    public MarketItemAdapter(Loader<MarketItem> loader, RequestManager glideRequestManager) {
        super(loader);
        setGlideRequestManager(glideRequestManager);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        return new MarketItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_market, parent, false));
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MarketItemViewHolder holder = (MarketItemViewHolder) viewHolder;
        MarketItem marketItem = getItem(position);
        String logoImageUrl = marketItem.getLogoImageUrl();
        Context context = holder.itemView.getContext();

        glide().load(logoImageUrl)
                .bitmapTransform(
                        new RoundedCornersTransformation(context, 10, 5, CornerType.TOP),
                        new BlurTransformation(context, 25, 2))
                .into(holder.thumbnailBackgroundView);
        glide().load(logoImageUrl)
                .into(holder.thumbnailView);

        holder.originPriceView.setPaintFlags(
                holder.originPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tag1View.setVisibility((marketItem.isNew()) ? View.VISIBLE : View.GONE);
        holder.tag2View.setVisibility((marketItem.isDeliveryFree()) ? View.VISIBLE : View.GONE);
        holder.tag3View.setVisibility((marketItem.isRecommend()) ? View.VISIBLE : View.GONE);
        holder.tag4View.setVisibility((marketItem.isHit()) ? View.VISIBLE : View.GONE);

        String customTag = marketItem.getCustomLabel();
        holder.tagCustomView.setText(customTag);
        holder.tagCustomView.setVisibility(TextUtils.isEmpty(customTag) ? View.GONE : View.VISIBLE);

        holder.titleView.setText(marketItem.getTitle());
        holder.priceView.setText(context.getString(R.string.format_price, marketItem.getPrice()));
        holder.originPriceView.setText(context.getString(R.string.format_price, marketItem.getOriginPrice()));
        holder.deductionRateView.setText(
                context.getString(R.string.format_number, marketItem.getDeductionRate()));
        if (marketItem.getDeductionRate() == 0) {
            holder.deductionView.setVisibility(View.GONE);
        } else {
            holder.deductionView.setVisibility(View.VISIBLE);
            holder.labelDeductionRateView.setText(
                    (marketItem.getDeductionRate() >= 100) ? R.string.postfix_point : R.string.postfix_percent);
        }

    }

    class MarketItemViewHolder extends BaseRecyclerView.BaseViewHolder {

        @BindView(R.id.thumbnail) ImageView thumbnailView;
        @BindView(R.id.thumbnailBackground) ImageView thumbnailBackgroundView;
        @BindView(R.id.tag1) TextView tag1View;
        @BindView(R.id.tag2) TextView tag2View;
        @BindView(R.id.tag3) TextView tag3View;
        @BindView(R.id.tag4) TextView tag4View;
        @BindView(R.id.tagCustom) TextView tagCustomView;
        @BindView(R.id.title) TextView titleView;
        @BindView(R.id.price) TextView priceView;
        @BindView(R.id.originPrice) TextView originPriceView;
        @BindView(R.id.deductionRate) TextView deductionRateView;
        @BindView(R.id.deduction) View deductionView;
        @BindView(R.id.labelDeductionRate) TextView labelDeductionRateView;

        MarketItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
