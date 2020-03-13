package com.zslide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.zslide.IntentConstants;
import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 2017. 9. 11..
 */

public class ImageGuideFragment extends BaseFragment {

    @BindView(R.id.progress) CircularProgressBar progressView;
    @BindView(R.id.guide) ImageView guideView;

    private String target;

    public static ImageGuideFragment newInstance(String target) {

        Bundle args = new Bundle();
        args.putString(IntentConstants.EXTRA_TARGET, target);

        ImageGuideFragment fragment = new ImageGuideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            target = getArguments().getString(IntentConstants.EXTRA_TARGET);
        }
    }

    public void load(String target) {
        progressView.setVisibility(View.VISIBLE);
        ZummaApi.general().staticImage(target)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> progressView.setVisibility(View.GONE))
                .subscribe(imageGuide -> Glide.with(this)
                        .load(imageGuide.getUrl())
                        .dontTransform()
                        .thumbnail(0.3f)
                        .into(guideView), ZummaApiErrorHandler::handleError);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_image_guide;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        load(target);
    }

    @OnClick(R.id.help)
    public void help() {
        Navigator.startPersonalHelp(getActivity());
    }
}

