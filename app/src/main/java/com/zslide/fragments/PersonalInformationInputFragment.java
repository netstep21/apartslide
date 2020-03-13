package com.zslide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.dialogs.BirthYearPickerDialog;
import com.zslide.models.Sex;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by chulwoo on 16. 4. 14..
 */
public class PersonalInformationInputFragment extends BaseFragment {

    @BindView(R.id.birthYear) TextView birthYearView;
    @BindView(R.id.womanCheck) ImageView womanCheckView;
    @BindView(R.id.womanLabel) TextView womanLabelView;
    @BindView(R.id.manCheck) ImageView manCheckView;
    @BindView(R.id.manLabel) TextView manLabelView;
    @BindView(R.id.next) Button nextButton;

    private int birthYear = -1;
    private Sex sex = null;
    private OnInputCompletedListener listener;

    public static PersonalInformationInputFragment newInstance() {
        return newInstance(-1, null);
    }

    public static PersonalInformationInputFragment newInstance(int birthYear) {
        return newInstance(birthYear, null);
    }

    public static PersonalInformationInputFragment newInstance(Sex sex) {
        return newInstance(-1, sex);
    }

    public static PersonalInformationInputFragment newInstance(int birthYear, Sex sex) {
        Bundle args = new Bundle();
        args.putInt(IntentConstants.EXTRA_YEAR, birthYear);
        args.putSerializable(IntentConstants.EXTRA_SEX, sex);

        PersonalInformationInputFragment instance = new PersonalInformationInputFragment();
        instance.setArguments(args);
        return instance;
    }

    public PersonalInformationInputFragment setOnInputCompletedListener(OnInputCompletedListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(IntentConstants.EXTRA_YEAR, birthYear);
        outState.putSerializable(IntentConstants.EXTRA_SEX, sex);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int birthYear;
        Serializable sexSerial;
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            birthYear = args.getInt(IntentConstants.EXTRA_YEAR);
            sexSerial = args.getSerializable(IntentConstants.EXTRA_SEX);
        } else {
            birthYear = savedInstanceState.getInt(IntentConstants.EXTRA_YEAR);
            sexSerial = savedInstanceState.getSerializable(IntentConstants.EXTRA_SEX);
        }
        setBirthYear(birthYear);
        setSex((Sex) sexSerial);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_personal_information_input;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        if (birthYear == -1) {
            return;
        }

        this.birthYear = birthYear;
        birthYearView.setSelected(true);
        birthYearView.setText(String.valueOf(birthYear));
        invalidateNextButtonEnabled();
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        if (sex == null) {
            return;
        }

        this.sex = sex;
        boolean isWoman = Sex.WOMAN.equals(sex);
        onSexSelectedChanged(womanCheckView, womanLabelView, isWoman);
        onSexSelectedChanged(manCheckView, manLabelView, !isWoman);
        invalidateNextButtonEnabled();
    }

    @OnClick(R.id.birthYear)
    public void showYearSelectDialog() {
        BirthYearPickerDialog.newInstance(getBirthYear())
                .setOnConfirmListener(this::setBirthYear)
                .show(getFragmentManager(), "pick_birth_year");
    }

    @OnClick(R.id.woman)
    public void selectWoman() {
        setSex(Sex.WOMAN);
    }

    @OnClick(R.id.man)
    public void selectMan() {
        setSex(Sex.MAN);
    }

    @OnClick(R.id.next)
    public void next() {
        if (listener != null) {
            listener.onInputCompleted();
        }
    }

    private void onSexSelectedChanged(ImageView checkView, TextView labelView, boolean select) {
        checkView.setVisibility(select ? View.VISIBLE : View.GONE);
        labelView.setSelected(select);
    }

    private void invalidateNextButtonEnabled() {
        nextButton.setEnabled(birthYear != -1 && sex != null);
    }

    public interface OnInputCompletedListener {
        void onInputCompleted();
    }
}