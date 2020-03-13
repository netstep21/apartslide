package com.zslide.network;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.zslide.R;
import com.zslide.ZummaApp;
import com.zslide.models.NaverUserProfileWrapper;
import com.zslide.network.service.NaverService;
import com.zslide.utils.GsonUtil;
import com.google.gson.annotations.SerializedName;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by chulwoo on 15. 8. 13..
 */
public class NaverApi {
    public static final String BASE_URL = "https://openapi.naver.com/";
    private final NaverService naverService;

    public NaverApi(Context context) {
        naverService = buildRetrofit(context.getApplicationContext()).create(NaverService.class);
    }

    private Retrofit buildRetrofit(Context context) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(ApiClientFactory.create(context, ApiClientFactory.TYPE_NAVER))
                // .setErrorHandler(new NaverErrorHandler())
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.customGson()))
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public Observable<NaverUserProfileWrapper.NaverUserProfile> getProfile() {
        return naverService.getProfile().map(NaverUserProfileWrapper::getProfile);
    }

    public static class ErrorHandler {

        public static Throwable handleError(Throwable e) {
            RetrofitException cause;
            if (e instanceof RetrofitException) {
                cause = (RetrofitException) e;
                switch (cause.getKind()) {
                    case HTTP:
                        handleHttpError(cause);
                        break;
                    case NETWORK:
                        handleNetworkError(cause);
                        break;
                }
            }

            return e;
        }

        protected static void handleNetworkError(RetrofitException cause) {
            ZummaApiErrorHandler.showToast(R.string.message_failure_network);
        }

        protected static void handleHttpError(RetrofitException cause) {
            Response response = cause.getResponse();
            int statusCode = response.code();

            if (statusCode >= 400) {
                NaverApiError naverApiError;
                try {
                    naverApiError = cause.getErrorBodyAs(NaverApiError.class);
                    AppCompatActivity activity = ZummaApp.getCurrentActivity();
                    if (activity != null) {
                        Toast.makeText(activity, naverApiError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // Do nothings
                    AppCompatActivity activity = ZummaApp.getCurrentActivity();
                    if (activity != null) {
                        Toast.makeText(activity, R.string.message_failure_network, Toast.LENGTH_SHORT).show();
                    }
                }

                // TODO: error handling
            }
        }
    }

    public static class NaverApiError {
        @SerializedName("errorCode") String code;
        @SerializedName("errorMessage") String message;

        public NaverApiError(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
