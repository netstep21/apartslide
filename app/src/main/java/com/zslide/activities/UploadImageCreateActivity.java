package com.zslide.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.utils.TimeUtil;
import com.zslide.utils.ZLog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 6. 15..
 * Updated by chulwoo on 2016. 10. 26..
 * ImageCropper 라이브러리 사용해 다시 구현
 * 이미지 선택, 잘라내기 후 이미지 파일 생성 지원
 * 생성된 이미지 파일을 result로 넘김
 */
public class UploadImageCreateActivity extends BaseActivity {

    public static final int UPLOAD_TYPE_USER = 0;
    public static final int UPLOAD_TYPE_FAMILY = 1;
    private static final int STORE_IMAGE_PERMISSION_REQUEST_CODE = 100;
    protected ProgressDialog progressDialog;
    int FAMILY_PROFILE_HEIGHT = 400;
    int USER_PROFILE_HEIGHT = 192;
    private Uri pickedImageUri;
    private Uri croppedImageUri;

    private int type;

    @Override
    public String getScreenName() {
        return "이미지 선택";
    }

    @Override
    protected int getLayoutResourceId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("이미지 처리중입니다.");
        onNewIntent(getIntent());
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        this.type = intent.getIntExtra(IntentConstants.EXTRA_TYPE, UPLOAD_TYPE_USER);

        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = CropImage.getPickImageResultUri(this, data);
                onSuccessImagePick(imageUri);
            } else {
                finish();
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (result.getError() == null) {
                    onSuccessImageCrop(result.getUri());
                } else {
                    onFailureImageCrop(result.getError());
                }
            } else {
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 0 ||
                grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.message_permission_denied, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        switch (requestCode) {
            case CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE:
                CropImage.startPickImageActivity(this);
                break;
            case CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE:
                if (pickedImageUri != null) {
                    cropImage(this.pickedImageUri);
                } else {
                    Toast.makeText(this, R.string.message_retry, Toast.LENGTH_LONG).show();
                }
                break;
            case STORE_IMAGE_PERMISSION_REQUEST_CODE:
                if (croppedImageUri != null) {
                    storeImage(this.croppedImageUri);
                } else {
                    Toast.makeText(this, R.string.message_retry, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void onSuccessImagePick(Uri imageUri) {
        if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
            this.pickedImageUri = imageUri;
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
        } else {
            cropImage(imageUri);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void onSuccessImageCrop(Uri imageUri) {
        if (isWriteExternalStoragePermissionsRequired()) {
            this.croppedImageUri = imageUri;
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORE_IMAGE_PERMISSION_REQUEST_CODE);
        } else {
            storeImage(imageUri);
        }
    }

    private void storeImage(Uri uri) {
        storeImageObservable(uri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file -> {
                    Intent intent = new Intent();
                    intent.putExtra(IntentConstants.EXTRA_FILE, file);
                    setResult(RESULT_OK, intent);
                    finish();
                }, this::onFailureImageCrop);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Observable<File> storeImageObservable(Uri uri) {
        return Observable.create(subscriber -> {
            File pictureFile = getOutputMediaFile();
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                int limitHeight = 100;
                switch (type) {
                    case UPLOAD_TYPE_FAMILY:
                        limitHeight = FAMILY_PROFILE_HEIGHT;
                        break;
                    case UPLOAD_TYPE_USER:
                        limitHeight = USER_PROFILE_HEIGHT;
                        break;
                }

                int bitmapHeight = bitmap.getHeight();

                int destWidth = bitmap.getWidth();
                int destHeight = bitmapHeight;
                if (bitmapHeight > limitHeight) {
                    float scale = limitHeight / (bitmapHeight / 100.0f);
                    destWidth *= (scale / 100);
                    destHeight *= (scale / 100);
                }
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, true);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();

                subscriber.onNext(pictureFile);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    private File getOutputMediaFile() {
        String externalStoragePath = Environment.getExternalStorageDirectory().toString();
        File mediaStorageDir = new File(externalStoragePath + "/Pictures/Zummaslide/");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        return new File(mediaStorageDir + "/" + TimeUtil.format(new Date()) + ".jpg");
    }

    private void onFailureImageCrop(Throwable e) {
        onFailureImageCrop(getString(R.string.message_failure_store_image), e);
    }

    private void onFailureImageCrop(String message, Throwable e) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        ZLog.e(e);
        finish();
    }

    public void cropImage(Uri imageUri) {
        CropImage.ActivityBuilder builder = CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON);
        switch (type) {
            case UPLOAD_TYPE_FAMILY:
                builder.setAspectRatio(9, 5)
                        .setCropShape(CropImageView.CropShape.RECTANGLE);
                break;
            case UPLOAD_TYPE_USER:
                builder.setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL);
                break;
        }
        builder.start(this);
    }

    private boolean isWriteExternalStoragePermissionsRequired() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }
}
