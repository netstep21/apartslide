package com.zslide.data.remote.exception;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import com.zslide.R;
import com.zslide.data.AuthenticationManager;
import com.zslide.data.ErrorHandler;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.models.ErrorResponse;
import com.zslide.network.RetrofitException;
import com.zslide.utils.DLog;
import com.zslide.utils.RxUtil;
import com.zslide.view.auth.AuthActivity;
import com.zslide.view.base.BaseActivity;

import java.lang.ref.WeakReference;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by chulwoo on 2017. 12. 29..
 */

public class RemoteErrorHandler implements ErrorHandler {

    public static final int BAD_REQUEST_ERROR = 400;
    public static final int SESSION_EXPIRED_ERROR = 401;
    public static final int FORBIDDEN_ERROR = 403;
    public static final int NOT_FOUND_ERROR = 404;
    public static final int PRECONDITION_FAILED_ERROR = 412;
    public static final int METHOD_NOT_ALLOWED_ERROR = 405;
    public static final int NOT_EXCEPTABLE_ERROR = 406;
    public static final int INTERNAL_SERVER_ERROR = 500;

    private static Toast currentToast;

    private final WeakReference<BaseActivity> activityRef;

    public RemoteErrorHandler(BaseActivity activity) {
        activityRef = new WeakReference<>(activity);
    }

    @Override
    public boolean handleError(Throwable throwable) {
        if (throwable instanceof RetrofitException) {
            RetrofitException retrofitError = (RetrofitException) throwable;
            switch (retrofitError.getKind()) {
                case HTTP:
                    handleHttpError(retrofitError);
                    break;
                case NETWORK:
                    handleNetworkError(retrofitError);
                    break;
                case UNEXPECTED:
                    handleUnexpectedError(retrofitError);
                    break;
                default:
                    handleUnknownError(retrofitError);
            }

            return true;
        }

        DLog.e(throwable);
        return false;
    }

    public void handleErrorWithoutToast(Throwable e) {
        // TODO
        DLog.e(e);
    }

    protected void showToast(@StringRes int resId) {
        BaseActivity activity = activityRef.get();
        if (activity == null) {
            return;
        }

        Single.just(resId)
                .observeOn(AndroidSchedulers.mainThread())
                .map(res -> Toast.makeText(activity, res, Toast.LENGTH_SHORT))
                .subscribe(toast -> {
                    if (currentToast != null) {
                        currentToast.cancel();
                    }

                    currentToast = toast;
                    currentToast.show();
                });
    }

    protected void handleNetworkError(RetrofitException e) {
        DLog.e(RemoteErrorHandler.class, e, "network error: " + e.getMessage());
        showToast(R.string.message_check_network);
    }

    protected void handleConversionError(RetrofitException e) {
        DLog.e(RemoteErrorHandler.class, e, "conversion error: " + e.getMessage());
    }

    protected void handleUnexpectedError(RetrofitException e) {
        DLog.e(RemoteErrorHandler.class, e, "unexcepted error:" + e.getMessage());
        //throw new AssertionError("unexpected error");
    }

    private void handleUnknownError(RetrofitException e) {
        showToast(R.string.message_failure_request);
        DLog.e(RemoteErrorHandler.class, e, "unknown error kind: " + e.getKind());
        //throw new AssertionError("Unknown error kind: " + e.getKind());
    }

    private void handleHttpError(RetrofitException cause) {
        Response response = cause.getResponse();
        int statusCode = response.code();

        if (statusCode >= INTERNAL_SERVER_ERROR) {
            showToast(R.string.message_failure_request);
        } else if (statusCode >= BAD_REQUEST_ERROR && statusCode != NOT_FOUND_ERROR) {
            String errorMessage = "";
            try {
                errorMessage = cause.getErrorBodyAs(ErrorResponse.class).detail;
            } catch (Exception e) {
                // Do nothings
                DLog.e(RemoteErrorHandler.class, e, "error message parse error");
            }

            switch (statusCode) {
                case SESSION_EXPIRED_ERROR:
                case FORBIDDEN_ERROR:
                    Single.just(errorMessage)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::expireSession, DLog::e);
                    break;
                default:
                    Single.just(errorMessage)
                            .filter(message -> !TextUtils.isEmpty(message))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::showErrorMessage, DLog::e);
            }
        }
    }

    /**
     * API 요청에 대해 400 이상의 에러 응답이 오면 호출된다.
     * detail 내용을 다이얼로그에 보여준다.
     */
    private void showErrorMessage(String errorMessage) {
        DLog.e(RemoteErrorHandler.class, "showErrorMessage:" + errorMessage);
        if (activityRef.get() != null) {
            SimpleAlertDialog.newInstance(errorMessage)
                    .show(activityRef.get().getSupportFragmentManager(), "error_message_dialog");
        }
    }

    /**
     * API 요청에 대해 Session Expired(401), Forbidden(403) 등의 응답이 오면 호출된다.
     * 현재 사용자 정보를 제거(로그아웃)하고, {@link Context}가 {@link BaseActivity}를 확장한 뷰일 경우,
     * 세션 만료 다이얼로그를 보여준다.
     */
    private void expireSession(String errorMessage) {
        AuthenticationManager.getInstance().localLogout()
                .subscribeOn(Schedulers.io())
                .subscribe(RxUtil::doNothing, DLog::e);

        BaseActivity activity = activityRef.get();
        if (activityRef.get() == null) {
            return;
        }

        if (!(activity instanceof AuthActivity)) {
            activity.showSessionExpiredDialog(errorMessage);
        }
    }
}
