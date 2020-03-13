package com.zslide.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by chulwoo on 16. 5. 30..
 */
public class FamilyCardView extends CardView {

    @BindView(R.id.familyThumbnail) ImageView familyThumbnailView;
    @BindView(R.id.name) TextView familyNameView;
    @BindView(R.id.leaderThumbnail) ImageView familyLeaderThumbnailView;
    @BindView(R.id.leaderNickname) TextView familyLeaderNicknameView;
    @BindView(R.id.count) TextView familyCountView;
    @BindView(R.id.join) RequestButton joinButton;

    private Family family;
    private FamilyJoinListener familyJoinListener;

    public FamilyCardView(Context context) {
        this(context, null);
    }

    public FamilyCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FamilyCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_family_card, this);
        ButterKnife.bind(this);
    }

    public void setFamily(Family family) {
        this.family = family;
        bind(family);
    }

    private void bind(Family family) {
        Context context = getContext();
        Glide.with(context)
                .load(TextUtils.isEmpty(family.getProfileImageUrl()) ?
                        R.drawable.img_family_profile_default : family.getProfileImageUrl())
                .placeholder(R.drawable.img_family_profile_default)
                .into(familyThumbnailView);
        User leader = family.getLeader();
        familyNameView.setText(family.getName());
        familyLeaderNicknameView.setText(leader.getDisplayNickname(context));
        Glide.with(context)
                .load(TextUtils.isEmpty(leader.getProfileImageUrl()) ?
                        R.drawable.img_profile_default : leader.getProfileImageUrl())
                .bitmapTransform(new CropCircleTransformation(context))
                .placeholder(R.drawable.img_profile_default)
                .into(familyLeaderThumbnailView);
        familyCountView.setText(context.getString(R.string.format_persons, family.getMembers().size()));
        User me = UserManager.getInstance().getUserValue();
        if (family.isMember(me) || family.getBlockedMembers().contains(me)) {
            joinButton.setEnabled(false);
        } else {
            joinButton.setEnabled(true);
        }
    }

    public void setFamilyJoinListener(FamilyJoinListener listener) {
        familyJoinListener = listener;
    }

    @OnClick(R.id.join)
    public void join() {
        Observable<Family> joinMethod;
        User user = UserManager.getInstance().getUserValue();
        if (family.isFamilyLeader(user)) {
            joinMethod = ZummaApi.user().joinFamily(user.getFamilyId(), family.getId());
        } else {
            joinMethod = ZummaApi.user().setFamily(family.getId());
        }

        joinMethod.observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> joinButton.setProgressing(false));

        joinButton.action(joinMethod, this::onSuccessJoin, this::onFailureJoin);
    }

    private void onSuccessJoin(Family family) {
        if (familyJoinListener != null) {
            familyJoinListener.onJoined(family);
        }
    }

    private void onFailureJoin(Throwable e) {
        ZummaApiErrorHandler.handleError(e);
    }

    public interface FamilyJoinListener {
        void onJoined(Family family);
    }
}
