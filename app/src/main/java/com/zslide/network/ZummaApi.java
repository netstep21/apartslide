package com.zslide.network;

import android.content.Context;

import com.zslide.utils.GsonUtil;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chulwoo on 15. 7. 1..
 * <p>
 * 줌머니 API 관련 메소드를 제공한다.
 * 어플리케이션 내의 모든 API는 이 클래스를 통해 호출된다.
 * <p>
 * TODO: 메소드 명 서버와 별개로 실제 동작과 연관성 있게 변경할 것
 * TODO: context 없어도 됨
 */
@Deprecated
public class ZummaApi {

    private volatile static ZummaApi instance;
    private final AddressApi addressApi;
    private final GeneralApi generalApi;
    private final NoticeApi noticeApi;
    private final EventApi eventApi;
    private final UserApi userApi;
    private final OCBApi ocbApi;
    private final ZmoneyApi zmoneyApi;
    private final ZummaShoppingApi zummaShoppingApi;

    private ZummaApi(Context context) {
        Retrofit retrofit = buildRetrofit(context.getApplicationContext());
        generalApi = new GeneralApi(retrofit);
        userApi = new UserApi(retrofit);
        addressApi = new AddressApi(retrofit);
        noticeApi = new NoticeApi(retrofit);
        eventApi = new EventApi(retrofit);
        ocbApi = new OCBApi(retrofit);
        zmoneyApi = new ZmoneyApi(retrofit);
        zummaShoppingApi = new ZummaShoppingApi(retrofit);
    }

    public static void initialize(Context context) {
        instance = new ZummaApi(context.getApplicationContext());
    }

    /**
     * {@link ZummaApi}의 인스턴스를 가져온다.
     *
     * @return 인스턴스
     */
    private static ZummaApi getInstance() {
        if (instance == null) {
            synchronized (ZummaApi.class) {
                if (instance == null) {
                    throw new IllegalStateException("ZummaApi not initialized");
                }
            }
        }

        return instance;
    }

    public static GeneralApi general() {
        return getInstance().generalApi;
    }

    public static UserApi user() {
        return getInstance().userApi;
    }

    public static AddressApi address() {
        return getInstance().addressApi;
    }

    public static NoticeApi notice() {
        return getInstance().noticeApi;
    }

    public static EventApi event() {
        return getInstance().eventApi;
    }

    public static OCBApi ocb() {
        return getInstance().ocbApi;
    }

    public static ZmoneyApi zmoney() {
        return getInstance().zmoneyApi;
    }

    public static ZummaShoppingApi shopping() { return getInstance().zummaShoppingApi; }

    private Retrofit buildRetrofit(Context context) {
        return new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_API_URL)
                .client(ApiClientFactory.create(context, ApiClientFactory.TYPE_ZUMMA))
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.customGson()))
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }
}
