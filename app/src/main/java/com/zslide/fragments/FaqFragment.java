package com.zslide.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.zslide.R;
import com.zslide.models.Faq;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.adapter.FaqAdapter;
import com.zslide.adapter.PaginationAdapter;
import com.zslide.widget.FaqCategoryView;

import java.util.List;

import butterknife.BindDimen;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 1. 25..
 * <p>
 * Updated by chulwoo on 2016. 8. 25..
 * 새 디자인으로 리뉴얼, 카테고리 적용
 */
public class FaqFragment extends PaginationFragment<Faq> implements FaqCategoryView.OnCategorySelectedListener {

    @BindDimen(R.dimen.spacing_large) int SPACING_LARGE;
    @BindDimen(R.dimen.divider_size) int DIVIDER_SIZE;
    private FaqCategoryView categoryView;

    public static FaqFragment newInstance() {
        Bundle args = new Bundle();

        FaqFragment instance = new FaqFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        super.setupLayout(view, savedInstanceState);
        view.setBackgroundResource(R.color.white);
        categoryView = new FaqCategoryView(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(SPACING_LARGE, SPACING_LARGE, SPACING_LARGE, SPACING_LARGE);
        categoryView.setLayoutParams(params);
        View divider = new View(getActivity());
        FrameLayout.LayoutParams dividerParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, DIVIDER_SIZE);
        divider.setBackgroundResource(R.color.gray_c);
        divider.setLayoutParams(dividerParams);
        getPaginationAdapter().addHeaderView(categoryView);
        getPaginationAdapter().addHeaderView(divider);

        ZummaApi.notice().faqCategories()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setupFaqCategories, ZummaApiErrorHandler::handleError);
    }

    public void setupFaqCategories(List<Faq.Category> categories) {
        categoryView.setCategories(categories, this);
    }

    @Override
    public PaginationAdapter<Faq> createPaginationAdapter() {
        return new FaqAdapter(page -> ZummaApi.notice().faqs(page));
    }

    @Override
    public void onCategorySelected(Faq.Category category) {
        getPaginationAdapter().setLoader(page -> ZummaApi.notice().faqs(category, page));
        refresh();
    }
}
