package com.zslide.network;

import android.content.Context;

import com.zslide.models.ShortDynamicLinkResult;
import com.zslide.network.service.FirebaseService;
import com.zslide.utils.GsonUtil;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by chulwoo on 2016. 12. 22..
 */

public class FirebaseApi {


    public static final String BASE_URL = "https://firebasedynamiclinks.googleapis.com/";
    private final FirebaseService firebaseService;

    public FirebaseApi(Context context) {
        firebaseService = buildRestAdapter(context).create(FirebaseService.class);
    }

    private Retrofit buildRestAdapter(Context context) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(ApiClientFactory.create(context, ApiClientFactory.TYPE_FIREBASE))
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.customGson()))
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    private Observable<ShortDynamicLinkResult> shortLink(Context context, String deepLink) {
        deepLink = deepLink.replace("zummaslide://", "/");
        deepLink = "http://zummaslide.com" + deepLink;
        String url = "https://sx3z6.app.goo.gl/" +
                "?link=" + deepLink +
                "&apn=" + context.getPackageName();
        return firebaseService.shortLinks(url);
    }
}
