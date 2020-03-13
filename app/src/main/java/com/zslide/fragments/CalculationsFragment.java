package com.zslide.fragments;

import android.os.Bundle;

import com.zslide.adapter.CalculationAdapter;
import com.zslide.adapter.PaginationAdapter;
import com.zslide.data.model.Payments;
import com.zslide.network.ZummaApi;

/**
 * Created by chulwoo on 2017. 9. 12..
 */

public class CalculationsFragment extends PaginationFragment<Payments> {

    public static CalculationsFragment newInstance() {

        Bundle args = new Bundle();

        CalculationsFragment fragment = new CalculationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected PaginationAdapter<Payments> createPaginationAdapter() {
        return new CalculationAdapter(page -> ZummaApi.user().calculations(page));
    }
}
