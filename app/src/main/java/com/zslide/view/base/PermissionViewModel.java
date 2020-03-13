package com.zslide.view.base;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by chulwoo on 2018. 1. 2..
 */

public class PermissionViewModel implements PermissionContract.ViewModel {

    private final PermissionContract.View permissionView;

    public PermissionViewModel(@NonNull PermissionContract.View permissionView) {
        this.permissionView = permissionView;
    }

    @Override
    public Single<PermissionContract.PermissionResults> checkPermission(PermissionContract.PermissionRequests requests) {
        return permissionView.checkPermission(requests);
    }

    @Override
    public int getRationaleMessageResource(List<String> permissions) {
        String className = getClass().getSimpleName();
        throw new UnsupportedOperationException("필수 권한 거부 시 안내할 메시지가 필요합니다. " + className + "#getRationaleMessageResource 메소드를 구현하세요. (" + className + ".java:0)");
    }
}