package com.zslide.data;

/**
 * Created by chulwoo on 2018. 1. 2..
 */

public interface ErrorHandler {

    /**
     *
     * @param throwable 발생한 에러를 이 파라미터로 전달합니다
     * @return 해당 에러가 이 메소드에서 처리됐을 경우 true를 반환합니다.
     */
    boolean handleError(Throwable throwable);
}
