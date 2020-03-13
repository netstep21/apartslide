package com.zslide.network;

import com.zslide.Config;
import com.zslide.data.model.AlertMessage;
import com.zslide.data.remote.response.AuthenticationResponse;
import com.zslide.firebase.MyFirebaseInstanceID;
import com.zslide.models.AppValue;
import com.zslide.models.InviteInfo;
import com.zslide.models.Notification;
import com.zslide.models.PaginationData;
import com.zslide.models.StaticImage;
import com.zslide.models.response.SimpleApiResponse;
import com.zslide.network.service.GeneralService;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by chulwoo on 16. 9. 28..
 */
public class GeneralApi {

    private GeneralService generalService;

    protected GeneralApi(Retrofit retrofit) {
        generalService = retrofit.create(GeneralService.class);
    }

    public Observable<AlertMessage> version() {
        return generalService.getAlertMessage(Config.VERSION_CODE);
    }

    public Observable<SimpleApiResponse> sendGcmInfo(MyFirebaseInstanceID myFirebaseInstanceID) {
        return generalService.createGcmDevice(myFirebaseInstanceID.deviceId, myFirebaseInstanceID.token);
    }

    public Observable<SimpleApiResponse> setSlideEnabled(boolean enabled) {
        return generalService.setLockEnabled(enabled ? "on" : "off");
    }

    public Observable<SimpleApiResponse> participateATEvent(String code, String type) {
        return generalService.participateATEvent(code, type);
    }

    public Observable<PaginationData<Notification>> notifications(int page) {
        return generalService.getNotifications(page);
    }

    public Observable<Notification> readNotification(long id) {
        return generalService.readNotification(id, null);
    }

    public Observable<SimpleApiResponse> requestEmailCertificationCode(String email) {
        return generalService.requestEmailCertificationCode(email);
    }

    public Observable<SimpleApiResponse> emailCertification(String email, String certificationCode) {
        return generalService.emailCertification(email, certificationCode);
    }

    public Observable<SimpleApiResponse> requestEmailLoginCertificationCode(String email) {
        return generalService.requestEmailLoginCertificationCode(email);
    }

    public Observable<AuthenticationResponse> emailLoginCertification(String email, String certificationCode) {
        return generalService.emailLoginCertification(email, certificationCode);
    }

    public Observable<StaticImage> staticImage(String target) {
        return generalService.staticImage(target);
    }

    public Observable<AppValue> inviteReward() {
        return generalService.value("invite_reward");
    }

    public Observable<String> friendInviteBannerUrl() {
        return generalService.staticImage("zslide_friend_invite").map(StaticImage::getUrl);
    }

    public Observable<InviteInfo> inviteInfo() {
        return Observable.combineLatest(
                generalService.value("recommend_info").map(AppValue::getValue),
                generalService.value("invite_reward").map(AppValue::getValue),
                InviteInfo::new);
    }
}
