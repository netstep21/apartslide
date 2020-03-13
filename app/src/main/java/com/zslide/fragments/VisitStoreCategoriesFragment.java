package com.zslide.fragments;

import android.os.Bundle;

import com.zslide.Navigator;
import com.zslide.models.ZummaStore;

import java.util.List;

/**
 * Created by chulwoo on 15. 7. 31..
 */
public class VisitStoreCategoriesFragment extends AbstractStoreCategoriesFragment {

    private final int zmoneyIndex = 2;

    public static VisitStoreCategoriesFragment newInstance() {

        Bundle args = new Bundle();

        VisitStoreCategoriesFragment fragment = new VisitStoreCategoriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private List<ZummaStore.Category> categories = ZummaStore.Category.visitCategories();

    @Override
    public List<ZummaStore.Category> getCategories() {
        return categories;
    }

    @Override
    protected void onCategorySelected(ZummaStore.Category category) {
        // TODO: 줌머니앱으로 보내주는 기능 필요
        Navigator.startZmoneyApplicationStore(getActivity(), (long) zmoneyIndex);
        //Navigator.startVisitStoresActivity(getActivity(), dong, category);
    }

    @Override
    protected ZummaStore.Type getStoreType() {
        return ZummaStore.Type.VISIT;
    }

    @Override
    public void search() {
        Navigator.startZmoneyApplicationStore(getActivity(), (long) zmoneyIndex);
    }

    @Override
    public void selectLocation() {
        Navigator.startZmoneyApplicationStore(getActivity(), (long) zmoneyIndex);
    }
}