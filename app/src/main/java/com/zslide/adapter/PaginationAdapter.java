package com.zslide.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zslide.R;
import com.zslide.models.ApiData;
import com.zslide.models.PaginationData;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.widget.EmptyView;
import com.zslide.widget.BaseRecyclerView;
import com.zslide.widget.PaginationView;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 16. 3. 11..
 * Updated by chulwoo on 16. 4. 19..
 * {@link PaginationView} 구현하면서 이름 변경, {@link PaginationAdapter.Loader}를 나중에 지정할 수 있도록 변경함
 * Updated by chulwoo on 16. 10. 11..
 * {@link HeaderFooterRecyclerViewAdapter}로 업데이트하면서 관련 코드 추가
 */

public abstract class PaginationAdapter<Data> extends BaseRecyclerView.BaseListAdapter<Data> {

    private static final int POS_REFRESH_PROGRESS = 0;
    private static final int POS_EMPTY = 0;
    private static final int POS_ERROR = 1;
    private static final int POS_LOAD_MORE_PROGRESS = 2;
    private static final int VIEW_TYPE_GONE = 100;
    private static final int VIEW_TYPE_EMPTY = 101;
    private static final int VIEW_TYPE_ERROR = 102;
    private static final int VIEW_TYPE_REFRESH_PROGRESS = 200;
    private static final int VIEW_TYPE_LOAD_MORE_PROGRESS = 201;
    protected Loader<Data> loader;
    protected int loadingOffset;
    private boolean visibleEmptyView = false;
    private boolean visibleErrorView = false;
    private boolean visibleRefreshProgress = false;
    private boolean visibleLoadMoreProgress = false;

    private int currentPage = 1;
    private int totalItemCount = 0;
    private boolean hasNext = false;

    private ArrayList<View> headers;
    private ArrayList<View> footers;

    private Subscription refreshSubscription;
    private ArrayList<Tracker<Data>> trackers;
    protected boolean autoLoadMore = true;

    public PaginationAdapter() {
        this(null);
    }

    public PaginationAdapter(Loader<Data> loader) {
        this(loader, 1);
    }

    public PaginationAdapter(Loader<Data> loader, int loadingOffset) {
        this.headers = new ArrayList<>();
        this.footers = new ArrayList<>();
        this.trackers = new ArrayList<>();
        this.loader = loader;
        this.loadingOffset = loadingOffset;
        // header, footer 카운트가 올라가지 않음
        getItemCount();
    }

    public void addHeaderView(View view) {
        headers.add(view);
        getItemCount();
        notifyHeaderItemInserted(headers.size());
    }

    public void addFooterView(View view) {
        footers.add(view);
        getItemCount();
        notifyFooterItemInserted(footers.size());
    }

    public void addTracker(Tracker tracker) {
        trackers.add(tracker);
    }

    public void refresh(boolean force) {
        if (force && refreshSubscription != null) {
            cancelRefresh();
        }

        if (refreshSubscription != null && refreshSubscription.isUnsubscribed()) {
            return;
        }

        currentPage = 1;
        prepareRefresh();
        for (Tracker tracker : trackers) {
            tracker.onRefresh();
        }

        refreshSubscription = load(currentPage).subscribe(this::onSuccessRefresh, this::onFailureRefresh);
    }

    public void cancelRefresh() {
        if (refreshSubscription != null) {
            refreshSubscription.unsubscribe();
            refreshSubscription = null;
        }
        hideRefreshProgress();
        for (Tracker tracker : trackers) {
            tracker.onCancelRefresh();
        }
    }

    public void increaseTotalItemCount() {
        totalItemCount++;
    }

