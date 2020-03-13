package com.zslide.network;

import android.content.Context;

import com.zslide.Config;
import com.zslide.network.interceptors.FirebaseRequestInterceptor;
import com.zslide.network.interceptors.NaverRequestInterceptor;
import com.zslide.network.interceptors.ZummaRequestInterceptor;
import com.zslide.utils.ZLog;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by chulwoo on 16. 5. 27..
 */
public class ApiClientFactory {

    public static final String TYPE_ZUMMA = "zumma";
    public static final String TYPE_NAVER = "naver";
    public static final String TYPE_FIREBASE = "firebase";

    public static OkHttpClient create(Context context, String type) {
        switch (type) {
            case TYPE_ZUMMA:
                return generate(new ZummaRequestInterceptor());
            case TYPE_NAVER:
                return generate(new NaverRequestInterceptor(context));
            case TYPE_FIREBASE:
                return generate(new FirebaseRequestInterceptor());
            default:
                throw new IllegalArgumentException(type + " does not supported.");
        }
    }

    private static OkHttpClient generate(Interceptor... interceptors) {
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();
        for (Interceptor interceptor : interceptors) {
            okClientBuilder.addInterceptor(interceptor);
        }
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new ZLog());
        httpLoggingInterceptor.setLevel(Config.DEBUG ?
                HttpLoggingInterceptor.Level.BODY :
                HttpLoggingInterceptor.Level.NONE);
        okClientBuilder.addInterceptor(httpLoggingInterceptor);
        /*final File baseDir = context.getCacheDir();
        if (baseDir != null) {
            final File cacheDir = new File(baseDir, "HttpResponseCache");
            okClientBuilder.cache(new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE));
        }*/
        //okClientBuilder.connectTimeout(ApiConstants.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        //okClientBuilder.readTimeout(ApiConstants.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        //okClientBuilder.writeTimeout(ApiConstants.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        return okClientBuilder.build();
    }
}
