package com.zslide.data.remote.base;

import android.support.annotation.NonNull;

import com.zslide.network.RetrofitException;

import org.reactivestreams.Publisher;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {

    private final RxJava2CallAdapterFactory original;

    private RxErrorHandlingCallAdapterFactory() {
        original = RxJava2CallAdapterFactory.create();
    }

    public static CallAdapter.Factory create() {
        return new RxErrorHandlingCallAdapterFactory();
    }

    @Override
    public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);

        boolean isCompletable = rawType == Completable.class;
        boolean isObservable = rawType == Observable.class;
        boolean isFlowable = rawType == Flowable.class;
        boolean isSingle = rawType == Single.class;
        boolean isMaybe = rawType == Maybe.class;

        if (!isCompletable && !isObservable && !isFlowable && !isSingle && !isMaybe) {
            return null;
        } else {
            return new RxJava2CallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit),
                    isCompletable, isObservable, isFlowable, isSingle, isMaybe);
        }
    }

    private static class RxJava2CallAdapterWrapper<R> implements CallAdapter<R, Object> {
        private final Retrofit retrofit;
        private final CallAdapter<R, Object> wrapped;
        private final boolean isCompletable;
        private final boolean isObservable;
        private final boolean isFlowable;
        private final boolean isSingle;
        private final boolean isMaybe;

        public RxJava2CallAdapterWrapper(Retrofit retrofit, CallAdapter<R, Object> wrapped,
                                         boolean isCompletable, boolean isObservable, boolean isFlowable, boolean isSingle, boolean isMaybe) {
            this.retrofit = retrofit;
            this.wrapped = wrapped;
            this.isCompletable = isCompletable;
            this.isObservable = isObservable;
            this.isFlowable = isFlowable;
            this.isSingle = isSingle;
            this.isMaybe = isMaybe;
        }

        @Override
        public Type responseType() {
            return wrapped.responseType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object adapt(@NonNull Call<R> call) {
            if (isCompletable) {
                return ((Completable) wrapped.adapt(call)).onErrorResumeNext(new Function<Throwable, CompletableSource>() {
                    @Override
                    public CompletableSource apply(Throwable throwable) throws Exception {
                        return Completable.error(asRetrofitException(throwable));
                    }
                });
            } else if (isObservable) {
                return ((Observable) wrapped.adapt(call)).onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                    @Override
                    public ObservableSource apply(Throwable throwable) throws Exception {
                        return Observable.error(asRetrofitException(throwable));
                    }
                });
            } else if (isFlowable) {
                return ((Flowable) wrapped.adapt(call)).onErrorResumeNext(new Function<Throwable, Publisher>() {
                    @Override
                    public Publisher apply(Throwable throwable) throws Exception {
                        return Flowable.error(asRetrofitException(throwable));
                    }
                });
            } else if (isSingle) {
                return ((Single) wrapped.adapt(call)).onErrorResumeNext(new Function<Throwable, SingleSource>() {
                    @Override
                    public SingleSource apply(Throwable throwable) throws Exception {
                        return Single.error(asRetrofitException(throwable));
                    }
                });
            } else if (isMaybe) {
                return ((Maybe) wrapped.adapt(call)).onErrorResumeNext(new Function<Throwable, MaybeSource>() {
                    @Override
                    public MaybeSource apply(Throwable throwable) throws Exception {
                        return Maybe.error(asRetrofitException(throwable));
                    }
                });
            } else {
                return null;
            }
        }

        private RetrofitException asRetrofitException(Throwable throwable) {
            // We had non-200 http error
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                Response response = httpException.response();
                return RetrofitException.httpError(response.raw().request().url().toString(), response, retrofit);
            }
            // A network error happened
            if (throwable instanceof IOException) {
                return RetrofitException.networkError((IOException) throwable);
            }

            // We don't know what happened. We need to simply convert to an unknown error

            return RetrofitException.unexpectedError(throwable);
        }
    }
}