    public void decreaseTotalItemCount() {
        totalItemCount--;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public void setLoader(Loader<Data> loader) {
        this.loader = loader;
    }

    public void setLoadingOffset(int loadingOffset) {
        this.loadingOffset = loadingOffset;
    }

    private Observable<List<Data>> load(int page) {
        return loader.load(page)
                .subscribeOn(Schedulers.newThread())
                .map(paginationData -> {
                    hasNext = !TextUtils.isEmpty(paginationData.getNext());
                    totalItemCount = paginationData.getCount();
                    return paginationData;
                })
                .map(PaginationData::getResults)
                .map(data -> data == null ? new ArrayList<Data>() : data)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void prepareRefresh() {
        for (Tracker tracker : trackers) {
            tracker.onPrepareRefresh();
        }
        hideEmptyView();
        hideErrorView();
        showRefreshProgress();
    }

    private void onSuccessRefresh(List<Data> data) {
        clear();
        addAll(data);
        notifyDataSetChanged();
        for (Tracker<Data> tracker : trackers) {
            tracker.onSuccessRefresh(data);
        }
        onFinishRefresh(true);
    }

    private void onFailureRefresh(Throwable e) {
        refreshSubscription = null;
        ZummaApiErrorHandler.handleError(e);
        hideRefreshProgress();
        clear();
        notifyDataSetChanged();
        for (Tracker tracker : trackers) {
            tracker.onFailureRefresh();
        }
        onFinishRefresh(false);
    }

    private void onFinishRefresh(boolean success) {
        refreshSubscription = null;
        hideRefreshProgress();
        if (success) {
            if (getContentItemCount() == 0) {
                showEmptyView();
            }
        } else {
            showErrorView();
        }
    }

    private void prepareLoadMore() {
        for (Tracker tracker : trackers) {
            tracker.onPrepareLoadMore();
        }
        showLoadMoreProgress();
    }

    private void onSuccessLoadMore(List<Data> data) {
        addAll(data);
        int position = getHeaderItemCount() + getContentItemCount();
        notifyItemRangeInserted(position, data.size());
        for (Tracker<Data> tracker : trackers) {
            tracker.onSuccessLoadMore(data);
        }
        onFinishLoadMore();
    }

    private void onFailureLoadMore(Throwable e) {
        ZummaApiErrorHandler.handleError(e);
        for (Tracker tracker : trackers) {
            tracker.onFailureLoadMore();
        }
        onFinishLoadMore();
        // TODO: error view가 보여야 함 || 더보기
    }

    private void onFinishLoadMore() {
        hideLoadMoreProgress();
    }

    public void init() {
        hideEmptyView();
        hideErrorView();
        hideRefreshProgress();
        hideLoadMoreProgress();
        clear();
    }

    public void showEmptyView() {
        visibleEmptyView = true;
        new Handler().post(() -> notifyFooterItemChanged(POS_EMPTY));
    }

    public void hideEmptyView() {
        visibleEmptyView = false;
        new Handler().post(() -> notifyFooterItemChanged(POS_EMPTY));
    }

    public void showErrorView() {
        visibleErrorView = true;
        new Handler().post(() -> notifyFooterItemChanged(POS_ERROR));
    }

    public void hideErrorView() {
        visibleErrorView = false;
        new Handler().post(() -> notifyFooterItemChanged(POS_ERROR));
    }

    protected void showRefreshProgress() {
        visibleRefreshProgress = true;
        new Handler().post(() -> notifyFooterItemChanged(POS_REFRESH_PROGRESS));
    }

    protected void hideRefreshProgress() {
        visibleRefreshProgress = false;
        new Handler().post(() -> notifyFooterItemChanged(POS_REFRESH_PROGRESS));
    }

    public void showLoadMoreProgress() {
        visibleLoadMoreProgress = true;
        new Handler().post(() -> notifyFooterItemChanged(POS_LOAD_MORE_PROGRESS));
    }

    public void hideLoadMoreProgress() {
        visibleLoadMoreProgress = false;
        new Handler().post(() -> notifyFooterItemChanged(POS_LOAD_MORE_PROGRESS));
    }

    @Override
    public synchronized int getHeaderItemCount() {
        return headers.size() + 1;
    }

    @Override
    protected int getHeaderItemViewType(int position) {
        if (position < headers.size()) {
            return position;
        } else {
            return visibleRefreshProgress ? VIEW_TYPE_REFRESH_PROGRESS : VIEW_TYPE_GONE;
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
        switch (headerViewType) {
            case VIEW_TYPE_REFRESH_PROGRESS:
                return createRefreshProgressViewHolder(parent);
            case VIEW_TYPE_GONE:
                return createGoneViewHolder(parent);
            default:
                return new BaseRecyclerView.BaseViewHolder(headers.get(headerViewType));
        }
    }

    @Override
    protected void onBindHeaderItemViewHolder(RecyclerView.ViewHolder headerViewHolder, int position) {
        super.onBindHeaderItemViewHolder(headerViewHolder, position);
    }

    @Override
    protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, int position) {
        if (autoLoadMore) {
            if (hasNext && (position >= getContentItemCount() - loadingOffset)) {
                currentPage++;
                prepareLoadMore();
                for (Tracker tracker : trackers) {
                    tracker.onLoadMore();
                }
                load(currentPage).subscribe(this::onSuccessLoadMore, this::onFailureLoadMore);
            }
        }

        super.onBindContentItemViewHolder(contentViewHolder, position);
        onBindItemViewHolder(contentViewHolder, position);
    }

    protected abstract void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position);

    @Override
    public int getFooterItemCount() {
        return footers.size() + 3;
    }

    @Override
    protected int getFooterItemViewType(int position) {
        int footerPosition = position - footers.size();
        switch (position) {
            case POS_EMPTY:
                return visibleEmptyView ? VIEW_TYPE_EMPTY : VIEW_TYPE_GONE;
            case POS_ERROR:
                return visibleErrorView ? VIEW_TYPE_ERROR : VIEW_TYPE_GONE;
            case POS_LOAD_MORE_PROGRESS:
                return visibleLoadMoreProgress ? VIEW_TYPE_LOAD_MORE_PROGRESS : VIEW_TYPE_GONE;
            default:
                return position - 3;
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType) {
        switch (footerViewType) {
            case VIEW_TYPE_EMPTY:
                return createEmptyViewHolder(parent);
            case VIEW_TYPE_ERROR:
                return createErrorViewHolder(parent);
            case VIEW_TYPE_LOAD_MORE_PROGRESS:
                return createLoadMoreProgressViewHolder(parent);
            case VIEW_TYPE_GONE:
                return createGoneViewHolder(parent);
            default:
                return new BaseRecyclerView.BaseViewHolder(footers.get(footerViewType));
        }
    }

    @Override
    protected void onBindFooterItemViewHolder(RecyclerView.ViewHolder footerViewHolder, int position) {
        super.onBindFooterItemViewHolder(footerViewHolder, position);
    }

    private BaseRecyclerView.BaseViewHolder createEmptyViewHolder(ViewGroup parent) {
        View view = createEmptyView(parent.getContext());
        return new BaseRecyclerView.BaseViewHolder(view);
    }

    private BaseRecyclerView.BaseViewHolder createErrorViewHolder(ViewGroup parent) {
        View view = createErrorView(parent.getContext());
        return new BaseRecyclerView.BaseViewHolder(view);
    }

    private BaseRecyclerView.BaseViewHolder createRefreshProgressViewHolder(ViewGroup parent) {
        View view = createRefreshProgressView(parent.getContext());
        return new BaseRecyclerView.BaseViewHolder(view);
    }

    private BaseRecyclerView.BaseViewHolder createLoadMoreProgressViewHolder(ViewGroup parent) {
        View view = createLoadMoreProgressView(parent.getContext());
        return new BaseRecyclerView.BaseViewHolder(view);
    }

    private BaseRecyclerView.BaseViewHolder createGoneViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hide, parent, false);
        return new BaseRecyclerView.BaseViewHolder(view);
    }

