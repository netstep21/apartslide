package com.zslide.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.activities.InviteActivity;
import com.zslide.activities.ZmoneyActivity;
import com.zslide.data.UserManager;
import com.zslide.data.model.HomeZmoney;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 2017. 9. 8..
 */
public class LockerZmoneyDialog extends DialogFragment {

    @BindView(R.id.month) TextView monthView;
    @BindView(R.id.zmoney) TextView zmoneyView;
    @BindView(R.id.close) ImageView closeView;

    public static LockerZmoneyDialog newInstance() {
        return new LockerZmoneyDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_locker_zmoney, null, false);
        ButterKnife.bind(this, view);

        HomeZmoney homeZmoney = UserManager.getInstance().getZmoneyValue();
        int zmoney = homeZmoney.getFamilyZmoney() == 0 ? homeZmoney.getUserZmoney() : homeZmoney.getFamilyZmoney();

        zmoneyView.setText(getString(R.string.format_price, zmoney));
        ImageView img = ButterKnife.findById(view, R.id.img);
        ZummaApi.general().staticImage("friend_invite_lock")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageGuide -> Glide.with(getActivity())
                        .load(imageGuide.getUrl())
                        .dontTransform()
                        .thumbnail(0.3f)
                        .into(img), ZummaApiErrorHandler::handleError);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        return builder.create();
    }

    @OnClick(R.id.zmoney)
    public void showZmoney() {
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getActivity());
        Intent intent = new Intent(getActivity(), com.zslide.activities.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        taskStackBuilder.addNextIntent(intent);
        intent = new Intent(getActivity(), ZmoneyActivity.class);
        intent.putExtra(IntentConstants.EXTRA_PAGE, ZmoneyActivity.PAGE_THIS_MONTH);
        taskStackBuilder.addNextIntent(intent);
        taskStackBuilder.startActivities();
    }

    @OnClick(R.id.close)
    @Override
    public void dismiss() {
        super.dismiss();
    }


    @OnClick(R.id.more)
    public void showInvite() {
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getActivity());
        Intent intent = new Intent(getActivity(), com.zslide.activities.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        taskStackBuilder.addNextIntent(intent);
        intent = new Intent(getActivity(), InviteActivity.class);
        taskStackBuilder.addNextIntent(intent);
        taskStackBuilder.startActivities();
    }
}
