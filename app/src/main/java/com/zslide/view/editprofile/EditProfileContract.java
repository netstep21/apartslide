package com.zslide.view.editprofile;

import android.content.Intent;

/**
 * Created by chulwoo on 2018. 1. 10..
 */

public interface EditProfileContract {

    interface ViewModel {

        void onThumbnailClick();

        boolean handleActivityResult(int requestCode, int resultCode, Intent data);
    }
}
