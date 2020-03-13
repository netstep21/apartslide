package com.zslide.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zslide.R;

import butterknife.OnClick;

/**
 * Created by chulwoo on 15. 8. 25..
 */
public class AdPartnerHelpFragment extends BaseFragment {

    public static AdPartnerHelpFragment newInstance() {
        return new AdPartnerHelpFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_ad_help;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {

    }

    @OnClick(R.id.help)
    public void sendEmail() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("plain/text");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ad@mobitle.com"});
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_ad_help_mail_subject));
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_ad_help_mail_content));
        startActivity(sendIntent);
    }

    @OnClick(R.id.closeButton)
    public void close() {
        getActivity().finish();
    }
}