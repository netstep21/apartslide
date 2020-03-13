package com.zslide.dialogs;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.User;
import com.zslide.models.LevelInfo;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;

import butterknife.BindView;

/**
 * Created by chulwoo on 16. 7. 14..
 */
public class LevelDialog extends BaseAlertDialog {

    @BindView(R.id.icon) ImageView iconView;
    @BindView(R.id.levelName) TextView levelNameView;
    @BindView(R.id.levelPeriodMessage) TextView levelPeriodMessageView;

    public static LevelDialog newInstance() {

        Bundle args = new Bundle();

        LevelDialog fragment = new LevelDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_level;
    }

    @Override
    protected void setupLayout(View view) {
        User user = UserManager.getInstance().getUserValue();
        if (user.getLevelInfo() == null) {
            // 대체 왜... 서버에선 이걸 해결 안해줄까;
            Crashlytics.log("LevelInfo is null...");

            levelPeriodMessageView.setText(
                    getString(R.string.message_level_period,
                            user.getLevelInfo().getFromDate(),
                            user.getLevelInfo().getToDate()));
        } else {
            LevelInfo.Advantage advantage = user.getLevelInfo().getAdvantage();
            if (advantage == null) {
                // 대체 왜... 서버에선 이걸 해결 안해줄까;
                Crashlytics.log("Advantage is null...");
            } else {
                Glide.with(this).load(advantage.getImageUrl()).into(iconView);
                levelNameView.setText(advantage.getName());
            }
        }
    }

    @Override
    protected void setupDialog(AlertDialog.Builder builder) {
        builder.setPositiveButton(R.string.label_close, null);
    }
}
