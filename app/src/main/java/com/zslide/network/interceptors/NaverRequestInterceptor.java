package com.zslide.network.interceptors;

import android.content.Context;

import com.nhn.android.naverlogin.OAuthLogin;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NaverRequestInterceptor implements Interceptor {

    private final Context context;

    public NaverRequestInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        OAuthLogin oauthLoginModule = OAuthLogin.getInstance();
        String accessToken = oauthLoginModule.getAccessToken(context);
        Request request = chain.request();
        Headers headers = request.headers().newBuilder()
                .add("Authorization", "Bearer " + accessToken)
                .build();
        request = request.newBuilder().headers(headers).build();
        return chain.proceed(request);
    }
}

