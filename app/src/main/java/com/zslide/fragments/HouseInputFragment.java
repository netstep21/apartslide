package com.zslide.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.zslide.R;
import com.zslide.utils.DisplayUtil;

import butterknife.BindView;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

/**
 * Created by chulwoo on 16. 4. 6..
 */
public class HouseInputFragment extends InputFragment {

    @BindView(R.id.detailAddress) EditText houseView;

    public static HouseInputFragment newInstance() {
        return new HouseInputFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_house_input;
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            houseView.requestFocus();
        }
    }


    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
    }

    public String getDetailAddress() {
        return houseView.getText().toString();
    }

    @OnTextChanged(value = R.id.detailAddress, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    @Override
    protected void onInputStateChanged() {
        super.onInputStateChanged();
    }

    @Override
    public boolean isCompleted() {
        return getDetailAddress().length() > 1;
    }

    @OnEditorAction({R.id.detailAddress})
    boolean onEditorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (isCompleted()) {
                complete();
            }
            return true;
        }

        return false;
    }

    @OnFocusChange({R.id.detailAddress})
    public void showKeyboard(View v, boolean hasFocus) {
        if (hasFocus) {
            v.postDelayed(() -> DisplayUtil.showKeyboard(getContext(), v), 100);
        }
    }
}
