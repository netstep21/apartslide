package com.zslide.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.models.Dong;
import com.zslide.models.ZummaStore;
import com.zslide.utils.EasySharedPreferences;
import com.zslide.widget.GridSpacingItemDecoration;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chulwoo on 15. 7. 31..
 */
public abstract class AbstractStoreCategoriesFragment extends com.zslide.view.base.BaseFragment {

    private static final int REQUEST_CODE_SELECT_LOCATION = 1;
    private static final String CATEGORY_LEARNED_USER = "cartegory_learned_user";

    public abstract List<ZummaStore.Category> getCategories();

    protected abstract ZummaStore.Type getStoreType();

    protected abstract void onCategorySelected(ZummaStore.Category category);

    @BindDimen(R.dimen.store_category_spacing) int spacing;

    @BindView(R.id.location) TextView locationView;
    @BindView(R.id.categories)
    RecyclerView categoriesView;

    protected Dong dong;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_store_categories;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationView.setText(R.string.find_my_location);
        CategoryAdapter adapter = new CategoryAdapter(getCategories(),
                category -> {
                    onCategorySelected(category);
//                    if (dong == null) {
//                        Toast.makeText(getActivity(), R.string.empty_location, Toast.LENGTH_SHORT).show();
//                    } else {
//                        onCategorySelected(category);
//                    }
                });
        categoriesView.setAdapter(adapter);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 3);
        categoriesView.setLayoutManager(glm);
        categoriesView.addItemDecoration(new GridSpacingItemDecoration(spacing));
    }

    @Override
    public void onResumeWithVisibleHint() {
        super.onResumeWithVisibleHint();
        if (locationView != null) {
            locationView.setText(R.string.find_my_location);
        }

        boolean learnedUser = EasySharedPreferences.with(getActivity()).getBoolean(CATEGORY_LEARNED_USER, false);
        if (!learnedUser) {
            View target = categoriesView.getChildAt(0);
            if (target != null) {
                showCategoryTip(target);
            }
        }
    }

    public void showCategoryTip(View target) {
        EasySharedPreferences.with(getActivity()).putBoolean(CATEGORY_LEARNED_USER, true);
        TapTargetView.showFor(getActivity(),
                TapTarget.forView(target, "", "원하는 메뉴를 선택하세요.")
                        .outerCircleColor(R.color.accentColor)
                        .outerCircleAlpha(0.96f)
                        .targetCircleColor(R.color.white)
                        .descriptionTextSize(15)
                        .descriptionTextColor(R.color.white)
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .tintTarget(false)                   // Whether to tint the target view's color
                        //.transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        //.icon(ContextCompat.getDrawable(getActivity(), R.drawable.img_c_d_chken))
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        target.performClick();
                    }
                });
    }

    public void updateLocation() {
        if (dong != null) {
            locationView.setText(dong.getName());
        }
    }

    @OnClick(R.id.location)
    public void selectLocation() {
        // TODO : 자연스레 줌머니 앱으로
    }

    @OnClick(R.id.search)
    public void search() {
        // TODO : 자연스레 줌머니 앱으로
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SELECT_LOCATION:
                if (resultCode == Activity.RESULT_OK && data == null && getUserVisibleHint()) {
                    this.dong = null;
                }
                break;
        }
    }

    static class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        public interface OnItemSelectedListener {
            void onItemSelected(ZummaStore.Category category);
        }

        List<ZummaStore.Category> categories;
        OnItemSelectedListener listener;

        CategoryAdapter(List<ZummaStore.Category> categories, OnItemSelectedListener listener) {
            this.categories = categories;
            this.listener = listener;
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_category, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, int position) {
            ZummaStore.Category category = categories.get(position);
            holder.image.setImageResource(category.getImageResource());
            holder.name.setText(category.getName());
            holder.itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemSelected(category);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        static class CategoryViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.image) ImageView image;
            @BindView(R.id.name) TextView name;

            public CategoryViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}