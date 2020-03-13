package com.zslide.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.data.model.Address;
import com.zslide.network.ZummaApi;

import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by chulwoo on 2016. 4. 6..
 */
public class AddressAutoCompleteTextView extends ZummaAutoCompleteTextView<Address> {

    public AddressAutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    public AddressAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AddressAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AddressAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setHint(R.string.message_input_dong);
    }

    @Override
    public Observable<List<Address>> search(String keyword) {
        return ZummaApi.address().items(keyword);
    }

    @Override
    protected View createDropDownEmptyView(Context context) {
        View emptyView = LayoutInflater.from(context).inflate(R.layout.view_empty_address, null, false);
        ButterKnife.findById(emptyView, R.id.button).setOnClickListener(v -> Navigator.startPersonalHelp(context));
        return emptyView;
    }
}
