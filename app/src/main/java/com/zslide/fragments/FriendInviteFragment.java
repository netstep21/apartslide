package com.zslide.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.R;
import com.zslide.data.model.InviteInfo;
import com.zslide.network.ApiConstants;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.EventLogger;
import com.bumptech.glide.Glide;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.annotations.Nullable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by chulwoo on 15. 11. 26..
 */
public class FriendInviteFragment extends InviteFragment {

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.image) ImageView imageView;
    @BindView(R.id.recommendCode) TextView recommendCodeView;
    @BindView(R.id.inviteCount) TextView inviteCountView;

    @BindColor(R.color.atlantis) int ACCENT_COLOR;

    private InviteInfo inviteInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callback = new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                //Toast.makeText(getApplicationContext(), errorResult.getErrorMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                EventLogger.invite(getActivity(), "family", "카카오톡");
                //Toast.makeText(getApplicationContext(), "Successfully sent KakaoLink v2 message.", Toast.LENGTH_LONG).show();
            }
        };

    }

    public static FriendInviteFragment newInstance() {

        Bundle args = new Bundle();

        FriendInviteFragment fragment = new FriendInviteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setInviteInfo(InviteInfo inviteInfo) {
        this.inviteInfo = inviteInfo;
        recommendCodeView.setText(inviteInfo.getInviteCode());
        String count = getString(R.string.format_persons, inviteInfo.getCount());
        SpannableString spanCount = new SpannableString(count);
        spanCount.setSpan(new StyleSpan(Typeface.BOLD), 0, count.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        inviteCountView.setText(spanCount);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_friend_invite;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            loadInviteImage();
        }
    }

    private void loadInviteImage() {
        ZummaApi.general().staticImage("friend_invite")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> progressBar.setVisibility(View.GONE))
                .subscribe(imageGuide -> Glide.with(this)
                        .load(imageGuide.getUrl())
                        .dontTransform()
                        .thumbnail(0.3f)
                        .into(imageView), ZummaApiErrorHandler::handleError);
    }

    @OnClick({R.id.inviteKakaotalkButton, R.id.inviteSmsButton, R.id.inviteTextCopyButton})
    public void invite(View view) {
        if (inviteInfo == null) {
            return;
        }

        int id = view.getId();
        String category = "";
        switch (id) {
            case R.id.inviteKakaotalkButton:
                inviteViaKakaotalk(inviteInfo);
                category = "카카오톡";
                break;
            case R.id.inviteSmsButton:
                inviteViaSms(inviteInfo);
                category = "SMS";
                break;
            case R.id.inviteTextCopyButton:
                inviteViaClipboard(inviteInfo);
                category = "문구 복사";
                break;
        }

        EventLogger.invite(getActivity(), "friend", category);
    }

    public void inviteViaKakaotalk(InviteInfo inviteInfo) {
        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("줌머니 친구초대",
                        ApiConstants.BASE_URL + "/media/invite_logo.png",
                        LinkObject.newBuilder().setWebUrl("https://zummaslide.com")
                                .setMobileWebUrl("https://zummaslide.com").build())
                        .setDescrption(getString(R.string.invite_friend_message, inviteInfo.getMessage(), inviteInfo.getInviteCode()))
                        .build())
                .addButton(new ButtonObject("친구따라 설치하기", LinkObject.newBuilder()
                        .setWebUrl("http://zummaslide.com/app/main?utm_campaign=%EC%A4%8C%EB%A7%88%EC%8A%AC%EB%9D%BC%EC%9D%B4%EB%93%9C+%EA%B0%80%EC%A1%B1%EC%B4%88%EB%8C%80&utm_medium=app&utm_source=zummaslidetps://play.google.com/store/apps/details?id=com.mobitle.zummoney")
                        .setMobileWebUrl("https://play.google.com/store/apps/details?id=com.mobitle.zummoney")
                        .setAndroidExecutionParams("key1=value1")
                        .setIosExecutionParams("key1=value1")
                        .build()))
                .build();
        KakaoLinkService.getInstance().sendDefault(getApplicationContext(), params, callback);
    }

    public void inviteViaSms(InviteInfo inviteInfo) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", getString(R.string.invite_friend_message_with_link, inviteInfo.getMessage(), inviteInfo.getInviteCode()));
        startActivity(intent);
    }

    public void inviteViaClipboard(InviteInfo inviteInfo) {
        Context context = getActivity();
        String msg = getString(R.string.invite_friend_message_with_link, inviteInfo.getMessage(), inviteInfo.getInviteCode());
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("invite", msg));
        Toast.makeText(context, R.string.message_copy, Toast.LENGTH_SHORT).show();
    }

    private ResponseCallback<KakaoLinkResponse> callback;
}
