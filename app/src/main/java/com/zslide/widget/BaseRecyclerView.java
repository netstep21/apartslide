package com.zslide.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.zslide.adapter.HeaderFooterRecyclerViewAdapter;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by chulwoo on 15. 7. 30..
 */
@Deprecated
public class BaseRecyclerView extends RecyclerView {

    private View emptyView;
    final private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            invalidate();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            invalidate();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            invalidate();
        }
    };

    public BaseRecyclerView(Context context) {
        super(context);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        invalidate();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        invalidate();
    }

    public void invalidate() {
        BaseListAdapter adapter = (BaseListAdapter) getAdapter();
        if (emptyView != null && adapter != null) {
            int count = adapter.getContentItemCount();
            final boolean emptyViewVisible = count == 0;
            emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
//            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    public interface OnItemSelectedListener<Item> {
        void onItemSelected(Item item, int position);
    }

    public static abstract class BaseListAdapter<Data> extends HeaderFooterRecyclerViewAdapter {

        private ArrayList<Data> items;
        private RequestManager glideRequestManager;
        private BaseRecyclerView.OnItemSelectedListener<Data> onItemSelectedListener;

        public BaseListAdapter() {
            this(null);
        }

        public BaseListAdapter(RequestManager glideRequestManager) {
            super();
            this.items = new ArrayList<>();
            this.glideRequestManager = glideRequestManager;
        }

        public void setOnItemSelectedListener(OnItemSelectedListener<Data> listener) {
            this.onItemSelectedListener = listener;
        }

        @Override
        protected void onBindContentItemViewHolder(ViewHolder contentViewHolder, int position) {
            contentViewHolder.itemView.setOnClickListener(v -> {
                Data item = getItem(position);
                if (!onItemSelected(contentViewHolder, position, item)) {
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onItemSelected(item, position);
                    }
                }
            });
        }

        protected boolean onItemSelected(ViewHolder viewHolder, int position, Data item) {
            return false;
        }

        @Override
        protected int getContentItemCount() {
            return items.size();
        }

        public int getListSize() {
            return items.size();
        }

        public void clear() {
            items.clear();
        }

        public Data remove(int index) {
            return items.remove(index);
        }

        public boolean remove(Data item) {
            return items.remove(item);
        }

        public boolean addAll(Collection<? extends Data> items) {
            return this.items.addAll(items);
        }

        public boolean addAll(int position, Collection<? extends Data> items) {
            return this.items.addAll(position, items);
        }

        public boolean add(Data item) {
            return items.add(item);
        }

        public void add(int position, Data item) {
            items.add(position, item);
        }

        public void replace(int position, Data item) {
            items.remove(position);
            items.add(position, item);
        }

        public boolean replaceAll(Collection<? extends Data> items) {
            this.items.clear();
            return this.items.addAll(items);
        }

        public boolean isEmpty() {
            return items.isEmpty();
        }

        public Data getItem(int index) {
            return items.get(index);
        }

        public int indexOf(Data item) {
            return items.indexOf(item);
        }

        public List<Data> getAll() {
            return items;
        }

        public int getListIndex(Data data) {
            return items.indexOf(data);
        }

        public void setGlideRequestManager(RequestManager glideRequestManager) {
            this.glideRequestManager = glideRequestManager;
        }

        protected RequestManager glide() {
            if (glideRequestManager == null) {
                throw new IllegalStateException("you must call Adapter(RequestManager) constructor or setGlideRequestManager");
            }
            return glideRequestManager;
        }

        @Override
        public int getHeaderItemCount() {
            return 0;
        }

        @Override
        public int getFooterItemCount() {
            return 0;
        }

        @Override
        protected ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
            return null;
        }

        @Override
        protected ViewHolder onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType) {
            return null;
        }

        @Override
        protected void onBindHeaderItemViewHolder(ViewHolder headerViewHolder, int position) {

        }

        @Override
        protected void onBindFooterItemViewHolder(ViewHolder footerViewHolder, int position) {

        }
    }

    public static class BaseViewHolder extends ViewHolder {

        protected Context context;

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        public Context getContext() {
            return context;
        }
    }
}