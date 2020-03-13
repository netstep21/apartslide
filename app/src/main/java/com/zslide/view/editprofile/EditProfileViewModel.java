package com.zslide.view.editprofile;

import android.app.Activity;
import android.content.Intent;

import com.zslide.IntentConstants;
import com.zslide.data.UserManager;
import com.zslide.data.model.User;
import com.zslide.network.ZummaApi;
import com.zslide.utils.DLog;

import java.io.File;

/**
 * Created by chulwoo on 2018. 1. 10..
 */

public class EditProfileViewModel implements EditProfileContract.ViewModel {

    private int REQUEST_IMAGE_SELECT = 100;

    private final EditProfileNavigator navigator;

    public EditProfileViewModel(EditProfileNavigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public void onThumbnailClick() {
        navigator.openImageUploadPage(REQUEST_IMAGE_SELECT);
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                File file = (File) data.getSerializableExtra(IntentConstants.EXTRA_FILE);
                // TODO: 추후 api v4 생성 시 mvvm 적용
                ZummaApi.user().editProfileImage(UserManager.getInstance().getUserValue().getId(), file)
                        .subscribeOn(rx.schedulers.Schedulers.newThread())
                        .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                        .doOnTerminate(() -> deleteCropImage(file.getPath()))
                        .map(User::getProfileImageUrl)
                        .subscribe(profileUrl -> {
                            User user = UserManager.getInstance().getUserValue();
                            user.setProfileImageUrl(profileUrl);
                            UserManager.getInstance().updateUser(user).subscribe();
                        }, DLog::e);
            }

            return true;
        }

        return false;
    }

    private void deleteCropImage(String path) {
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                DLog.e(this, "이미지 파일이 존재하지 않습니다.\n" + file.getPath());
            }
            if (!file.delete()) {
                DLog.e(this, "사용자가 촬영한 프로필 이미지 파일을 삭제하는데 실패했습니다.\n" + path);
            }
        }
    }
}
