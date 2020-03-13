package com.zslide.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.IntentConstants;
import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.dialogs.FamilyNameEditDialog;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.models.FamilyUser;
import com.zslide.models.TempApartment;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.DLog;
import com.zslide.utils.ZLog;
import com.zslide.widget.AccountInfoView;
import com.zslide.widget.CustomCollapsingToolbarLayout;
import com.zslide.widget.FamilySettingsMemberView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 5. 26..
 */
public class FamilyActivity extends BaseActivity {

    private static final String DIALOG_MODIFY_FAMILY_NAME = "dialog.modify_family_name";
    private static final String DIALOG_CHANGE_PROFILE = "dialog.change_profile";
    private static final int REQUEST_IMAGE_SELECT = 110;

    @BindView(R.id.collapsingToolbar) CustomCollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.toolbarTitle) TextView toolbarTitleView;
    @BindView(R.id.thumbnail) ImageView familyProfileView;
    @BindView(R.id.name) TextView nameView;
    @BindView(R.id.modifyName) View modifyNameButton;
    @BindView(R.id.address) TextView addressView;
    @BindView(R.id.apartType) ImageView apartTypeView;
    @BindView(R.id.tempApartmentContainer) ViewGroup tempApartmentContainer;
    @BindView(R.id.tempApartmentMessage) TextView tempApartmentMessageView;
    @BindView(R.id.tempApartmentAddress) View tempApartmentAddressButton;
    @BindView(R.id.tempApartmentAddressText) TextView tempApartmentAddressTextView;
    @BindView(R.id.dividerAboveFamily) View dividerAboveFamilyView;
    @BindView(R.id.familyList) FamilySettingsMemberView familySettingsMemberView;
    @BindView(R.id.accountInfo) AccountInfoView accountInfoView;
    @BindView(R.id.dividerAboveAccount) View dividerAboveAccountView;

    private boolean currentToolbarVisible = false;

    private CompositeDisposable disposables;

    @Override
    public String getScreenName() {
        return getString(R.string.screen_family_setting);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        disposables = new CompositeDisposable();
    }

    @OnClick(R.id.modifyName)
    public void showModifyFamilyNameDialog() {
        Family family = UserManager.getInstance().getFamilyValue();
        if (family.isNull()) {
            return;
        }

        FamilyNameEditDialog.newInstance(family.getName())
                .setOnModifyListener(nameView::setText)
                .show(getSupportFragmentManager(), DIALOG_MODIFY_FAMILY_NAME);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnClick(R.id.modifyProfile)
    public void showChangeFamilyProfileDialog() {
        Navigator.startUploadImageCreateActivity(this, REQUEST_IMAGE_SELECT,
                UploadImageCreateActivity.UPLOAD_TYPE_FAMILY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_SELECT) {
            if (resultCode == RESULT_OK) {
                File file = (File) data.getSerializableExtra(IntentConstants.EXTRA_FILE);
                User user = UserManager.getInstance().getUserValue();
                if (!user.isNull()) {
                    ZummaApi.user().editFamilyImage(user.getId(), file)
                            .subscribeOn(rx.schedulers.Schedulers.newThread())
                            .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                            .doOnTerminate(() -> deleteCropImage(file.getPath()))
                            .doOnTerminate(this::hideTitleProgress)
                            .subscribe(family -> {
                                        setProfileImage(family.getProfileImageUrl());
                                        UserManager.getInstance().fetchFamily().subscribe();
                                    },
                                    ZummaApiErrorHandler::handleError);
                }
            }
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_family;
    }

    @Override
    protected void onResume() {
        super.onResume();
        DLog.e(this, "onResume! why not subscribe");
        UserManager userManager = UserManager.getInstance();
        disposables.add(UserManager.getInstance().fetchUserAndFamily().subscribe());
        disposables.add(Observable.combineLatest(
                userManager.getUserObservable(),
                userManager.getFamilyObservable(),
                FamilyUser::new)
                .subscribeOn(Schedulers.io())
                .filter(FamilyUser::isNotNull)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bind, DLog::e));
    }

    @Override
    protected void onPause() {
        if (disposables != null) {
            disposables.clear();
        }
        super.onPause();
    }

    @Override
    protected void setupToolbar(Toolbar toolbar) {
        super.setupToolbar(toolbar);
        if (currentToolbarVisible) {
            toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp));
        } else {
            toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_white_24dp));
        }

        toolbarTitleView.setText(R.string.label_family_info);
        toolbarLayout.setOnScrimVisibleChangedListener(this::setToolbarVisible);
    }

    @Override
    protected void setupActionBar(ActionBar actionBar) {
        super.setupActionBar(actionBar);
        if (currentToolbarVisible) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_arrow_black);
        } else {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_arrow_white);
        }
    }

    protected void bind(FamilyUser familyUser) {
        DLog.e(this, "bind family activity");
        User user = familyUser.getUser();
        Family family = familyUser.getFamily();

        familySettingsMemberView.setInfo(familyUser.getUser(), familyUser.getFamily());

        modifyNameButton.setVisibility(family.isFamilyLeader(user) ? View.VISIBLE : View.INVISIBLE);
        setProfileImage(family.getProfileImageUrl());
        nameView.setText(family.getName());

        if (family.hasTempApartment()) {
            TempApartment tempApartment = family.getTempApartment();
            addressView.setText(tempApartment.getName());
            apartTypeView.setVisibility(View.GONE);
            tempApartmentContainer.setVisibility(View.VISIBLE);
            dividerAboveFamilyView.setVisibility(View.GONE);
            switch (tempApartment.getStatus()) {
                case TempApartment.STATUS_WAIT:
                    tempApartmentMessageView.setText(R.string.message_temp_apartment_wait);
                    tempApartmentAddressButton.setVisibility(View.GONE);
                    break;
                case TempApartment.STATUS_SUCCESS:
                    tempApartmentMessageView.setText(R.string.message_temp_apartment_success);
                    tempApartmentAddressTextView.setText(R.string.label_input_address_detail);
                    tempApartmentAddressButton.setVisibility(View.VISIBLE);
                    if (family.isFamilyLeader(user)) {
                        tempApartmentAddressButton.setOnClickListener(v -> {
                            User u = UserManager.getInstance().getUserValue();
                            if (family.isFamilyLeader(u)) {
                                Navigator.startTempApartmentCompleteActivity(this);
                            } else {
                                User leader = family.getLeader();
                                String message = getString(R.string.message_address_alert,
                                        leader.getBlurredName(this), leader.getBlurredPhoneNumber(true));
                                SimpleAlertDialog.newInstance(message)
                                        .show(getSupportFragmentManager(), "alert");
                            }
                        });
                    }
                    break;
                case TempApartment.STATUS_FAILURE:
                    tempApartmentMessageView.setText(R.string.message_temp_apartment_failure);
                    tempApartmentAddressTextView.setText(R.string.label_input_address);
                    if (family.isFamilyLeader(user)) {
                        tempApartmentAddressButton.setVisibility(View.VISIBLE);
                        tempApartmentAddressButton.setOnClickListener(v ->
                                Navigator.startTempApartmentCompleteActivity(this));
                    } else {
                        tempApartmentAddressButton.setVisibility(View.GONE);
                    }
                    break;
            }
        } else {
            tempApartmentContainer.setVisibility(View.GONE);
            addressView.setText(family.getApartment().getAddress());
            if (family.getApartment().isJoined()) {
                apartTypeView.setVisibility(View.VISIBLE);
            }
        }

        if (family.hasTempApartment() || family.getApartment().isJoined()) {
            accountInfoView.setVisibility(View.GONE);
            dividerAboveAccountView.setVisibility(View.GONE);
        } else {
            accountInfoView.setVisibility(View.VISIBLE);
            dividerAboveAccountView.setVisibility(View.VISIBLE);
            accountInfoView.setFamily(family);
        }
    }

    @OnClick(R.id.modifyAddress)
    protected void modifyAddress() {
        Family family = UserManager.getInstance().getFamilyValue();
        if (family.isNull()) {
            return;
        }

        if (family.getMembers().size() == 1) {
            Navigator.startFamilyRegistrationActivity(this);
        } else {
            Navigator.startFamilyMoveActivity(this);
        }
    }

    private void setToolbarVisible(boolean visible) {
        currentToolbarVisible = visible;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null || toolbar == null) {
            return;
        }

        if (visible) {
            toolbarTitleView.setVisibility(View.VISIBLE);
            toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp));
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_arrow_black);
        } else {
            toolbarTitleView.setVisibility(View.INVISIBLE);
            toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_white_24dp));
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_arrow_white);
        }
    }

    private void setProfileImage(String url) {
        familyProfileView.post(() ->
                glide().load(TextUtils.isEmpty(url) ? R.drawable.img_family_profile_default : url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.img_family_profile_default)
                        .into(familyProfileView));
    }

    private void deleteCropImage(String path) {
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                ZLog.e(this, "이미지 파일이 존재하지 않습니다.\n" + file.getPath());
            }
            if (!file.delete()) {
                ZLog.e(this, "사용자가 촬영한 프로필 이미지 파일을 삭제하는데 실패했습니다.\n" + path);
            }
        }
    }
}
