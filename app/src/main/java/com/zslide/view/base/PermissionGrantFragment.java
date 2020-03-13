package com.zslide.view.base;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.subjects.SingleSubject;

import static com.kakao.kakaostory.StringSet.permission;

/**
 * Created by chulwoo on 2018. 1. 2..
 */
public abstract class PermissionGrantFragment extends BaseFragment implements PermissionContract.View {

    public abstract PermissionContract.ViewModel getPermissionViewModel();

    private static final String TAG_PERMISSION_DESC_DIALOG = "permission_desc";

    private SingleSubject<PermissionContract.PermissionResults> permissionResultsSubject;
    private PermissionContract.PermissionRequests permissionRequests;
    private PermissionContract.PermissionResults permissionResults;

    @Override
    public void onStop() {
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_PERMISSION_DESC_DIALOG);
        if (fragment != null && fragment instanceof DialogFragment) {
            ((DialogFragment) fragment).dismiss();
        }

        super.onStop();
    }

    @Override
    public Single<PermissionContract.PermissionResults> checkPermission(PermissionContract.PermissionRequests requests) {
        this.permissionResultsSubject = SingleSubject.create();
        this.permissionRequests = requests;
        this.permissionResults = new PermissionContract.PermissionResults(requests.getRequestCode());

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : requests.getPermissions()) {
            int result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }

            permissionResults.set(permission, result);
        }

        if (listPermissionsNeeded.isEmpty()) {
            sendPermissionResults();
        } else {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requests.getRequestCode());
        }

        return permissionResultsSubject.doOnSuccess(results -> {
            permissionResultsSubject = null;
            permissionRequests = null;
            permissionResults = null;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionResults.getRequestCode() == requestCode) {
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (permissions.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int result = grantResults[i];
                    if (result != PackageManager.PERMISSION_GRANTED &&
                            permissionRequests.isRequired(permission) &&
                            !shouldShowRequestPermissionRationale(permission)) {
                        // 반드시 필요한 권한에 대해선 다시 보지 않기를 체크하더라도 알릴 필요가 있음
                        listPermissionsNeeded.add(permission);
                    }

                    permissionResults.set(permission, result);
                }
            }

            if (listPermissionsNeeded.isEmpty()) {
                sendPermissionResults();
            } else {
                Fragment permissionDialog = getFragmentManager().findFragmentByTag(TAG_PERMISSION_DESC_DIALOG);
                if (permissionDialog != null && permissionDialog instanceof DialogFragment) {
                    ((PermissionDescriptionDialog) permissionDialog).dismiss();
                }

                //permission dialog를 띄운 경우, Fragment의 onPause가 호출되면서 subject가 dispose 된 것을 가정
                int rationaleMessageResource = getPermissionViewModel().getRationaleMessageResource(listPermissionsNeeded);
                PermissionDescriptionDialog.newInstance(rationaleMessageResource)
                        .doOnCancel(this::sendPermissionResults)
                        .show(getFragmentManager(), permission);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void sendPermissionResults() {
        if (permissionResultsSubject != null) {
            permissionResultsSubject.onSuccess(permissionResults);
        }
    }
}