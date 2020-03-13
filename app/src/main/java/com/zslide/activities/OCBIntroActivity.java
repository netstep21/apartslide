package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.widget.RequestButton;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by chulwoo on 16. 6. 27..
 */
public class OCBIntroActivity extends BaseActivity {

    @BindView(R.id.usedPoint) TextView usedPointView;
    @BindView(R.id.cancel) RequestButton cancelButton;
    @BindView(R.id.alert) ImageView alertView;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Glide.with(this).load("http://zummaslide.com/media/images/staticimage/img-ocb-memo.png")
                .into(alertView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncUsedPoint();
    }

    protected void syncUsedPoint() {
        showTitleProgress();
        subscriptions.add(ZummaApi.ocb().usedPoint()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ocbPoint -> {
                    hideTitleProgress();
                    setUsedPoint(ocbPoint.getPoint());
                }, e -> {
                    hideTitleProgress();
                    ZummaApiErrorHandler.handleError(e);
                }));
    }

    public void setUsedPoint(int point) {
        usedPointView.setText(getString(R.string.format_ocb, point));
    }

    @Override
    public String getScreenName() {
        return "OK캐쉬백";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_ocb_intro;
    }

    @OnClick(R.id.use)
    public void use() {
        Navigator.startOCBActivity(this);
    }

    @OnClick(R.id.cancel)
    public void cancel() {
        showOCBCancelConfirmDialog(cancelButton);
    }

    private void showOCBCancelConfirmDialog(RequestButton button) {
        SimpleAlertDialog.newInstance("포인트 사용을 취소하시겠어요?", true)
                .setOnConfirmListener(() -> {
                    button.setProgressing(true);
                    subscriptions.add(ZummaApi.ocb().cancel()
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(ocb -> {
                                button.setProgressing(false);
                                if (ocb.isSuccess()) {
                                    Toast.makeText(this, "포인트 사용이 취소됐습니다.", Toast.LENGTH_SHORT).show();
                                    syncUsedPoint();
                                } else {
                                    Toast.makeText(this, ocb.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }, e -> {
                                button.setProgressing(false);
                                ZummaApiErrorHandler.handleError(e);
                            }));
                })
                .show(getSupportFragmentManager(), "confirm");
    }

    @Override
    protected void onDestroy() {
        subscriptions.unsubscribe();
        super.onDestroy();
    }
}
