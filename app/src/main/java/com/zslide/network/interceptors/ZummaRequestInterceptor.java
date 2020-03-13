package com.zslide.network.interceptors;

import com.zslide.data.AuthenticationManager;
import com.zslide.network.ApiConstants;
import com.zslide.utils.RequestSignature;
import com.zslide.utils.ZLog;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by chulwoo on 15. 8. 17..
 */

public class ZummaRequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        long timeStamp = System.currentTimeMillis() / 1000L;
        Request request = chain.request();
        Headers headers = request.headers().newBuilder()
                .add("x-app-agent", ApiConstants.USER_AGENT)
                .add("Authorization", AuthenticationManager.getInstance().getApiKey())
                .add("x-app-signature", RequestSignature.get(timeStamp))
                .add("x-app-timestamp", Long.toString(timeStamp))
                .build();
        request = request.newBuilder().headers(headers).build();
        try {
            return chain.proceed(request);
        } catch (Exception e) {
            ZLog.e(this, "onError: " + e + ", " + request.url());
            throw e;
        }
    }
}