    protected View createEmptyView(Context context) {
        FrameLayout container = new FrameLayout(context);
        container.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        container.addView(EmptyView.getDefault(context));
        return container;
    }

    protected View createErrorView(Context context) {
        EmptyView errorView = new EmptyView(context,
                context.getString(R.string.label_empty_error_title),
                context.getString(R.string.label_empty_error_message),
                context.getString(R.string.label_empty_error_button));
        errorView.setOnButtonClickListener(() -> refresh(true));
        FrameLayout container = new FrameLayout(context);
        container.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        container.addView(errorView);
        return container;
    }

    protected View createRefreshProgressView(Context context) {
        FrameLayout container = new FrameLayout(context);
        container.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        View view = LayoutInflater.from(context).inflate(R.layout.view_refresh_progress, container
                , false);
        container.addView(view);
        return container;
    }

    protected View createLoadMoreProgressView(Context context) {
        Resources res = context.getResources();
        int padding = res.getDimensionPixelSize(R.dimen.spacing_normal);
        int size = res.getDimensionPixelSize(R.dimen.progress_size_micro);
        int strokeWidth = res.getDimensionPixelSize(R.dimen.progress_width_micro);
        int color = ContextCompat.getColor(context, R.color.subAccentColor);
        FrameLayout container = new FrameLayout(context);
        container.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        container.setPadding(padding, padding, padding, padding);
        CircularProgressBar progressView = new CircularProgressBar(context);
        progressView.setLayoutParams(new FrameLayout.LayoutParams(size, size, Gravity.CENTER));
        progressView.setIndeterminateDrawable(new CircularProgressDrawable.Builder(context)
                .color(color)
                .strokeWidth(strokeWidth)
                .style(CircularProgressDrawable.STYLE_ROUNDED)
                .build());
        progressView.setIndeterminate(true);

        container.addView(progressView);
        return container;
    }

    public boolean isItemView(int position) {
        return position >= getHeaderItemCount() &&
                position < getHeaderItemCount() + getContentItemCount();
    }

    public interface Loader<Data> {
        Observable<PaginationData<Data>> load(int page);
    }

    public interface Tracker<Data> {

        void onPrepareRefresh();

        void onRefresh();

        void onCancelRefresh();

        void onSuccessRefresh(List<Data> data);

        void onFailureRefresh();

        void onPrepareLoadMore();

        void onLoadMore();

        void onSuccessLoadMore(List<Data> data);

        void onFailureLoadMore();
    }

    public static class SimpleTracker<Data extends ApiData> implements Tracker<Data> {

        @Override
        public void onPrepareRefresh() {

        }

        @Override
        public void onRefresh() {

        }

        @Override
        public void onCancelRefresh() {

        }

        @Override
        public void onSuccessRefresh(List<Data> data) {

        }

        @Override
        public void onFailureRefresh() {

        }

        @Override
        public void onPrepareLoadMore() {

        }

        @Override
        public void onLoadMore() {

        }

        @Override
        public void onSuccessLoadMore(List<Data> data) {

        }

        @Override
        public void onFailureLoadMore() {

        }
    }
}