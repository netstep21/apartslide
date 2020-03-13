package com.zslide.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.zslide.R;
import com.zslide.widget.ProgressButton;

/**
 * Created by chulwoo on 16. 4. 7..
 */
public abstract class InputFragment extends BaseFragment {

    protected ProgressButton completeButton;
    private String buttonLabel;
    private OnInputStateChangeListener inputStateChangeListener;
    private InputCompleteAction inputCompleteAction;

    public void useCompleteButton(String label, InputCompleteAction action) {
        buttonLabel = label;
        inputCompleteAction = action;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!TextUtils.isEmpty(buttonLabel)) {
            setupCompleteButton(getContext(), (ViewGroup) view, buttonLabel);
        }
    }

    @SuppressWarnings("unchecked")
    protected void setupCompleteButton(Context context, ViewGroup container, String label) {
        completeButton = (ProgressButton) LayoutInflater.from(context)
                .inflate(R.layout.view_button_input, container, false);
        completeButton.setEnabled(isCompleted());
        completeButton.setText(label);
        completeButton.setOnClickListener(v -> {
            completeButton.setProgressing(false);
            inputCompleteAction.complete();
        });
        if (container instanceof ScrollView) {
            ((ViewGroup) container.getChildAt(0)).addView(completeButton);
        } else {
            container.addView(completeButton);
        }
    }

    public void setOnInputStateChangeListener(OnInputStateChangeListener listener) {
        this.inputStateChangeListener = listener;
    }

    protected void onInputStateChanged() {
        boolean completed = isCompleted();
        if (inputStateChangeListener != null) {
            inputStateChangeListener.onInputStateChanged(completed);
        }

        if (completeButton != null) {
            completeButton.setEnabled(completed);
        }
    }

    public void setInputCompleteAction(InputCompleteAction action) {
        this.inputCompleteAction = action;
    }

    public void setCompleteButtonEnabled(boolean enabled) {
        if (completeButton == null) {
            throw new IllegalStateException("complete button must be setup before calling setCompleteButtonEnabled(boolean)");
        }

        completeButton.setEnabled(enabled);
    }

    public void showProgress() {
        if (completeButton == null) {
            throw new IllegalStateException("complete button must be setup before calling showProgress()");
        }
        completeButton.setProgressing(true);
    }

    public void hideProgress() {
        if (completeButton == null) {
            throw new IllegalStateException("complete button must be setup before calling hideProgress()");
        }
        completeButton.setProgressing(false);
    }

    protected void complete() {
        if (inputCompleteAction != null) {
            inputCompleteAction.complete();
        }
    }

    public abstract boolean isCompleted();

    public interface InputCompleteAction {
        void complete();
    }

    public interface OnInputStateChangeListener {
        void onInputStateChanged(boolean completed);
    }
}
