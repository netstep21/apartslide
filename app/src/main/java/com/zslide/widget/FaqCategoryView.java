package com.zslide.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.models.Faq;

import java.util.List;

import butterknife.BindDimen;
import butterknife.ButterKnife;

/**
 * Created by chulwoo on 16. 8. 25..
 */
public class FaqCategoryView extends LinearLayout {

    @BindDimen(R.dimen.divider_size) int DIVIDER_SIZE;
    @BindDimen(R.dimen.spacing_large) int SPACING_LARGE;
    private View currentSelectedView;
    private List<Faq.Category> categories;
    private OnCategorySelectedListener listener;

    public FaqCategoryView(Context context) {
        this(context, null);
    }

    public FaqCategoryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaqCategoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FaqCategoryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        ButterKnife.bind(this);
        setOrientation(VERTICAL);
        setBackgroundResource(R.color.gray_c);
        setPadding(DIVIDER_SIZE, DIVIDER_SIZE, DIVIDER_SIZE, DIVIDER_SIZE);

        ViewGroup container = createLineContainer(context);

        for (int i = 0; i < 3; i++) {
            TextView categoryView = createCategoryView(context, false);
            if (i == 0) {
                categoryView.setSelected(true);
                categoryView.setText(R.string.label_all);
            } else {
                categoryView.setClickable(false);
            }
            currentSelectedView = categoryView;
            container.addView(categoryView);
        }
        addView(container);
    }

    public void setCategories(List<Faq.Category> categories, OnCategorySelectedListener listener) {
        this.categories = categories;
        this.listener = listener;

        if (getChildCount() > 0) {
            removeAllViews();
        }

        Context context = getContext();
        int lineSize = ((categories.size() + 1) / 3) + (((categories.size() + 1) % 3 == 0) ? 0 : 1);
        for (int i = 0; i < lineSize; i++) {
            LinearLayout container = createLineContainer(context);
            ((LinearLayout.LayoutParams) container.getLayoutParams()).topMargin = DIVIDER_SIZE;

            for (int j = 0; j < 3; j++) {
                int position = i * 3 + j - 1;

                TextView categoryView = createCategoryView(context, j == 0);
                if (i == 0 && j == 0) {
                    categoryView.setSelected(true);
                    categoryView.setText(R.string.label_all);
                    currentSelectedView = categoryView;
                } else {
                    if (position < categories.size()) {
                        categoryView.setTag(categories.get(position));
                        categoryView.setText(categories.get(position).getTitle());
                    } else {
                        categoryView.setClickable(false);
                    }
                }
                container.addView(categoryView);
            }

            addView(container);
        }
    }

    private LinearLayout createLineContainer(Context context) {
        LinearLayout container = new LinearLayout(context);
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        container.setLayoutParams(params);
        return container;
    }

    private TextView createCategoryView(Context context, boolean isFirst) {
        TextView categoryView = new TextView(getContext());
        categoryView.setTextColor(ContextCompat.getColorStateList(context, R.color.colors_faq_category));
        categoryView.setBackgroundResource(R.drawable.bg_faq_category);
        categoryView.setPadding(0, SPACING_LARGE, 0, SPACING_LARGE);
        /*categoryView.setTypeface(
                TypefaceUtils.load(context.getAssets(), context.getString(R.string.font_path)));*/
        categoryView.setGravity(Gravity.CENTER);
        categoryView.setOnClickListener(view -> {
            if (view.equals(currentSelectedView)) {
                return;
            }

            if (currentSelectedView != null) {
                currentSelectedView.setSelected(false);
            }
            view.setSelected(true);
            currentSelectedView = view;

            Faq.Category category = (Faq.Category) categoryView.getTag();
            if (listener != null) {
                listener.onCategorySelected(category);
            }
        });


        LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
        if (!isFirst) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginStart(DIVIDER_SIZE);
            } else {
                params.leftMargin = DIVIDER_SIZE;
            }
        }
        categoryView.setLayoutParams(params);

        return categoryView;

    }

    public interface OnCategorySelectedListener {
        void onCategorySelected(Faq.Category category);
    }
}
