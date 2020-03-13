package com.zslide.network;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.zslide.R;
import com.zslide.ZummaApp;
import com.zslide.activities.BaseActivity;
import com.zslide.data.AuthenticationManager;
import com.zslide.dialogs.SessionExpiredDialog;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.models.ErrorResponse;
import com.zslide.utils.ZLog;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class ZummaApiErrorHandler {

    public static final int BAD_REQUEST_ERROR = 400;
    public static final int SESSION_EXPIRED_ERROR = 401;
    public static final int FORBIDDEN_ERROR = 403;
    public static final int NOT_FOUND_ERROR = 404;
    public static final int PRECONDITION_FAILED_ERROR = 412;
    public static final int METHOD_NOT_ALLOWED_ERROR = 405;
    public static final int NOT_EXCEPTABLE_ERROR = 406;
    public static final int INTERNAL_SERVER_ERROR = 500;
    private static Toast currentToast;

    public ZummaApiErrorHandler() {

    }

    public static void handleError(Throwable t) {
        if (t instanceof RetrofitException) {
            RetrofitException retrofitError = (RetrofitException) t;
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
        }

        ZLog.e(t);
    }

    public static void handleErrorWithoutToast(Throwable e) {
        // TODO
        ZLog.e(e);
    }

    protected static void showToast(@StringRes int resId) {
        Activity activity = ZummaApp.getCurrentActivity();
        Observable.just(resId)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(res -> activity != null)
                .map(res -> Toast.makeText(activity, res, Toast.LENGTH_SHORT))
                .subscribe(toast -> {
                    if (currentToast != null) {
                        currentToast.cancel();
                    }

                    currentToast = toast;
                    currentToast.show();
                });
    }

    protected static void handleNetworkError(RetrofitException e) {
        ZLog.e(ZummaApiErrorHandler.class, e, "network error: " + e.getMessage());
        showToast(R.string.message_check_network);
    }

    protected static void handleConversionError(RetrofitException e) {
        ZLog.e(ZummaApiErrorHandler.class, e, "conversion error: " + e.getMessage());
    }

    protected static void handleUnexpectedError(RetrofitException e) {
        ZLog.e(ZummaApiErrorHandler.class, e, "unexcepted error:" + e.getMessage());
        //throw new AssertionError("unexpected error");
    }

    private static void handleUnknownError(RetrofitException e) {
        showToast(R.string.message_failure_request);
        ZLog.e(ZummaApiErrorHandler.class, e, "unknown error kind: " + e.getKind());
        //throw new AssertionError("Unknown error kind: " + e.getKind());
    }

    private static void handleHttpError(RetrofitException cause) {
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
                ZLog.e(ZummaApiErrorHandler.class, e, "error message parse error");
            }

            switch (statusCode) {
                case SESSION_EXPIRED_ERROR:
                case FORBIDDEN_ERROR:
                    final String finalErrorMessage = errorMessage;
                    Observable.just(errorMessage)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(context -> expireSession(finalErrorMessage), ZLog::e);
                    break;
                default:
                    Observable.just(errorMessage)
                            .filter(message -> !TextUtils.isEmpty(message))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(ZummaApiErrorHandler::showErrorMessage, ZLog::e);
            }
        }
    }

    /**
     * API 요청에 대해 400 이상의 에러 응답이 오면 호출된다.
     * detail 내용을 다이얼로그에 보여준다.
     */
    private static void showErrorMessage(String errorMessage) {
        final Activity activity = ZummaApp.getCurrentActivity();
        ZLog.e(ZummaApiErrorHandler.class, "showErrorMessage:" + errorMessage);
        if (activity != null) {
            SimpleAlertDialog.newInstance(errorMessage)
                    .show(((AppCompatActivity) activity).getSupportFragmentManager(), "error_message_dialog");
        }
    }

    /**
     * API 요청에 대해 Session Expired(401), Forbidden(403) 등의 응답이 오면 호출된다.
     * 현재 사용자 정보를 제거(로그아웃)하고, {@link Context}가 {@link BaseActivity}를 확장한 뷰일 경우,
     * 세션 만료 다이얼로그를 보여준다.
     */
    private static void expireSession(String errorMessage) {
        AuthenticationManager.getInstance().localLogout().subscribe();
        Activity activity = ZummaApp.getCurrentActivity();
        if (activity != null) {
            if (activity instanceof BaseActivity) {
                showSessionExpiredDialog((BaseActivity) activity, errorMessage);
            } else if (activity instanceof com.zslide.view.base.BaseActivity) {
                ((com.zslide.view.base.BaseActivity) activity).showSessionExpiredDialog(errorMessage);
            }
        }
    }

    private static void showSessionExpiredDialog(BaseActivity activity, String message) {
        if (activity == null) {
            return;
        }

        FragmentManager fm = activity.getSupportFragmentManager();
        if (fm.findFragmentByTag("session_expired_dialog") == null) {
            SessionExpiredDialog.newInstance(message).show(fm, "session_expired_dialog");
        }
    }
}