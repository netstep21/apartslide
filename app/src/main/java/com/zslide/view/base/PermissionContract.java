package com.zslide.view.base;

import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;
import lombok.Getter;

/**
 * Created by chulwoo on 2018. 1. 2..
 */

public interface PermissionContract {

    interface View {

        Single<PermissionResults> checkPermission(PermissionRequests requests);
    }

    interface ViewModel {

        Single<PermissionResults> checkPermission(PermissionRequests requests);

        int getRationaleMessageResource(List<String> permissions);
    }

    class PermissionRequests {

        @Getter private final int requestCode;
        private HashMap<String, Boolean> permissions;

        public PermissionRequests(int requestCode) {
            this.requestCode = requestCode;
            this.permissions = new HashMap<>();
        }

        public PermissionRequests(int requestCode, String... permissions) {
            this(requestCode);

            for (String permission : permissions) {
                this.permissions.put(permission, false);
            }
        }

        public PermissionRequests add(String permission) {
            permissions.put(permission, false);
            return this;
        }

        public PermissionRequests addRequired(String permission) {
            permissions.put(permission, true);
            return this;
        }

        public List<String> getPermissions() {
            return new ArrayList<>(permissions.keySet());
        }

        public boolean isRequired(String permission) {
            return permissions.containsKey(permission) && permissions.get(permission);
        }
    }

    /**
     * Created by chulwoo on 2017. 11. 30..
     */

    class PermissionResults {

        @Getter private final int requestCode;
        private HashMap<String, Integer> results;

        public PermissionResults(int requestCode) {
            this.requestCode = requestCode;
            this.results = new HashMap<>();
        }

        public PermissionResults set(String permission, int result) {
            results.put(permission, result);
            return this;
        }

        public List<String> getPermissions() {
            return new ArrayList<>(results.keySet());
        }

        public boolean isGrantedPermission(String permission) {
            return results.containsKey(permission) && results.get(permission) == PackageManager.PERMISSION_GRANTED;
        }

        public boolean isGrantedPermissions(String... permissions) {
            for (String permission : permissions) {
                if (!isGrantedPermission(permission)) {
                    return false;
                }
            }

            return true;
        }

        public boolean isGrantedAll() {
            for (Integer grantResult : results.values()) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }

            return true;
        }
    }
}