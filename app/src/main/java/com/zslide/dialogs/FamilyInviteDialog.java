package com.zslide.dialogs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.IntentConstants;
import com.zslide.R;

import butterknife.BindColor;
import butterknife.BindView;

/**
 * Created by jdekim43 on 2016. 5. 26..
 */
public class FamilyInviteDialog extends BaseAlertDialog {

    @BindColor(R.color.subAccentColor) int POSITIVE_TEXT_COLOR;
    @BindColor(R.color.gray_7) int NEGATIVE_TEXT_COLOR;
    @BindView(R.id.text) TextView textView;

    private String message;
    private String userName;
    private String familyName;
    private String recommendCode;

    public static FamilyInviteDialog newInstance(String message, String userName, String familyName, String recommendCode) {
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString(IntentConstants.EXTRA_NAME, userName);
        args.putString(IntentConstants.EXTRA_FAMILY_NAME, familyName);
        args.putString(IntentConstants.EXTRA_RECOMMEND_CODE, recommendCode);

        FamilyInviteDialog instance = new FamilyInviteDialog();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            message = args.getString("message");
            userName = args.getString(IntentConstants.EXTRA_NAME);
            familyName = args.getString(IntentConstants.EXTRA_FAMILY_NAME);
            recommendCode = args.getString(IntentConstants.EXTRA_RECOMMEND_CODE);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_family_invite;
    }

    @Override
    protected void setupLayout(View view) {
        textView.setText(getString(R.string.invite_family_message_with_link, userName, familyName, message, recommendCode));
    }

    @Override
    protected void setupButton(Button positiveButton, Button negativeButton, Button neutralButton) {
        positiveButton.setTextColor(POSITIVE_TEXT_COLOR);
        negativeButton.setTextColor(NEGATIVE_TEXT_COLOR);
    }

    @Override
    protected void setupDialog(AlertDialog.Builder builder) {
        builder.setPositiveButton(R.string.label_clipboard, (dialog, which) -> {
            Context context = getActivity();
            String msg = getString(R.string.invite_family_message_with_link, userName, familyName, message, recommendCode);
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("invite", msg));
            Toast.makeText(context, R.string.message_copy, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton(R.string.label_cancel, null);
    }
}
