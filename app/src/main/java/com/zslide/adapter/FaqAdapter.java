package com.zslide.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.models.Faq;
import com.zslide.widget.BaseRecyclerView;
import com.zslide.widget.HtmlContentView;

import butterknife.BindView;

/**
 * Created by jdekim43 on 2016. 1. 25..
 */
public class FaqAdapter extends ExpandablePaginationAdapter<Faq> {

    public FaqAdapter(Loader<Faq> loader) {
        super(loader);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        return new FaqViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faq, parent, false));
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        FaqViewHolder holder = (FaqViewHolder) viewHolder;
        Faq faq = getItem(position);
        holder.title.setText(faq.getTitle());
        holder.content.setHtml(faq.getContent());
        if (isExpanded(position)) {
            holder.arrow.setImageResource(R.drawable.ic_arrow_up_gray);
            holder.contentContainer.setVisibility(View.VISIBLE);
        } else {
            holder.arrow.setImageResource(R.drawable.ic_arrow_down_gray);
            holder.contentContainer.setVisibility(View.GONE);
        }
    }

    static class FaqViewHolder extends BaseRecyclerView.BaseViewHolder {

        @BindView(R.id.title) TextView title;
        @BindView(R.id.arrow) ImageView arrow;
        @BindView(R.id.contentContainer) ViewGroup contentContainer;
        @BindView(R.id.content) HtmlContentView content;

        FaqViewHolder(View itemView) {
            super(itemView);
        }
    }
}
