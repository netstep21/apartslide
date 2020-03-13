package com.zslide.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.data.AuthenticationManager;
import com.zslide.data.UserManager;
import com.zslide.data.model.User;
import com.zslide.dialogs.BirthYearPickerDialog;
import com.zslide.dialogs.NameEditDialog;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.models.LevelInfo;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.view.base.NavigatorFactory;
import com.zslide.view.editprofile.EditProfileContract;
import com.zslide.view.editprofile.EditProfileNavigator;
import com.zslide.view.editprofile.EditProfileViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 2016. 11. 22..
 */

public class MyAccountActivity extends BaseActivity {

    @BindView(R.id.thumbnail) ImageView thumbnailView;
    @BindView(R.id.nickname) TextView nicknameView;
    @BindView(R.id.level) TextView levelView;
    @BindView(R.id.name) TextView nameView;
    @BindView(R.id.birthYear) TextView birthYearView;
    @BindView(R.id.sex) TextView sexView;
    @BindView(R.id.alertEmail) View alertEmailView;
    @BindView(R.id.alertMessage) TextView alertMessageView;
    @BindView(R.id.linkAccount) TextView linkAccountView;
    @BindView(R.id.authMessage) TextView authMessageView;

    @BindColor(R.color.black) int BLACK;
    @BindColor(R.color.link_email) int EMAIL_LINK_COLOR;
    @BindColor(R.color.link_kakao) int KAKAO_LINK_COLOR;
    @BindColor(R.color.link_naver) int NAVER_LINK_COLOR;

    private EditProfileContract.ViewModel viewModel;
    private Disposable userSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new EditProfileViewModel(new EditProfileNavigator(NavigatorFactory.create(this)));
        userSubscription = UserManager.getInstance()
                .getUserObservable()
                .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(this::updateUi);

        alertMessageView.setText(Html.fromHtml(
                getString(R.string.message_alert_auth).replace("**", "</b>").replace("*", "<b>")));
        linkAccountView.setText(Html.fromHtml("<b>" + getString(R.string.label_alert_auth) + "</b>"));
    }

    private void updateUi(User user) {
        Glide.with(this)
                .load(user.hasProfileImage() ? user.getProfileImageUrl() : R.drawable.bg_my_account_thumbnail)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bg_my_account_thumbnail)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(thumbnailView);

        nicknameView.setText(user.getNickname());
        String displayLevel = "";
        if (user.getLevelInfo() == null) {
            Crashlytics.log("LevelInfo is null...");
        } else {
            LevelInfo.Advantage advantage = user.getLevelInfo().getAdvantage();
            if (advantage == null) {
                Crashlytics.log("Advantage is null...");
            } else {
                displayLevel = user.getLevelInfo().getAdvantage().getName();
            }
        }
        levelView.setText(displayLevel);
        nameView.setText(user.getName());
        birthYearView.setText(user.getBirthYear());
        sexView.setText(user.getSex().getSimpleKorean());
        if (user.isAccountLinked()) {
            alertEmailView.setVisibility(View.GONE);
            authMessageView.setVisibility(View.VISIBLE);

            String type = "";
            int color = 0;
            switch (user.getSocialAccount().getAuthType()) {
                case EMAIL:
                    type = user.getEmail();
                    color = EMAIL_LINK_COLOR;
                    break;
                case KAKAO:
                    type = getString(R.string.label_link_account_kakao);
                    color = KAKAO_LINK_COLOR;
                    break;
                case NAVER:
                    type = getString(R.string.label_link_account_naver);
                    color = NAVER_LINK_COLOR;
                    break;
            }

            SpannableString message = new SpannableString(
                    getString(R.string.message_login_with, type));
            message.setSpan(new ForegroundColorSpan(color), 0, type.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            message.setSpan(new StyleSpan(Typeface.BOLD), 0, type.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            authMessageView.setText(message);
        } else {
            alertEmailView.setVisibility(View.VISIBLE);
            authMessageView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        if (userSubscription != null) {
            userSubscription.dispose();
        }
        super.onDestroy();
    }

    @OnClick(R.id.thumbnailContainer)
    public void editThumbnail() {
        viewModel.onThumbnailClick();
    }

    @OnClick(R.id.nicknameContainer)
    public void editNickname() {
        NameEditDialog.newNicknameInstance().show(getSupportFragmentManager(), "edit_nickname");
    }

    @OnClick(R.id.levelContainer)
    public void openLevelPage() {
        Navigator.startLevelBenefitActivity(this);
    }

    @OnClick(R.id.nameContainer)
    public void editName() {
        NameEditDialog.newNameInstance().show(getSupportFragmentManager(), "edit_name");
    }

    @OnClick(R.id.birthYearContainer)
    public void editBirthYear() {
        int birthYear = Integer.parseInt(birthYearView.getText().toString());
        BirthYearPickerDialog.newInstance(birthYear)
                .setOnConfirmListener(year -> ZummaApi.user().editBirthYear(String.valueOf(year))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(User::getStrBirth)
                        .subscribe(newBirth -> {
                            Toast.makeText(this, R.string.message_success_edit_profile, Toast.LENGTH_SHORT).show();
                            User user = UserManager.getInstance().getUserValue();
                            user.setStrBirth(newBirth);
                            UserManager.getInstance().updateUser(user).subscribe();
                        }, ZummaApiErrorHandler::handleError))
                .show(getSupportFragmentManager(), "edit_name");
    }

    @OnClick(R.id.linkAccount)
    public void linkAccount() {
        Navigator.startAccountLinkActivity(this);
    }

    @OnClick(R.id.logout)
    public void confirmLogout() {
        int resId = UserManager.getInstance().getUserValue().isAccountLinked() ?
                R.string.message_confirm_logout : R.string.message_confirm_logout_without_auth;
        SimpleAlertDialog.newInstance(getString(resId), true)
                .setOnConfirmListener(this::logout)
                .show(getSupportFragmentManager(), "confirm");
    }

    @OnClick(R.id.leave)
    public void leave() {
        Navigator.startLeaveActivity(this);
    }

    private void logout() {
        AuthenticationManager.getInstance().logout()
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(() -> Navigator.startAuthActivity(this), ZummaApiErrorHandler::handleError);
    }

    @Override
    public String getScreenName() {
        return getString(R.string.label_account_my);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_my_account;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!viewModel.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
