package com.zslide.dialogs;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.Navigator;
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
public class LevelUpDialog extends BaseAlertDialog {

    @BindView(R.id.icon) ImageView iconView;
    @BindView(R.id.message) TextView messageView;

    public static LevelUpDialog newInstance() {
        Bundle args = new Bundle();

        LevelUpDialog fragment = new LevelUpDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_level_up;
    }

    @Override
    protected void setupLayout(View view) {
        User user = UserManager.getInstance().getUserValue();
        if (user.getLevelInfo() == null) {
            // 대체 왜... 서버에선 이걸 해결 안해줄까;
            Crashlytics.log("LevelInfo is null...");
        } else {
            LevelInfo.Advantage advantage = user.getLevelInfo().getAdvantage();
            if (advantage == null) {
                // 대체 왜... 서버에선 이걸 해결 안해줄까;
                Crashlytics.log("Advantage is null...");
            } else {
                Glide.with(getContext()).load(advantage.getBoxImageUrl()).into(iconView);
                messageView.setText(getString(R.string.message_levelup, advantage.getName()));
            }
        }
    }

    @Override
    protected void setupDialog(AlertDialog.Builder builder) {
        builder.setNegativeButton(R.string.label_check_benefit, (dialogInterface, i) ->
                Navigator.startLevelBenefitActivity(getActivity()));
        builder.setPositiveButton(R.string.label_close, null);
    }
}
