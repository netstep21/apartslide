package com.zslide.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.models.Sex;
import com.zslide.utils.DisplayUtil;
import com.zslide.utils.StringUtil;
import com.zslide.view.auth.AuthActivity;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

/**
 * Created by chulwoo on 16. 4. 6..
 */
public class ProfileInputFragment extends InputFragment {

    public static final int MAX_LENGTH_NAME = 10;
    public static final int MAX_LENGTH_NICKNAME = 6;

    private static final int DEFAULT_YEAR = 1980;
    private static final int DEFAULT_MONTH = 1;
    private static final int DEFAULT_DAY = 1;

    @BindView(R.id.name) EditText nameView;
    @BindView(R.id.nickname) EditText nicknameView;
    @BindView(R.id.birthYear) TextView birthYearView;
    @BindView(R.id.birthMonth) TextView birthMonthView;
    @BindView(R.id.birthDay) TextView birthDayView;
    @BindView(R.id.sex) RadioGroup sexView;
    @BindView(R.id.man) RadioButton manView;
    @BindView(R.id.woman) RadioButton womanView;

    private String name;
    private String nickname;
    private int year;
    private int month; // 1 based (1-12)
    private int day;
    private Sex sex;
    private boolean editable;

    public static ProfileInputFragment newInstance(String name, String nickname, int year, int month, int day, Sex sex, boolean editable) {
        Bundle args = new Bundle();
        args.putString(IntentConstants.EXTRA_NAME, name);
        args.putString(IntentConstants.EXTRA_NICKNAME, nickname);
        args.putInt(IntentConstants.EXTRA_YEAR, year);
        args.putInt(IntentConstants.EXTRA_MONTH, month);
        args.putInt(IntentConstants.EXTRA_DAY, day);
        args.putSerializable(IntentConstants.EXTRA_SEX, sex);
        args.putBoolean(IntentConstants.EXTRA_EDITABLE, editable);

        ProfileInputFragment instance = new ProfileInputFragment();
        instance.setArguments(args);

        return instance;
    }

    public static ProfileInputFragment newInstance(String name, String nickname, int year, int month, int day, Sex sex) {
        return newInstance(name, nickname, year, month, day, sex, true);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_profile_input;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readFromBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
    }

