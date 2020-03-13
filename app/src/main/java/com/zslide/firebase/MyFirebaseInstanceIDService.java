package com.zslide.firebase;

import com.zslide.utils.ZLog;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by chulwoo on 15. 10. 21..
 * <p>
 * InstanceID 토큰이 업데이트 될 경우 호출된다.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        ZLog.i(this, "onTokenRefresh");
        MyFirebaseInstanceIDHandler.setSentGcmInfo(this, false);
        MyFirebaseInstanceIDHandler.sendGcmInfoIfNeeded(this);
    }
}
