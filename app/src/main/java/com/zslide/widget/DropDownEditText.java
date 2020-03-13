package com.zslide.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.network.ZummaApiErrorHandler;

import java.util.List;

import butterknife.BindDimen;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by chulwoo on 2016. 4. 6..
 * <p>
 */
public class DropDownEditText<T> extends EditText {

    protected PopupWindow shownDropdownWindow;
    protected DropDownAdapter dropdownAdapter;
    protected ListView dropdownView;
    protected View emptyView;
    @BindDimen(R.dimen.edittext_height) int DEFAULT_MIN_HEIGHT;
    @BindDimen(R.dimen.spacing_large) int DEFAULT_PADDING;
    @BindDimen(R.dimen.divider_size) int DEFAULT_DIVIDER_HEIGHT;
    @DrawableRes private int DEFAULT_BACKGROUND_TOP = R.drawable.bg_list_top;
    @DrawableRes private int DEFAULT_BACKGROUND_BOTTOM = R.drawable.bg_list_bottom;
    @DrawableRes private int DEFAULT_BACKGROUND = R.drawable.bg_list;
    private OnDropDownItemClickListener listener;

    public DropDownEditText(Context context) {
        this(context, null);
    }

    public DropDownEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public DropDownEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }

        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DropDownEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (isInEditMode()) {
            return;
        }
        init(context);
    }

    private void init(Context context) {
        ButterKnife.bind(this);
        dropdownAdapter = new DropDownAdapter(context);
        dropdownView = createDropDownView(context, dropdownAdapter);
        emptyView = createDropDownEmptyView(context);
        if (dropdownView != null) {
            dropdownView.setId(R.id.list);
            if (emptyView != null) {
                emptyView.setId(R.id.empty);
                dropdownView.addFooterView(emptyView);
            }
        }
        setBackgroundResource(DEFAULT_BACKGROUND);
        setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
        setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (isShownDropDown()) {
                    dismissDropdown();

                    return true;
                }
            }

            return false;
        });
    }

    public void setOnDropDownItemClickListener(OnDropDownItemClickListener listener) {
        this.listener = listener;
    }

    @SuppressWarnings("unchecked")
    public void setItems(List<T> items) {
        dropdownAdapter.clear();
        dropdownAdapter.addAll(items);
        postDelayed(() -> dropdownAdapter.notifyDataSetChanged(), 100);
    }

    public void showDropDown(List<T> items) {
        setItems(items);
        showDropDown();
    }

    public boolean isShownDropDown() {
        return shownDropdownWindow != null;
    }

    public void showDropDown() {
        if (shownDropdownWindow != null || !hasFocus()) {
            return;
        }

        ViewParent parent = getParent();
        while (parent != null) {
            if (parent instanceof ScrollView) {
                ((ScrollView) parent).scrollTo(0, getBottom());
                break;
            }

            parent = parent.getParent();
        }
        shownDropdownWindow = createDropDownWindow(getContext(), dropdownView);
        setBackgroundResource(DEFAULT_BACKGROUND_TOP);
        setMinHeight(DEFAULT_MIN_HEIGHT);
        setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
        shownDropdownWindow.showAsDropDown(this);
        if (dropdownView != null) {
            dropdownView.postDelayed(() -> dropdownView.scrollTo(0, 0), 300);
        }
    }

    public void dismissDropdown() {
        if (shownDropdownWindow != null) {
            Observable.just(shownDropdownWindow)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(PopupWindow::dismiss, ZummaApiErrorHandler::handleError);
        }
    }

    protected PopupWindow createDropDownWindow(Context context, View contentView) {
        final PopupWindow dropdownWindow = new PopupWindow(context);
        dropdownWindow.setOutsideTouchable(false);
        dropdownWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dropdownWindow.setWidth(getMeasuredWidth());
        dropdownWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        dropdownWindow.setContentView(contentView);
        dropdownWindow.setOnDismissListener(() -> {
            setBackgroundResource(DEFAULT_BACKGROUND);
            setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
            shownDropdownWindow = null;
        });
        dropdownWindow.setAnimationStyle(R.style.Animation_Dropdown);
        dropdownWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        dropdownWindow.update();
        return dropdownWindow;
    }

    protected ListView createDropDownView(Context context, ListAdapter adapter) {
        final ListView listView = new ListView(context);
        listView.setDividerHeight(DEFAULT_DIVIDER_HEIGHT);
        listView.setScrollbarFadingEnabled(false);
        listView.setAdapter(adapter);
        listView.setBackgroundResource(DEFAULT_BACKGROUND_BOTTOM);

        return listView;
    }

    protected View createDropDownEmptyView(Context context) {
        TextView emptyView = new TextView(context);
        emptyView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        emptyView.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);

        return emptyView;
    }

    public T getItem(int position) {
        return dropdownAdapter.getItem(position);
    }

    @Override
    protected void onDetachedFromWindow() {
        dismissDropdown();
        super.onDetachedFromWindow();
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.GONE) {
            dismissDropdown();
        }

        super.setVisibility(visibility);
    }

    interface OnDropDownItemClickListener {
        void onDropDownItemClick(int position);
    }

    public class DropDownAdapter extends ArrayAdapter<T> {

        public DropDownAdapter(Context context) {
            super(context, R.layout.item_simple_string);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            view.setOnClickListener(v -> {
                dismissDropdown();
                if (listener != null) {
                    listener.onDropDownItemClick(position);
                }
            });
            return view;
        }
    }
}
