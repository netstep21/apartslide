package com.zslide.network;

import com.zslide.BuildConfig;
import com.zslide.Config;

/**
 * Created by chulwoo on 15. 12. 24..
 * <p>
 * API 관련 클래스에서 사용하는 값들
 */
public class ApiConstants {

    public static final String INVITE_SHORT_LINK = "https://sx3z6.app.goo.gl/ajBE";
    /**
     * HTTP 연결이 6초 이상 완료되지 않을 경우 타임아웃
     */
    public static final int HTTP_CONNECT_TIMEOUT = 6000;
    /**
     * HTTP 데이터를 읽는 시간이 10초 이상 완료되지 않을 경우 타임아웃
     */
    public static final int HTTP_READ_TIMEOUT = 10000;
    /**
     * 데이터 전송하는 시간을 제한하지 않음
     */
    public static final int HTTP_WRITE_TIMEOUT = Integer.MAX_VALUE;
    /**
     * 어플리케이션의 버전 정보를 구별하기 위한 UserAgent 헤더
     */
    //private static final String DEV_URL = "http://172.30.1.26:8000/";
    private static final String RELEASE_URL = "https://zummaslide.com/";
    private static final String DEV_URL = "http://zummaslide.com/";
    public static final String BASE_URL = (Config.DEBUG) ? DEV_URL : RELEASE_URL;
    //private static final String DEV_URL = "http://staging.zummaslide.com/"; // 스테이징 서버
    public static final String USER_AGENT = "ZummaSlideClient/" + BuildConfig.VERSION_CODE;
    public static final String BASE_API_URL = BASE_URL + "api/";
    public static final String BASE_STATIC_IMAGE_URL = BASE_URL + "media/images/staticimage";
}
