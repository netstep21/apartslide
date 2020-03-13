package com.zslide.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.adapter.MarketItemAdapter;
import com.zslide.models.MarketItem;
import com.zslide.network.ZummaApi;
import com.zslide.widget.ListHeaderView;
import com.zslide.widget.PaginationView;

import butterknife.BindDimen;

/**
 * Created by chulwoo on 15. 8. 17..
 */
public class MarketItemsFragment extends PaginationFragment<MarketItem> {

    @BindDimen(R.dimen.spacing_large) int PADDING_VERTICAL;
    private ListHeaderView headerView;
    private final int zmoneyIndex = 3;
    public static MarketItemsFragment newInstance() {
        return new MarketItemsFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headerView = new ListHeaderView(getContext());
        headerView.setName(R.string.label_zummashopping);
        headerView.setBackgroundResource(R.color.amber);
        headerView.setUsageBehavior(() -> Navigator.startZummaShoppingHelpActivity(getContext()));
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        super.setupLayout(view, savedInstanceState);
        PaginationView<MarketItem> paginationView = getPaginationView();
        paginationView.setClipToPadding(false);
        paginationView.setBackgroundResource(R.color.gray_e);
        paginationView.addItemDecoration(
                new SpacesItemDecoration(0, 0, 0, PADDING_VERTICAL * 2));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MarketItemselected", "MarketItemSElected");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public MarketItemAdapter createPaginationAdapter() {
        MarketItemAdapter adapter = new MarketItemAdapter(ZummaApi.shopping()::items, glide());
        // TODO : 앱으로 보내는 기능 필요
        adapter.setOnItemSelectedListener(((marketItem, position) ->
                Navigator.startZmoneyApplicationStore(getActivity(), (long) zmoneyIndex)));
        adapter.addHeaderView(headerView);
        return adapter;
    }


    class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int left;
        private int top;
        private int right;
        private int bottom;

        SpacesItemDecoration(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (!getPaginationAdapter().isItemView(position)) {
                return;
            }
            outRect.top = top;
            outRect.left = left;
            outRect.right = right;
            outRect.bottom = bottom;
        }
    }
}
