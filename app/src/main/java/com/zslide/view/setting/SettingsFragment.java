package com.zslide.view.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.MetaDataManager;
import com.zslide.data.UserManager;
import com.zslide.utils.DLog;
import com.zslide.utils.RxUtil;
import com.zslide.view.base.BaseFragment;
import com.zslide.view.base.NavigatorFactory;
import com.zslide.view.editprofile.EditProfileContract;
import com.zslide.view.editprofile.EditProfileNavigator;
import com.zslide.view.editprofile.EditProfileViewModel;
import com.zslide.view.setting.adapter.SettingItemAdapter;
import com.zslide.view.setting.adapter.SettingItemDividerDecoration;
import com.zslide.view.setting.adapter.SettingItemSectionDecoration;
import com.zslide.view.setting.adapter.item.SettingItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.navi.NaviLifecycle;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by chulwoo on 2018. 1. 5..
 */

public class SettingsFragment extends BaseFragment {

    private final LifecycleProvider<ActivityEvent> provider
            = NaviLifecycle.createActivityLifecycleProvider(this);

    @BindDimen(R.dimen.spacing_large) int sectionSpacing;
    @BindDimen(R.dimen.setting_divider_spacing) int dividerSpacing;

    @BindView(R.id.thumbnail) ImageView thumbnailView;
    @BindView(R.id.nickname) TextView nicknameView;
    @BindView(R.id.level) TextView levelView;
    @BindView(R.id.settings) RecyclerView settingsView;

    private SettingItemAdapter adapter;
    private EditProfileContract.ViewModel editProfileViewModel;
    private SettingsContract.ViewModel settingsViewModel;
    private RecyclerView.ItemDecoration sectionDecoration;

    public static SettingsFragment newInstance() {

        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_settings;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSettingsView(settingsView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        editProfileViewModel = new EditProfileViewModel(new EditProfileNavigator(NavigatorFactory.create(this)));
        settingsViewModel = new SettingsViewModel(context, provider,
                UserManager.getInstance(), MetaDataManager.getInstance(),
                new SettingNavigator(NavigatorFactory.create(this)));
    }

    private void setupSettingsView(RecyclerView settingsView) {
        Context context = settingsView.getContext();
        adapter = new SettingItemAdapter(new ArrayList<>());
        settingsView.setNestedScrollingEnabled(false);
        settingsView.setLayoutManager(new LinearLayoutManager(context));
        settingsView.addItemDecoration(new SettingItemDividerDecoration(context, dividerSpacing));
        settingsView.setItemAnimator(null);
        settingsView.setAdapter(adapter);
    }

    @Override
    public void onResumeWithVisibleHint() {
        super.onResumeWithVisibleHint();
        settingsViewModel.refreshFamily()
                .compose(provider.bindToLifecycle())
                .subscribe(RxUtil::doNothing, DLog::e);
        settingsViewModel.getSettingUiModelObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.bindToLifecycle())
                .subscribe(this::updateUi, DLog::e);
        settingsViewModel.getSettingItemsObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.bindToLifecycle())
                .subscribe(this::updateItem, DLog::e);
    }

    private void updateUi(SettingUiModel model) {
        if (TextUtils.isEmpty(model.getUserProfileUrl())) {
            thumbnailView.setImageResource(R.drawable.ic_camera_upload);
        } else {
            Glide.with(this).load(
                    model.hasProfileImage() ? model.getUserProfileUrl() : R.drawable.ic_camera_upload)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                    .into(thumbnailView);
        }

        nicknameView.setText(model.getNickname());
        levelView.setText(model.getDisplayLevel());
    }

    private void updateItem(List<SettingItem> items) {
        this.adapter.setItems(items);
        this.adapter.notifyDataSetChanged();

        if (sectionDecoration == null) {
            List<Integer> sectionIndex = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).isSectionHeader()) {
                    sectionIndex.add(i);
                }
            }
            sectionDecoration = new SettingItemSectionDecoration(sectionSpacing, sectionIndex);
            settingsView.addItemDecoration(sectionDecoration);
        }
    }

    /*private void showLockerSnoozeDialog() {
        Fragment fragment = getFragmentManager().findFragmentByTag("snooze");
        if (fragment != null && fragment.isAdded()) {
            return;
        }

        LockerSnoozeDialog.newInstance().show(getFragmentManager(), "snooze");
    }*/

    @OnClick(R.id.userProfile)
    public void onUserProfileClick() {
        settingsViewModel.onUserProfileClick();
    }

    @OnClick(R.id.thumbnail)
    public void onThumbnailClick() {
        editProfileViewModel.onThumbnailClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!editProfileViewModel.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*@Override
    public void onSelectSnoozeSec(int snoozeSec) {
        if (snoozeSec == LockerSnoozeDialog.DISABLE_SEC) {
            settingsViewModel.disableLocker();
        } else {
            settingsViewModel.snooze(snoozeSec);
        }
    }

    @Override
    public void onCancel() {
    }*/
}