package com.zslide.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.models.Notice;
import com.zslide.widget.BaseRecyclerView;

import butterknife.BindView;

/**
 * Created by chulwoo on 2015. 10. 29..
 */
public class NoticeAdapter extends PaginationAdapter<Notice> {

    public NoticeAdapter(Loader<Notice> loader) {
        super(loader);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        return new NoticeViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notice, parent, false));
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        NoticeViewHolder holder = ((NoticeViewHolder) viewHolder);
        Notice notice = getItem(position);
        holder.titleView.setText(notice.getTitle());
        if (notice.isNew()) {
            holder.titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_new, 0);
        } else {
            holder.titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        holder.createdAtView.setText(DateFormat.format("yyyy-MM-dd", notice.getPubDate()));
    }

    class NoticeViewHolder extends BaseRecyclerView.BaseViewHolder {

        @BindView(R.id.title) TextView titleView;
        @BindView(R.id.createdAt) TextView createdAtView;
        @BindView(R.id.arrow) ImageView arrowView;

        NoticeViewHolder(View itemView) {
            super(itemView);
        }
    }
}
