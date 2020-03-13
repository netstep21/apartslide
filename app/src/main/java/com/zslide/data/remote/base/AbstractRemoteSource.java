package com.zslide.data.remote.base;

import com.zslide.Config;
import com.zslide.network.ApiConstants;
import com.zslide.network.interceptors.ZummaRequestInterceptor;
import com.zslide.utils.DLog;
import com.zslide.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chulwoo on 2017. 12. 29..
 */

public abstract class AbstractRemoteSource {

    protected <T> T create(final Class<T> service) {
        return createRetrofit().create(service);
    }

    protected List<Interceptor> getInterceptors() {
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.add(new ZummaRequestInterceptor());
        return interceptors;
    }

    protected Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.customGson()))
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(createOkHttpClient(getInterceptors()))
                .build();
    }

    protected OkHttpClient createOkHttpClient(List<Interceptor> interceptors) {
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();
        for (Interceptor interceptor : interceptors) {
            okClientBuilder.addInterceptor(interceptor);
        }
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new DLog());
        httpLoggingInterceptor.setLevel(Config.DEBUG ?
                HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        okClientBuilder.addInterceptor(httpLoggingInterceptor);
        return okClientBuilder.build();
    }


}