    private void readFromBundle(Bundle bundle) {
        name = bundle.getString(IntentConstants.EXTRA_NAME);
        nickname = bundle.getString(IntentConstants.EXTRA_NICKNAME, "");
        year = bundle.getInt(IntentConstants.EXTRA_YEAR);
        month = bundle.getInt(IntentConstants.EXTRA_MONTH);
        day = bundle.getInt(IntentConstants.EXTRA_DAY);
        sex = (Sex) bundle.getSerializable(IntentConstants.EXTRA_SEX);
        editable = bundle.getBoolean(IntentConstants.EXTRA_EDITABLE);
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        nameView.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            // 유니코드는 천지인 입력용
            return !StringUtil.getNamePattern().matcher(source).matches() ? "" : null;
        }, new InputFilter.LengthFilter(MAX_LENGTH_NAME)});
        nicknameView.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            // 유니코드는 천지인 입력용
            return !StringUtil.getNamePattern().matcher(source).matches() ? "" : null;
        }, new InputFilter.LengthFilter(MAX_LENGTH_NICKNAME)});
        bindName(name);
        bindNickname(nickname);
        bindBirth(year, month, day);
        bindSex(sex);
        setEditable(editable);

        if (!(getActivity() instanceof AuthActivity)) {
            nicknameView.setVisibility(View.GONE);
        }
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
            nameView.clearFocus();
            nameView.requestFocus();
        }
    }

    protected void bindName(String name) {
        nameView.setText(name);
    }

    protected void bindNickname(String nickname) {
        nicknameView.setText(nickname);
    }

    protected void bindBirth(int year, int month, int day) {
        if (year != -1) {
            birthYearView.setText(getString(R.string.format_year, year));
            birthYearView.setGravity(Gravity.CENTER);
        }

        if (month != -1) {
            birthMonthView.setText(getString(R.string.format_month, month));
            birthMonthView.setGravity(Gravity.CENTER);
        }

        if (day != -1) {
            birthDayView.setText(getString(R.string.format_day, day));
            birthDayView.setGravity(Gravity.CENTER);
        }
    }

    protected void bindSex(Sex sex) {
        switch (sex) {
            case MAN:
                sexView.check(R.id.man);
                break;
            case WOMAN:
                sexView.check(R.id.woman);
                break;
            default:
                sexView.check(R.id.man);
                break;
        }
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        setupDisplayMode(editable);
    }

    private void setupDisplayMode(boolean editable) {
        nameView.setEnabled(editable);
        nicknameView.setEnabled(editable);
        birthYearView.setEnabled(editable);
        birthMonthView.setEnabled(editable);
        birthDayView.setEnabled(editable);
        manView.setEnabled(editable);
        womanView.setEnabled(editable);

        if (editable) {
            manView.setVisibility(View.VISIBLE);
            womanView.setVisibility(View.VISIBLE);
            birthYearView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_dropdown_down, 0);
            birthMonthView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_dropdown_down, 0);
            birthDayView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_dropdown_down, 0);
        } else {
            switch (sex) {
                case MAN:
                    womanView.setVisibility(View.GONE);
                    break;
                case WOMAN:
                    manView.setVisibility(View.GONE);
                    break;
            }

            birthYearView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            birthMonthView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            birthDayView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    @OnFocusChange({R.id.name, R.id.nickname})
    public void onFocusChangedNameView(boolean visible) {
        if (visible) {
            nameView.postDelayed(() -> DisplayUtil.showKeyboard(getContext(), nameView), 150);
        } else {
            if (!(nameView.hasFocus() || nicknameView.hasFocus())) {
                DisplayUtil.hideKeyboard(getActivity());
            }
        }
    }

    @OnClick({R.id.birthYear, R.id.birthMonth, R.id.birthDay})
    public void showDatePicker() {
        Context context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light_Dialog);
        } else {
            context = getActivity();
        }

        new DatePickerDialog(context,
                (view, year, monthOfYear, dayOfMonth) -> {
                    this.year = year;
                    this.month = monthOfYear + 1;
                    this.day = dayOfMonth;
                    bindBirth(this.year, this.month, this.day);
                    onInputStateChanged();
                },
                (year == -1) ? DEFAULT_YEAR : this.year,
                ((month == -1) ? DEFAULT_MONTH : this.month) - 1,
                (day == -1) ? DEFAULT_DAY : this.day).show();
    }

    @OnClick({R.id.man, R.id.woman})
    public void onSelectedSex(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.man:
                sex = Sex.MAN;
                break;
            case R.id.woman:
                sex = Sex.WOMAN;
                break;
        }
        onInputStateChanged();
    }

    @OnTextChanged(value = R.id.name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onNameChanged(CharSequence name) {
        onInputStateChanged();
    }


    @OnTextChanged(value = R.id.nickname, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onNicknameChanged(CharSequence nickname) {
        onInputStateChanged();
    }

    @Override
    public boolean isCompleted() {
        return isInputCompleted();
    }

    private boolean isInputCompleted() {
        return !TextUtils.isEmpty(getName()) && !TextUtils.isEmpty(getNickname())
                && year != -1 && month != -1 && day != -1;
    }

    public String getName() {
        return nameView.getText().toString().trim();
    }

    public String getNickname() {
        return nicknameView.getText().toString().trim();
    }

    public int[] getBirth() {
        return new int[]{year, month, day};
    }

    public String getFormattedBirth() {
        return String.format("%s-%s-%s", year, month, day);
    }

    public Sex getSex() {
        if (manView.isChecked()) {
            return Sex.MAN;
        } else if (womanView.isChecked()) {
            return Sex.WOMAN;
        } else {
            return Sex.NONE;
        }
    }
}
