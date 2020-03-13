package com.zslide.view.editprofile;

import android.content.Intent;

import com.zslide.IntentConstants;
import com.zslide.activities.UploadImageCreateActivity;
import com.zslide.view.base.Navigator;

/**
 * Created by chulwoo on 2018. 1. 10..
 */

public class EditProfileNavigator {

    private final Navigator navigator;

    public EditProfileNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public void openImageUploadPage(int requestCode) {
        Intent intent = new Intent(navigator.getContext(), UploadImageCreateActivity.class);
        intent.putExtra(IntentConstants.EXTRA_TYPE, UploadImageCreateActivity.UPLOAD_TYPE_USER);
        navigator.startForResult(intent, requestCode);
    }
}
