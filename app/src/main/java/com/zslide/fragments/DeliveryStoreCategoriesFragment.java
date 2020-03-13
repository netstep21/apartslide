package com.zslide.fragments;

import android.os.Bundle;

import com.zslide.Navigator;
import com.zslide.models.ZummaStore;

import java.util.List;

/**
 * Created by chulwoo on 15. 7. 31..
 */
public class DeliveryStoreCategoriesFragment extends AbstractStoreCategoriesFragment {

    private final int zmoneyIndex = 1;

    public static DeliveryStoreCategoriesFragment newInstance() {

        Bundle args = new Bundle();

        DeliveryStoreCategoriesFragment fragment = new DeliveryStoreCategoriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private List<ZummaStore.Category> categories = ZummaStore.Category.deliveryCategories();

    @Override
    public List<ZummaStore.Category> getCategories() {
        return categories;
    }

    @Override
    protected void onCategorySelected(ZummaStore.Category category) {
        Navigator.startZmoneyApplicationStore(getActivity(), (long) zmoneyIndex);
    }

    @Override
    protected ZummaStore.Type getStoreType() {
        return ZummaStore.Type.DELIVERY;
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