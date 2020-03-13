package com.zslide.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.zslide.R;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.ZLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindDimen;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by chulwoo on 2016. 4. 6..
 * <p>
 * 입력된 값에 대한 결과 값 리스트를 API를 통해 받아와 보여주는 AutoCompleteTextView.
 * <p>
 */
public abstract class ZummaAutoCompleteTextView<T> extends DropDownEditText<T>
        implements DropDownEditText.OnDropDownItemClickListener {

    private static final int DEFAULT_DEBOUNCE_INTERVAL = 300;
    @BindDimen(R.dimen.spacing_smaller) int DRAWABLE_PADDING;
    private boolean editable = true;
    private boolean autoCompleteEnabled = true;
    private PublishSubject<String> autoCompleter;
    private List<OnClickListener> onClickListeners;
    private String latestKeyword;
    private T item;
    private OnItemSelectedListener<T> onItemSelectedListener;
    private SearchProgressListener searchProgressListener;

    public ZummaAutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    public ZummaAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZummaAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ZummaAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public abstract Observable<List<T>> search(String keyword);

    private void init() {
        setMaxLines(1);
        setInputType(InputType.TYPE_CLASS_TEXT);
        setCompoundDrawablePadding(DRAWABLE_PADDING);
        setOnDropDownItemClickListener(this);
        setOnTouchListener(new RightDrawableOnTouchListener() {
            @Override
            public boolean onDrawableTouch() {
                clear();
                requestFocusOnLastText();
                return false;
            }
        });
        autoCompleter = PublishSubject.create();
        autoCompleter.debounce(DEFAULT_DEBOUNCE_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribe(keyword -> {
                    this.latestKeyword = keyword;
                    if (TextUtils.isEmpty(keyword)) {
                        dismissDropdown();
                    } else {
                        Observable.just(searchProgressListener)
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .filter(listener -> listener != null)
                                .subscribe(listener -> listener.onStart(keyword), ZLog::e);

                        search(keyword)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::showDropDown, this::onFailureLoading, this::onFinishLoading);
                    }
                }, ZLog::e);
        onClickListeners = new ArrayList<>();
    }

    private void onFailureLoading(Throwable e) {
        dismissDropdown();
        ZummaApiErrorHandler.handleError(e);

        if (searchProgressListener != null) {
            searchProgressListener.onFinish();
        }
    }

    private void onFinishLoading() {
        if (searchProgressListener != null) {
            searchProgressListener.onFinish();
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener<T> listener) {
        this.onItemSelectedListener = listener;
    }

    public void setSearchProgressListener(SearchProgressListener searchProgressListener) {
        this.searchProgressListener = searchProgressListener;
    }

    @Override
    public void onDropDownItemClick(int position) {
        this.item = getItem(position);
        setItem(item);
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(item);
        }
    }

    public void setItem(T item) {
        this.item = item;
        setAutoCompleteEnabled(false);
        setText(item.toString());
        setEditable(false);
    }

    public boolean isAutoCompleteEnabled() {
        return autoCompleteEnabled;
    }

    public void setAutoCompleteEnabled(boolean enabled) {
        this.autoCompleteEnabled = enabled;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        setFocusableInTouchMode(editable);
        setFocusable(editable);
        setCursorVisible(editable);
    }

    @OnTextChanged(callback = OnTextChanged.Callback.TEXT_CHANGED)
    protected void onTextChanged(CharSequence s) {
        if (isAutoCompleteEnabled()) {
            if (autoCompleter != null) {
                autoCompleter.onNext(s.toString());
            }
            if ((s.length() > 0)) {
                setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_search_gray, 0, R.drawable.ic_cancel_gray, 0);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_search_gray, 0, 0, 0);
            }
        } else {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cancel_gray, 0);
        }
    }

    @OnClick
    protected void onClick() {
        if (!isAutoCompleteEnabled()) {
            restoreKeyword();
            requestFocusOnLastText();
        }

        for (OnClickListener listener : onClickListeners) {
            listener.onClick(this);
        }
    }

    private void requestFocusOnLastText() {
        postDelayed(() -> {
            requestFocus();
            int lastPosition = getText().length();
            if (lastPosition > 0) {
                setSelection(lastPosition);
            }
        }, 100);
    }

    public void addOnClickListener(OnClickListener listener) {
        onClickListeners.add(listener);
    }

    private void restoreKeyword() {
        setAutoCompleteEnabled(true);
        setEditable(true);
        setText(latestKeyword);
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(null);
        }
    }

    public void clear() {
        if (!isAutoCompleteEnabled()) {
            latestKeyword = "";
            restoreKeyword();
        } else {
            setText("");
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(null);
            }
        }
    }

    public T getCurrentItem() {
        return item;
    }

    public interface OnItemSelectedListener<ItemType> {
        void onItemSelected(@Nullable ItemType item);
    }

    public interface SearchProgressListener {
        void onStart(String keyword);

        void onFinish();
    }
}
