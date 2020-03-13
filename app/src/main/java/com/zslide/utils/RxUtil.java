package com.zslide.utils;

import android.support.design.BuildConfig;
import android.util.Log;

import com.zslide.network.RetrofitException;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by chulwoo on 15. 8. 5..
 * <p>
 * Updated by chulwoo on 16. 1. 14..
 * <p>
 * HTTP 에러가 발생한 경우 retry를 하지 않도록 변경함
 */
public class RxUtil {

    public static final String TAG = RxUtil.class.getSimpleName();

    public static Observable<?> exponentialBackoff(Observable<? extends Throwable> attempts) {
        return exponentialBackoff(3, attempts);
    }

    public static Observable<?> exponentialBackoffInfinite(Observable<? extends Throwable> attempts) {
        return new RetryWithExponentialDelay(Integer.MAX_VALUE).call(attempts);
    }

    public static Observable<?> exponentialBackoff(int retry, Observable<? extends Throwable> attempts) {
        return new RetryWithExponentialDelay(retry).call(attempts);
    }

    public static void doNothing() {
        //do nothing
    }

    public static void doNothing(Object object) {
        //do nothing
    }

    static class RetryWithExponentialDelay
            implements Func1<Observable<? extends Throwable>, Observable<?>> {

        private final int mRetryCount;
        private int mCurrentRetryCount;

        public RetryWithExponentialDelay(final int retryCount) {
            this.mRetryCount = retryCount;
            this.mCurrentRetryCount = 0;
        }

        @Override
        public Observable<?> call(Observable<? extends Throwable> attempts) {
            return attempts.flatMap(throwable -> {
                // HTTP 에러가 발생한 경우 더이상 요청을 반복할 필요가 없다.
                if (throwable instanceof RetrofitException) {
                    if (((RetrofitException) throwable).getKind() == RetrofitException.Kind.HTTP) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "Occurred HTTP error. Stop exponential backoff");
                        }
                        mCurrentRetryCount = mRetryCount;
                    }
                }

                if (++mCurrentRetryCount <= mRetryCount) {
                    return Observable.timer(
                            (int) Math.pow(2, mCurrentRetryCount), TimeUnit.SECONDS);
                }

                return Observable.error(throwable);
            });
        }
    }
}
