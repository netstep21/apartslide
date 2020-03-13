package com.zslide.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.models.LevelInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by jdekim43 on 2016. 5. 26..
 */
public class UserProfileView extends LinearLayout {

    @BindView(R.id.thumbnail) ImageView thumbnailView;
    @BindView(R.id.level) ImageView levelView;
    @BindView(R.id.nickname) TextView nicknameView;
    @BindView(R.id.name) TextView nameView;
    @BindView(R.id.meBadge) View meBadge;
    @BindView(R.id.leaderBadge) View leaderBadge;

    int[] levelIcons;

    public UserProfileView(Context context) {
        this(context, null);
    }

    public UserProfileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserProfileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_user_profile_name, this);
        ButterKnife.bind(this);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UserProfileView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.view_user_profile_name, this);
        ButterKnife.bind(this);
        init(context);
    }

    private void loadLevelIcons(Resources res) {
        TypedArray a = res.obtainTypedArray(R.array.level_icons);
        levelIcons = new int[a.length()];
        for (int i = 0; i < levelIcons.length; i++) {
            levelIcons[i] = a.getResourceId(i, R.drawable.img_lv_0);
        }
        a.recycle();
    }

    public void setUser(User user) {
        Glide.with(getContext())
                .load(TextUtils.isEmpty(user.getProfileImageUrl())
                        ? R.drawable.img_profile_default
                        : user.getProfileImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(thumbnailView);

        int level= 0;
        if (user.getLevelInfo() == null) {
            // 대체 왜... 서버에선 이걸 해결 안해줄까;
            Crashlytics.log("LevelInfo is null...");
        } else {
            LevelInfo.Advantage advantage = user.getLevelInfo().getAdvantage();
            if (advantage == null) {
                // 대체 왜... 서버에선 이걸 해결 안해줄까;
                Crashlytics.log("Advantage is null...");
            } else {
                level = advantage.getPriority();
            }
        }

        if (level >= levelIcons.length) {
            levelView.setImageResource(levelIcons[levelIcons.length - 1]);
        } else {
            levelView.setImageResource(levelIcons[level]);
        }


        nicknameView.setText(user.getDisplayNickname(getContext()));
        nameView.setText(user.getBlurredName(getContext()));

        User me = UserManager.getInstance().getUserValue();
        Family family = UserManager.getInstance().getFamilyValue();
        setVisibility(meBadge, me.getId() == user.getId());
        setVisibility(leaderBadge, family.getFamilyLeaderId() == user.getId());
        /*setVisibleCertificationRequireView(certificationVisible);*/
    }

    /*public void setVisibleCertificationRequireView(boolean visible) {
        setVisibility(certificationRequireView, visible && !user.isCertified());
    }*/

    protected void init(Context context) {
        loadLevelIcons(context.getResources());
    }

    private void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
