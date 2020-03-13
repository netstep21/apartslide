package com.zslide.firebase;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.zslide.IntentConstants;
import com.zslide.data.AuthenticationManager;
import com.zslide.data.UserManager;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.DLog;
import com.zslide.utils.EasySharedPreferences;
import com.zslide.utils.RxUtil;
import com.zslide.utils.ZLog;
import com.google.firebase.iid.FirebaseInstanceId;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 15. 10. 21..
 * <p>
 * TODO: static 메소드 전부 제거하고 intent로 핸들링
 */
public class MyFirebaseInstanceIDHandler extends IntentService {

    private static PublishSubject<MyFirebaseInstanceID> instanceIDSubject = PublishSubject.create();
    private static Observable<MyFirebaseInstanceID> instanceIDObservable;

    public MyFirebaseInstanceIDHandler() {
        super(MyFirebaseInstanceIDHandler.class.getSimpleName());
    }

    public static void sendGcmInfo(Context context, MyFirebaseInstanceID myFirebaseInstanceID) {
        ZummaApi.general().sendGcmInfo(myFirebaseInstanceID)
                .subscribeOn(Schedulers.newThread())
                .retryWhen(RxUtil::exponentialBackoff)
                .subscribe(result -> setSentGcmInfo(context, true), ZummaApiErrorHandler::handleError);
    }

    public static void sendGcmInfoIfNeeded(Context context) {
        ZLog.i(MyFirebaseInstanceID.class, "firebase instance id token: " + FirebaseInstanceId.getInstance().getToken());

        if (!isSentGcmInfo(context)) {
            if (AuthenticationManager.getInstance().isLoggedIn()) {
                long userId = UserManager.getInstance().getUserValue().getId();
                getGcmInfo(context, userId)
                        .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                        .subscribe(gcmInfo -> sendGcmInfo(context, gcmInfo), DLog::e);
            }
        }
    }

    public static Observable<MyFirebaseInstanceID> getGcmInfo(@NonNull Context context, long userId) {
        if (instanceIDObservable == null) {
            instanceIDObservable = instanceIDSubject.cacheWithInitialCapacity(1);

            Intent intent = new Intent(context, MyFirebaseInstanceIDHandler.class);
            intent.putExtra(IntentConstants.EXTRA_ID, userId);
            context.startService(intent);
        }

        return instanceIDObservable;
    }

    public static void setSentGcmInfo(Context context, boolean sent) {
        EasySharedPreferences.with(context).putBoolean("sendGcmInfo", sent);
    }

    public static boolean isSentGcmInfo(Context context) {
        return EasySharedPreferences.with(context).getBoolean("sendGcmInfo", false);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long userId = intent.getLongExtra(IntentConstants.EXTRA_ID, -1);
        String token = FirebaseInstanceId.getInstance().getToken();
        instanceIDSubject.onNext(new MyFirebaseInstanceID(String.valueOf(userId), token));
        instanceIDSubject.onComplete();
    }
}
