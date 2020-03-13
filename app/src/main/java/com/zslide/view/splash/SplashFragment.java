package com.zslide.view.splash;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.zslide.R;
import com.zslide.data.MetaDataManager;
import com.zslide.data.UserManager;
import com.zslide.data.model.AlertMessage;
import com.zslide.utils.DLog;
import com.zslide.utils.RxUtil;
import com.zslide.view.base.PermissionContract;
import com.zslide.view.base.PermissionGrantFragment;
import com.zslide.view.base.PermissionViewModel;
import com.zslide.view.splash.exception.GoogleApiAvailabilityException;
import com.google.android.gms.common.GoogleApiAvailability;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public class SplashFragment extends PermissionGrantFragment implements AlertMessageDialog.Callback {

    interface Callback {
        void showUpdateDialog(AlertMessage alertMessage);

        void showGoogleApiAvailabilityErrorDialog(GoogleApiAvailability apiAvailability);
    }

    public static SplashFragment newInstance() {

        Bundle args = new Bundle();

        SplashFragment fragment = new SplashFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Callback callback;
    private Disposable initializeDisposable;
    private ISplashViewModel viewModel;
    private PermissionViewModel permissionViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new SplashViewModel(this,
                UserManager.getInstance(),
                MetaDataManager.getInstance(),
                new SplashNavigator((AppCompatActivity) getActivity()));
        permissionViewModel = (PermissionViewModel) viewModel;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_splash;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (Callback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement Callback");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize(true);
    }

    private void initialize(boolean checkUpdate) {
        if (initializeDisposable != null && !initializeDisposable.isDisposed()) {
            initializeDisposable.dispose();
        }

        initializeDisposable = viewModel.initialize(checkUpdate).subscribe(this::onSuccessInitialize, this::onFailureInitialize);

        bind(initializeDisposable);
        bind(viewModel.getAlertMessageObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(alertMessage -> {
                    if (alertMessage.isNeedShowingMessage(getActivity())) {
                        callback.showUpdateDialog(alertMessage);
                    } else {
                        viewModel.onDisposeAlertMessage();
                    }
                }));
    }

    private void onSuccessInitialize() {
    }

    private void onFailureInitialize(Throwable throwable) {
        if (throwable instanceof IllegalStateException) {
            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        } else if (throwable instanceof GoogleApiAvailabilityException) {
            GoogleApiAvailability apiAvailability = ((GoogleApiAvailabilityException) throwable).getApiAvailability();
            int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
            if (apiAvailability.isUserResolvableError(resultCode)) {
                callback.showGoogleApiAvailabilityErrorDialog(apiAvailability);
            } else {
                DLog.i(SplashViewModel.class, "지원하지 않는 디바이스입니다.");
                finish();
            }
        } else {
            DLog.e(throwable);
            Toast.makeText(getActivity(), R.string.message_failure_initialize, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    public void finish() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onConfirm(String action) {
        bind(viewModel.update(action).subscribe(RxUtil::doNothing, DLog::e));
        viewModel.onDisposeAlertMessage();
    }

    @Override
    public void onCancel() {
        viewModel.onDisposeAlertMessage();
        //initialize(false);
    }

    @Override
    public PermissionContract.ViewModel getPermissionViewModel() {
        return permissionViewModel;
    }
}