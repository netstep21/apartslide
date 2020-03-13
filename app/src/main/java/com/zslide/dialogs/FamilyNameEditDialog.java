package com.zslide.dialogs;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.StringUtil;

import java.util.regex.Pattern;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 5. 26..
 */
public class FamilyNameEditDialog extends BaseAlertDialog {

    @BindView(R.id.content) EditText contentView;
    @BindColor(R.color.black) int FONT_COLOR;
    @BindColor(R.color.brown2) int HIGHLIGHT_COLOR;
    @BindString(R.string.message_error_family_short) String MESSAGE_TOO_SHORT;
    private Action1<String> inputListener;

    public static FamilyNameEditDialog newInstance(String prevText) {

        Bundle args = new Bundle();
        args.putString("text", prevText);

        FamilyNameEditDialog fragment = new FamilyNameEditDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public FamilyNameEditDialog setOnModifyListener(Action1<String> listener) {
        this.inputListener = listener;
        return this;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_input;
    }

    @Override
    protected void setupLayout(View view) {
        contentView.setHint(R.string.hint_family_name);

        String text = getArguments().getString("text");
        contentView.setText(text);

        Pattern pattern = StringUtil.getFamilyNamePattern();
        contentView.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) ->
                // 유니코드는 천지인 입력용
                !pattern.matcher(source).matches() ? "" : null,
                new InputFilter.LengthFilter(10)});

        contentView.getBackground().setColorFilter(HIGHLIGHT_COLOR, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    protected void setupDialog(AlertDialog.Builder builder) {
        builder.setPositiveButton(R.string.label_modify, null);
        builder.setNegativeButton(R.string.label_cancel, null);
    }

    @Override
    protected void setupButton(Button positiveButton, Button negativeButton, Button neutralButton) {
        super.setupButton(positiveButton, negativeButton, neutralButton);
        positiveButton.setTextColor(FONT_COLOR);
        negativeButton.setTextColor(FONT_COLOR);
        positiveButton.setOnClickListener(v -> {
            String text = contentView.getText().toString().trim();
            if (text.length() < 2) {
                SimpleAlertDialog.newInstance(MESSAGE_TOO_SHORT).show(getFragmentManager(), "error");
                return;
            }

            sendServer(text);
        });
    }

    private void sendServer(String name) {
        long familyId = UserManager.getInstance().getFamilyValue().getId();
        ZummaApi.user().editFamilyName(familyId, name)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Family::getName)
                .subscribe(familyName -> {
                    Family family = UserManager.getInstance().getFamilyValue();
                    family.setName(familyName);
                    UserManager.getInstance().updateFamily(family)
                            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                Toast.makeText(getActivity(), R.string.message_success_edit_profile, Toast.LENGTH_SHORT).show();
                                if (inputListener != null) {
                                    inputListener.call(name);
                                }

                                dismiss();
                            });
                }, ZummaApiErrorHandler::handleError);
    }
}
