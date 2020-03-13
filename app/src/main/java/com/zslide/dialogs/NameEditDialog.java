package com.zslide.dialogs;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.User;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.StringUtil;

import java.util.regex.Pattern;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 2016. 11. 22..
 * <p>
 * TODO
 * 우선 급하게 만듦
 * 추후 다시 수정해야 함
 */
public class NameEditDialog extends BaseAlertDialog {

    private static final int TYPE_NICKNAME = 0;
    private static final int TYPE_NAME = 1;
    @BindView(R.id.message) TextView messageView;
    @BindView(R.id.content) EditText contentView;
    @BindColor(R.color.brown2) int HIGHLIGHT_COLOR;
    @BindColor(R.color.black) int FONT_COLOR;
    @BindString(R.string.message_error_name_short) String MESSAGE_NAME_TOO_SHORT;
    @BindString(R.string.message_error_nickname_short) String MESSAGE_NICKNAME_TOO_SHORT;

    String shortMessage;

    public static NameEditDialog newNicknameInstance() {
        return newInstance(TYPE_NICKNAME, "");
    }

    public static NameEditDialog newNameInstance() {
        return newInstance(TYPE_NAME, "");
    }

    public static NameEditDialog newNicknameInstance(String message) {
        return newInstance(TYPE_NICKNAME, message);
    }

    public static NameEditDialog newNameInstance(String message) {
        return newInstance(TYPE_NAME, message);
    }

    private static NameEditDialog newInstance(int type, String message) {

        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("message", message);

        NameEditDialog fragment = new NameEditDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_input;
    }

    @Override
    protected void setupLayout(View view) {
        int type = getArguments().getInt("type");
        String message = getArguments().getString("message");
        // TODO: 일단 급하게 만듦.
        if (!TextUtils.isEmpty(message)) {
            messageView.setText(message);
            messageView.setVisibility(View.VISIBLE);
        }

        User user = UserManager.getInstance().getUserValue();
        switch (type) {
            case TYPE_NAME:
                shortMessage = MESSAGE_NAME_TOO_SHORT;
                contentView.setHint(R.string.hint_name);
                contentView.setText(user.getName());
                break;
            case TYPE_NICKNAME:
                shortMessage = MESSAGE_NICKNAME_TOO_SHORT;
                if (TextUtils.isEmpty(message)) {
                    messageView.setText(R.string.message_alert_nickname);
                    messageView.setVisibility(View.VISIBLE);
                }
                contentView.setHint(R.string.hint_nickname);
                if (user.hasNickname()) {
                    contentView.setText(user.getNickname());
                }
                break;
        }

        Pattern pattern = StringUtil.getNamePattern();
        contentView.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) ->
                // 유니코드는 천지인 입력용
                !pattern.matcher(source).matches() ? "" : null,
                new InputFilter.LengthFilter(type == TYPE_NAME ? 20 : 6)});
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
                SimpleAlertDialog.newInstance(shortMessage).show(getFragmentManager(), "error");
                return;
            }

            sendServer(text);
        });
    }

    private void sendServer(String text) {
        int type = getArguments().getInt("type");
        if (type == TYPE_NAME) {
            ZummaApi.user().editName(text)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(User::getName)
                    .subscribe(newName -> {
                        User user = UserManager.getInstance().getUserValue();
                        user.setName(newName);
                        UserManager.getInstance().updateUser(user).subscribe(this::onSuccessEdit, ZummaApiErrorHandler::handleError);
                    }, ZummaApiErrorHandler::handleError);
        } else if (type == TYPE_NICKNAME) {
            ZummaApi.user().editNickname(text)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(User::getNickname)
                    .subscribe(newNickname -> {
                        User user = UserManager.getInstance().getUserValue();
                        user.setNickname(newNickname);
                        UserManager.getInstance().updateUser(user).subscribe(this::onSuccessEdit, ZummaApiErrorHandler::handleError);
                    }, ZummaApiErrorHandler::handleError);
        }
    }

    private void onSuccessEdit() {
        Toast.makeText(getActivity(), R.string.message_success_edit_profile, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
