package com.zslide.data.remote;

import com.zslide.data.model.Family;
import com.zslide.data.model.HomeZmoney;
import com.zslide.data.model.InviteInfo;
import com.zslide.data.model.User;
import com.zslide.data.remote.api.UserApi;
import com.zslide.data.remote.base.AbstractRemoteSource;

import io.reactivex.Single;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public class RemoteUserSource extends AbstractRemoteSource {

    private final UserApi userApi;

    public RemoteUserSource() {
        userApi = create(UserApi.class);
    }

    public Single<User> getMe() {
        return userApi.getMe();
    }

    public Single<HomeZmoney> getHomeZmoney(long familyId) {
        return userApi.getHomeZmoney(familyId);
    }

    public Single<Family> getFamily(long familyId) {
        if (familyId == 0) {
            return Single.just(Family.NULL);
        } else {
            return userApi.getFamily(familyId);
        }
    }

    public Single<InviteInfo> getInviteInfo() {
        return userApi.getInviteInfo();
    }
}
